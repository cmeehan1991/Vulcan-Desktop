/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.output;

import com.cbmwebdevelopment.accounting.Payment;
import com.cbmwebdevelopment.accounting.Payment.SelectedTransactions;
import com.cbmwebdevelopment.customers.Customers;
import com.cbmwebdevelopment.notifications.Notifications;
import static com.itextpdf.io.font.FontConstants.HELVETICA;
import static com.itextpdf.io.font.FontConstants.HELVETICA_BOLD;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import static java.util.Locale.US;
import java.util.Properties;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author cmeehan
 */
public class PaymentReceipt {
    public final Payment PAYMENT;
    private Cell cell;
    private final SimpleDoubleProperty AMOUNT_DUE, AMOUNT_APPLIED;
    
    public PaymentReceipt(Payment payment){
        this.PAYMENT = payment;
        this.AMOUNT_APPLIED = new SimpleDoubleProperty();
        this.AMOUNT_DUE = new SimpleDoubleProperty();
    }
    
    public void savePdf(){
        FileChooser fc = new FileChooser();
        fc.setTitle(PAYMENT.getTransactionNumber());
        
        File pdfFile = fc.showSaveDialog(new Stage());
        createPdf(pdfFile.getAbsolutePath());
    }
    
    public void emailPdf(String to){
        String from = "connor@meehanwoodworking.com";
        String host = "localhost";
        
        // Get system properties
        Properties properties = System.getProperties();
        
        // Set up mail server
        properties.setProperty("mail.smtp.host", host);
        
        // Get the default Session object
        Session session = Session.getDefaultInstance(properties);
        
        try{
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Meehan Wood Working Payment Receipt");
            
            BodyPart messageBodyPart = new MimeBodyPart();
            
            // Fill the message
            messageBodyPart.setText("Attached is the requested payment receipt.");
            
            Multipart multipart = new MimeMultipart();
            
            multipart.addBodyPart(messageBodyPart);
            
            messageBodyPart = new MimeBodyPart();
            
            String filename = createPdf(new ByteArrayOutputStream().toString());
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName("Receipt.pdf");
            multipart.addBodyPart(messageBodyPart);
            
            
            // Send the complete message parts
            message.setContent(multipart);
            
            Transport.send(message);
            Notifications.snackbarNotification("Email Sent");
        }catch(MessagingException ex){
            System.err.println(ex.getMessage());
            Notifications.snackbarNotification("Message Failed To Send");
        }
    }
    
    private String createPdf(String dest){
        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            pdf.addNewPage();

            float width = (float) (pdf.getPage(1).getPageSize().getWidth() / 2) - (headerImage().getImageScaledWidth() / 2);
            float height = pdf.getPage(1).getPageSize().getHeight() - 75;
            document.add(headerImage().setFixedPosition(width, height));
            document.add(headerText().setMarginTop(20));
            document.add(receiptInformation());
            document.add(activitySection());
            document.close();
            pdf.close();
            writer.close();
            return dest;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
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
    
    private Table receiptInformation() throws IOException{
        Table table = new Table(4);
        table.setWidthPercent(100);
        table.setBorder(Border.NO_BORDER);
        
        cell = new Cell(1, 4).add(new Paragraph("Payment Receipt").setFontSize(18f).setFont(PdfFontFactory.createFont(HELVETICA_BOLD)));    
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        
        cell = new Cell(1, 2).add(new Paragraph("BILL TO").setFontSize(14f).setFont(PdfFontFactory.createFont(HELVETICA_BOLD)));  
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        
        cell = new Cell(1, 1).add(new Paragraph("Transaction No.").setFont(PdfFontFactory.createFont(HELVETICA_BOLD)));  
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        
        cell = new Cell(1, 1).add(new Paragraph(PAYMENT.getTransactionNumber()).setFont(PdfFontFactory.createFont(HELVETICA)));  
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        
        cell = new Cell(1, 2).add(new Paragraph(new Customers().getCustomerById(PAYMENT.getCustomerId())).setFont(PdfFontFactory.createFont(HELVETICA)));  
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        
        cell = new Cell(1, 1).add(new Paragraph("Date").setFont(PdfFontFactory.createFont(HELVETICA_BOLD)));  
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        
        cell = new Cell(1, 1).add(new Paragraph(PAYMENT.getPaymentDate()).setFont(PdfFontFactory.createFont(HELVETICA)));  
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        
        cell = new Cell(1, 4).setBackgroundColor(Color.LIGHT_GRAY).setMaxHeight(1f);  
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell); 
        
        cell = new Cell(1, 1).add(new Paragraph("PMT METHOD").setFont(PdfFontFactory.createFont(HELVETICA_BOLD)));  
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        
        cell = new Cell(1, 1).add(new Paragraph(PAYMENT.getPaymentMethod()).setFont(PdfFontFactory.createFont(HELVETICA)));  
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        
        cell = new Cell(1, 1).add(new Paragraph("Reference No.").setFont(PdfFontFactory.createFont(HELVETICA_BOLD)));  
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
        
        cell = new Cell(1, 1).add(new Paragraph(PAYMENT.getReferenceNumber()).setFont(PdfFontFactory.createFont(HELVETICA)));  
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell).setHorizontalAlignment(HorizontalAlignment.RIGHT);
        
