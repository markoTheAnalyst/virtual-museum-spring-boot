package com.example.MuseumSpringBoot.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

@Service
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("marko.kalember@student.etf.unibl.org");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendEmailWithAttachment(String to, String subject, String body, String attachment)  {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom("marko.kalember@student.etf.unibl.org");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setText(body);
            mimeMessageHelper.setSubject(subject);

            FileSystemResource fileSystemResource =
                    new FileSystemResource(new File(attachment));
            mimeMessageHelper.addAttachment(fileSystemResource.getFilename(),
                    fileSystemResource);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
    public void preparePDF()  {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream contentStream = null;
        float offset = 14.5f;
        float curYPos = 750f;
        try {
            contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.COURIER, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(25, 750);
            contentStream.setLeading(offset);

            for(String line : Files.readAllLines(Paths.get("log.txt"))) {

                if(curYPos < 50){
                    curYPos = 750f;
                    contentStream.endText();
                    contentStream.close();

                    page = new PDPage();
                    document.addPage(page);

                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(PDType1Font.COURIER, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(25, 750);
                    contentStream.setLeading(14.5f);
                }
                String[] match = line.split("@@");
                if(match.length > 1) {
                    contentStream.showText(match[1]);
                    contentStream.newLine();
                    curYPos -= offset;
                }


            }
            contentStream.endText();
            contentStream.close();

            document.save("log.pdf");
            document.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
