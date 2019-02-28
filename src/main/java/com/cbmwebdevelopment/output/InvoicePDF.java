/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.output;

import com.cbmwebdevelopment.customers.Customers;
import com.cbmwebdevelopment.invoices.InvoiceData;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author cmeehan
 */
public class InvoicePDF {

    private Cell cell = new Cell();
    private double itemsTotal = 0;
    private final InvoiceData INVOICE_DATA;
    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.US);

    public InvoicePDF(InvoiceData data) {
        this.INVOICE_DATA = data;
    }
    
    public void savePdf() {
        FileChooser fc = new FileChooser();
        fc.setInitialFileName("MWW Invoice " + INVOICE_DATA.getId() + " " + formatter.format(new Date()) + ".pdf");
        File file = fc.showSaveDialog(new Stage());

        createPdf(file.toString());
    }

    private void createPdf(String dest) {
        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            pdf.addNewPage();

            // Watermark
            PdfExtGState gs1 = new PdfExtGState();
            for (int i = 1; i <= pdf.getNumberOfPages(); i++) {
                PdfPage pdfPage = pdf.getPage(i);

                Rectangle pageSize = pdfPage.getPageSizeWithRotation();
                pdfPage.setIgnorePageRotationForContent(true);

                float x = pageSize.getWidth() / 2;
                float y = pageSize.getHeight() / 2;

                PdfCanvas waterMark = new PdfCanvas(pdf.getPage(i));
                waterMark.saveState();
                waterMark.setExtGState(gs1);

                document.showTextAligned(waterMark(), x, y, i, TextAlignment.CENTER, VerticalAlignment.TOP, 45);

                waterMark.restoreState();
            }

            float width = (float) (pdf.getPage(1).getPageSize().getWidth() / 2) - (headerImage().getImageScaledWidth() / 2);
            float height = pdf.getPage(1).getPageSize().getHeight() - 75;
            document.add(headerImage().setFixedPosition(width, height));
            document.add(headerText().setMarginTop(20));
            document.add(billTo());
            document.add(quoteData());
            
            System.out.println(INVOICE_DATA.getMemo());
            
            String memo = INVOICE_DATA.getMemo() == null ? "" : INVOICE_DATA.getMemo();
            
            if(!memo.isEmpty()){
                document.add(memoData());
            }
            document.close();
            pdf.close();
            writer.close();
        } catch (IOException | ParseException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private Paragraph waterMark() {
        String status = INVOICE_DATA.getStatus() == null ? "" : INVOICE_DATA.getStatus();
        Paragraph paragraph = new Paragraph(status).setFontColor(Color.DARK_GRAY).setFontSize(36f).setOpacity(0.5f);

        return paragraph;
    }

    private Image headerImage() {
        Image image = new Image(ImageDataFactory.create(getClass().getResource("/images/Logo.png")));
        image.scaleToFit(75, 75);
        return image;
    }

    private Table headerText() {
        Table table = new Table(2);
        table.setWidthPercent(66);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        cell = new Cell(1, 2).add(new Paragraph("\nMeehan Wood Working"));
        cell.setTextAlignment(TextAlignment.CENTER);
        cell.setBorder(Border.NO_BORDER);
        cell.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("320 Burlington Ave.\nGibsonville, NC 27249\n(336) 260-0061").setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("257 Wild Horse Rd.\nHilton Head Island, SC 29926\n(336) 260-7945").setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER);
        table.addCell(cell);

        return table;
    }

    private Table billTo() throws IOException {
        Table table = new Table(2);
        table.setWidthPercent(100f);
        table.setBorder(Border.NO_BORDER);

        Table contactTable = new Table(1);
        contactTable.setWidthPercent(100f);
        cell = new Cell().add(new Paragraph("Bill To:").setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD)));
        contactTable.addCell(cell);

        cell = new Cell().add(new Customers().getCustomerById(INVOICE_DATA.getCustomerId()) + "\n" + INVOICE_DATA.getBillingAddress());
        contactTable.addCell(cell);

        cell = new Cell().add(contactTable).setBorder(Border.NO_BORDER);

        table.addCell(cell).setBorder(Border.NO_BORDER).setHorizontalAlignment(HorizontalAlignment.LEFT);

        Table dateTable = new Table(2);
        dateTable.setWidthPercent(100f);

        cell = new Cell().add("Date");
        dateTable.addCell(cell);

        cell = new Cell().add("Estimate No.");
        dateTable.addCell(cell);

        cell = new Cell().add(INVOICE_DATA.getInvoiceDate());
        dateTable.addCell(cell);

        cell = new Cell().add(INVOICE_DATA.getId());
        dateTable.addCell(cell);

        cell = new Cell().add(dateTable).setBorder(Border.NO_BORDER);

        table.addCell(cell).setHorizontalAlignment(HorizontalAlignment.RIGHT);

        return table;
    }

    private Table quoteData() throws ParseException {
        Table table = new Table(5);
        table.setMarginTop(5);
        table.setWidthPercent(100f);

        cell = new Cell().add("Item").setVerticalAlignment(VerticalAlignment.MIDDLE);
        table.addHeaderCell(cell);

        cell = new Cell().add("Quantity").setVerticalAlignment(VerticalAlignment.MIDDLE);
        table.addHeaderCell(cell);

        cell = new Cell().add("Description");
        table.addHeaderCell(cell);

        cell = new Cell().add("Unit Price").setVerticalAlignment(VerticalAlignment.MIDDLE);
        table.addHeaderCell(cell);

        cell = new Cell().add("Total").setVerticalAlignment(VerticalAlignment.MIDDLE);
        table.addHeaderCell(cell);

        if (!INVOICE_DATA.getInvoiceItems().isEmpty()) {
            INVOICE_DATA.getInvoiceItems().forEach(item -> {
                cell = new Cell().add(item.getItem()).setBorderTop(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER);
                table.addCell(cell);

                cell = new Cell().add(String.valueOf(item.getQuantity())).setBorderTop(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER);
                table.addCell(cell);

                cell = new Cell().add(item.getDescription()).setBorderTop(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER);
                table.addCell(cell);

                String price = CURRENCY.format(Double.parseDouble(item.getPrice()));
                cell = new Cell().add(price).setBorderTop(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER);
                table.addCell(cell);

                String total = CURRENCY.format(Double.parseDouble(item.getTotal()));
                cell = new Cell().add(total).setBorderTop(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER);
                table.addCell(cell);

                try {
                    itemsTotal += NumberFormat.getInstance().parse(item.getTotal()).doubleValue();
                } catch (ParseException ex) {
                    System.err.println(ex.getMessage());
                }
            });
        } else {
            cell = new Cell().add("").setBorderTop(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER);
            table.addCell(cell);

            cell = new Cell().add("").setBorderTop(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER);
            table.addCell(cell);

            cell = new Cell().add("").setBorderTop(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER);
            table.addCell(cell);

            cell = new Cell().add("").setBorderTop(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER);
            table.addCell(cell);

            cell = new Cell().add("").setBorderTop(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER);
            table.addCell(cell);
        }

        String subTotal = NumberFormat.getCurrencyInstance().format(itemsTotal);

        cell = new Cell(1, 4).add(new Paragraph("Sub Total")).setBorderRight(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell(1, 1).add(new Paragraph(subTotal));
        table.addCell(cell);

        cell = new Cell(1, 4).add(new Paragraph("Tax")).setBorderRight(Border.NO_BORDER);
        table.addCell(cell);
        
        String taxRate = INVOICE_DATA.getTaxRate() == null ? "No Tax" : INVOICE_DATA.getTaxRate();
        cell = new Cell(1, 1).add(new Paragraph(taxRate));
        table.addCell(cell);

        cell = new Cell(1, 4).add(new Paragraph("Total").setFontSize(18f)).setBorderRight(Border.NO_BORDER);
        table.addCell(cell);
        
        cell = new Cell(1, 1).add(new Paragraph(calculateTotal(subTotal, taxRate)).setFontSize(18f));
        table.addCell(cell);
        
        return table;
    }

    private Table memoData() {
        Table table = new Table(1);
        table.setMarginTop(5f);
        table.setWidthPercent(100f);
        
        cell = new Cell(1,1).add(new Paragraph("Memo:").setFontSize(18f));
        table.addCell(cell);
        
        cell = new Cell(1,1).add(new Paragraph(INVOICE_DATA.getMemo()));
        table.addCell(cell);
        
        return table;
    }

    private String calculateTotal(String subTotal, String tax) {

        try {
            double subTotalVal = NumberFormat.getCurrencyInstance().parse(subTotal).doubleValue();

            double total = subTotalVal;

            if (!tax.equals("No Tax")) {
                total = (subTotalVal * (Double.parseDouble(tax.trim().replace("%", "")) / 100)) + subTotalVal;
            }

            return NumberFormat.getCurrencyInstance().format(total);

        } catch (ParseException ex) {

            System.err.println(ex.getMessage());

            return null;
        }
    }

}