        return table;
    }
    
    private Table activitySection() throws IOException{
        Table table = new Table(4);
        table.setBorder(Border.NO_BORDER);
        table.setWidthPercent(100);
        
        cell = new Cell(1, 4).add(new Paragraph("Activity").setFontSize(18f).setFont(PdfFontFactory.createFont(HELVETICA_BOLD))).setBorder(Border.NO_BORDER);
        table.addCell(cell);
        
        cell = new Cell(1, 1).add(new Paragraph("DESCRIPTION").setFontSize(12).setFont(PdfFontFactory.createFont(HELVETICA))).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(Color.BLACK, 2));
        table.addCell(cell);
        
        cell = new Cell(1, 1).add(new Paragraph("ORIGINAL AMOUNT").setFontSize(12).setFont(PdfFontFactory.createFont(HELVETICA))).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(Color.BLACK, 2));
        table.addCell(cell);
        
        cell = new Cell(1, 1).add(new Paragraph("OPEN BALANCE").setFontSize(12).setFont(PdfFontFactory.createFont(HELVETICA))).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(Color.BLACK, 2));
        table.addCell(cell);
        
        cell = new Cell(1, 1).add(new Paragraph("PAYMENT AMOUNT").setFontSize(12).setFont(PdfFontFactory.createFont(HELVETICA))).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(Color.BLACK, 2));
        table.addCell(cell);
        
        ObservableList<SelectedTransactions> transactions = PAYMENT.getSelectedTransaction();
        
        transactions.forEach(item -> {
            cell = new Cell(1, 1).add(new Paragraph(item.getDescription()).setFontSize(14)).setBorder(Border.NO_BORDER);
            table.addCell(cell);
            
            cell = new Cell(1, 1).add(new Paragraph(NumberFormat.getCurrencyInstance(US).format(item.getOriginalAmount())).setFontSize(12)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
            table.addCell(cell);
            
            cell = new Cell(1, 1).add(new Paragraph(NumberFormat.getCurrencyInstance(US).format(item.getOpenBalance())).setFontSize(12)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
            table.addCell(cell);
            
            cell = new Cell(1, 1).add(new Paragraph(NumberFormat.getCurrencyInstance(US).format(item.getPaymentAmount())).setFontSize(12)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
            table.addCell(cell);   
            
            AMOUNT_DUE.set(AMOUNT_DUE.add(item.getOpenBalance()).doubleValue());
            AMOUNT_APPLIED.set(AMOUNT_APPLIED.add(item.getPaymentAmount()).doubleValue());
        });
        
        cell = new Cell(1, 3).add(new Paragraph("Amount Due").setHorizontalAlignment(HorizontalAlignment.RIGHT)).setFontSize(14).setFont(PdfFontFactory.createFont(HELVETICA_BOLD)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
        table.addCell(cell);
        
        cell = new Cell(1, 4).add(new Paragraph(NumberFormat.getCurrencyInstance(US).format(AMOUNT_DUE.get())).setFont(PdfFontFactory.createFont(HELVETICA))).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
        table.addCell(cell);
        
        cell = new Cell(1, 3).add(new Paragraph("Applied")).setFontSize(14).setFont(PdfFontFactory.createFont(HELVETICA_BOLD)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
        table.addCell(cell);
        
        cell = new Cell(1, 4).add(new Paragraph(NumberFormat.getCurrencyInstance(US).format(AMOUNT_APPLIED.get())).setFont(PdfFontFactory.createFont(HELVETICA))).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
        table.addCell(cell);        
        
        return table;
    }
}
