/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.output;

import com.cbmwebdevelopment.customers.Customers;
import com.cbmwebdevelopment.quote.QuoteData;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
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
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author cmeehan
 */
public class QuotePDF {

    private Cell cell = new Cell();
    private double itemsTotal = 0;
    private final QuoteData QUOTE_DATA;
    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.US);
    
    public QuotePDF(QuoteData quoteData) {
        this.QUOTE_DATA = quoteData;
        System.out.println(quoteData.getClientId());
    }

    public void printPdf() {

    }

    public void savePdf() {
        FileChooser fc = new FileChooser();
        fc.setInitialFileName("Quote " + QUOTE_DATA.getQuoteId() + " " + formatter.format(new Date()) + ".pdf");
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
            document.add(footer());
            document.close();
            pdf.close();
            writer.close();
        } catch (IOException | ParseException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private Paragraph waterMark() {
        Paragraph paragraph = new Paragraph("ESTIMATE").setFontColor(Color.DARK_GRAY).setFontSize(36f).setOpacity(0.5f);

        return paragraph;
    }

    private Image headerImage() {
        Image image = new Image(ImageDataFactory.create(getClass().getResource("/images/Logo.png")));
        image.scaleToFit(75, 75);
        return image;
    }

    private Table headerText() {
        Table table = new Table(1);
        table.setWidthPercent(66);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        Cell cell = new Cell().add(new Paragraph("\nMeehan Wood Working"));
        cell.setTextAlignment(TextAlignment.CENTER);
        cell.setBorder(Border.NO_BORDER);
        cell.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("320 Burlington Ave.\nGibsonville, NC 27249\n(336) 260-0061").setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER);
        table.addCell(cell);

        return table;
    }

    private Table billTo() throws IOException {
        Table table = new Table(2);
        table.setWidthPercent(100f);
        table.setBorder(Border.NO_BORDER);

        Table contactTable = new Table(1);
        contactTable.setWidthPercent(100f);
        cell = new Cell().add(new Paragraph("TO:").setFont(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD))).setBackgroundColor(new DeviceRgb(0,77,0)).setFontColor(Color.WHITE);
        contactTable.addCell(cell);
        
        cell = new Cell().add(new Customers().getCustomerById(QUOTE_DATA.getClientId()) + "\n" + QUOTE_DATA.getBillingAddress());
        contactTable.addCell(cell);

        cell = new Cell().add(contactTable).setBorder(Border.NO_BORDER);

        table.addCell(cell).setBorder(Border.NO_BORDER).setHorizontalAlignment(HorizontalAlignment.LEFT);

        Table dateTable = new Table(2);
        dateTable.setWidthPercent(100f);

        cell = new Cell().add(new Paragraph("DATE")).setBackgroundColor(new DeviceRgb(0,77,0)).setFontColor(Color.WHITE);
        dateTable.addCell(cell);

        cell = new Cell().add("ESTIMATE NO.").setBackgroundColor(new DeviceRgb(0,77,0)).setFontColor(Color.WHITE);;
        dateTable.addCell(cell);

        cell = new Cell().add(QUOTE_DATA.getQuoteDate());
        dateTable.addCell(cell);

        cell = new Cell().add(QUOTE_DATA.getQuoteId());
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

        if (!QUOTE_DATA.getQuoteItems().isEmpty()) {
            QUOTE_DATA.getQuoteItems().forEach(item -> {
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

        String total = NumberFormat.getCurrencyInstance().format(itemsTotal);

        cell = new Cell(1, 4).add(new Paragraph("Total").setFontSize(16f)).setBorderRight(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell(1, 1).add(new Paragraph(total).setFontSize(16f));
        table.addCell(cell);

        return table;
    }
    
    public Paragraph footer(){
        Paragraph paragraph = new Paragraph();
        
        paragraph.add("Note: This estimate is not a contract or a bill. It is our best guess at the total price to complete the work stated above. If prices change or additional materials and labor are required, we will inform you prior to proceeding with the work. All materials are to be paid for up front; labor costs are not due until project completion, unless otherwise agreed upon.");
        
        return paragraph;
    }

}
