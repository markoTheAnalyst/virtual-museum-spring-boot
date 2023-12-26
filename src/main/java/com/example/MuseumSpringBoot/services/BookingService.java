package com.example.MuseumSpringBoot.services;

import com.example.MuseumSpringBoot.model.Booking;
import com.example.MuseumSpringBoot.model.Museum;
import com.example.MuseumSpringBoot.model.Reservation;
import com.example.MuseumSpringBoot.repositories.BookingRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    private final EmailSenderService mailService;

    public BookingService(BookingRepository bookingRepository, EmailSenderService mailService) {
        this.bookingRepository = bookingRepository;
        this.mailService = mailService;
    }

    public void createBooking(int userId, int reservationId){

        Booking booking = new Booking();
        booking.setReservation(reservationId);
        booking.setUser(userId);
        bookingRepository.save(booking);
    }

    public void sendNotification(Reservation reservation){

        LocalDateTime startingTime = reservation.getStartingTime();
        int duration = reservation.getDuration();
        LocalDateTime currentTime = LocalDateTime.now();

        long delay = ChronoUnit.MILLIS.between(currentTime, startingTime.minusHours(1));

        TimerTask first = new TimerTask() {
            public void run() {
                mailService.sendEmail("kalembermarko@gmail.com","Notification",
                        "The virtual visit will start in one hour.");
            }
        };
        Timer timer = new Timer();
        delay = delay > 0 ? delay : 0;
        timer.schedule(first, delay);

        delay = ChronoUnit.MILLIS.between(currentTime, startingTime.plusHours(duration).minusMinutes(5));

        TimerTask second = new TimerTask() {
            public void run() {
                mailService.sendEmail("kalembermarko@gmail.com","Notification",
                        "The virtual visit will end in five minutes.");
            }
        };
        delay = delay > 0 ? delay : 0;
        timer.schedule(second, delay);

    }

    public void sendTicket(Museum museum, Reservation reservation){

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = null;
        try {
            contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.COURIER, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(25, 600);
            contentStream.setLeading(14.5f);
            contentStream.showText("Ticket number: " + new Random().nextInt(1000));
            contentStream.newLine();
            contentStream.showText(museum.getName() + " museum");
            contentStream.newLine();
            contentStream.showText("Visit start time: " + reservation.getStartingTime());
            contentStream.newLine();
            contentStream.showText("Visit duration: " + reservation.getDuration() + "h");
            contentStream.endText();
            contentStream.close();

            document.save("images/ticket.pdf");
            document.close();


            mailService.sendEmailWithAttachment("kalembermarko@gmail.com","Ticket",
                    "This is the ticket for your virtual visit.","images/ticket.pdf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Booking> getBookings(int userId){

        return bookingRepository.findByUser(userId);
    }
}
