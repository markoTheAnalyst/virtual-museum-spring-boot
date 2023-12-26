package com.example.MuseumSpringBoot.controller;

import com.example.MuseumSpringBoot.services.*;
import com.example.MuseumSpringBoot.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class Controller {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MuseumService museumService;
    private final ReservationService reservationService;
    private final EmailSenderService mailService;
    private final UserService userService;
    private final BookingService bookingService;

    public Controller(MuseumService museumService, ReservationService reservationService, EmailSenderService mailService, UserService userService, BookingService bookingService) {

        this.museumService = museumService;
        this.reservationService = reservationService;
        this.mailService = mailService;
        this.userService = userService;
        this.bookingService = bookingService;
    }



    @GetMapping("/bookings/{id}")
    ResponseEntity<List<Booking>> getBookings(@PathVariable("id") int userId) {

        logger.info("@@ Getting bookings");
        return new ResponseEntity<>(bookingService.getBookings(userId),HttpStatus.OK);
    }

    @PostMapping("/booking")
    ResponseEntity<String> addBooking(@RequestParam MultiValueMap<String, String> map) {

        logger.info("@@ Creating booking");
        int userId = Integer.parseInt(map.getFirst("user"));
        int reservationId = Integer.parseInt(map.getFirst("reservation"));

        Reservation reservation = reservationService.getReservation(reservationId).get();
        Museum museum = museumService.getMuseum(reservation.getMuseum()).get();

        bookingService.createBooking(userId, reservationId);
        bookingService.sendTicket(museum, reservation);
        bookingService.sendNotification(reservation);
        return new ResponseEntity<>(museum.getName(), HttpStatus.OK);
    }

    @GetMapping("/token/{id}")
    ResponseEntity<?> getToken(@PathVariable("id") int adminId) {

        logger.info("@@ Getting token");
        return new ResponseEntity<>(
                Collections.singletonMap("token",userService.getToken(adminId)),
                HttpStatus.OK);
    }

    @PostMapping("/login")
    ResponseEntity<User> logIn(@RequestBody User user) {

        logger.info("@@ User logging in");
        User account = userService.getAccount(user.getUsername(), user.getPassword());
        if (account != null) {
            userService.login(account.getUserId());
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody User user) {

        logger.info("@@ User registering");
        if(userService.registerUser(user))
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/logout")
    ResponseEntity<?> logOut(@RequestBody int id) {

        logger.info("@@ User logging out");
        userService.logout(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/museums")
    ResponseEntity<Integer> addMuseum(@RequestBody Museum museum) {

        logger.info("@@ Creating a museum");
        museumService.addMuseum(museum);
        return new ResponseEntity<Integer>(HttpStatus.OK);
    }

    @PostMapping("/reservations")
    ResponseEntity<Integer> addReservation(@RequestBody Reservation reservation) {

        logger.info("@@ Creating a reservation");
        reservationService.addReservation(reservation);
        return new ResponseEntity<Integer>(HttpStatus.OK);
    }

    @GetMapping("/reservations")
    ResponseEntity<List<Reservation>> getAllReservations() {

        logger.info("@@ Getting all reservations");
        return new ResponseEntity<>(reservationService.getAllReservations(),HttpStatus.OK);
    }

    @GetMapping("/reservations/{museumId}")
    ResponseEntity<List<Reservation>> getReservations(@PathVariable("museumId") int museumId) {

        logger.info("@@ Getting reservations for museum");
        return new ResponseEntity<>(reservationService.getReservations(museumId),HttpStatus.OK);
    }
    @GetMapping("/reservation/{reservationId}")
    ResponseEntity<Reservation> getReservation(@PathVariable("reservationId") int reservationId) {


        logger.info("@@ Getting reservation");
        Optional<Reservation> reservation = reservationService.getReservation(reservationId);

        if (reservation.isPresent()) {
            return new ResponseEntity<>(reservation.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/museums")
    ResponseEntity<List<Museum>> getMuseums() {

        logger.info("@@ Getting all museums");
        return new ResponseEntity<>(museumService.getAll(),HttpStatus.OK);
    }

    @GetMapping("/museums/{id}")
    ResponseEntity<Museum> getMuseum(@PathVariable("id") int id) {

        logger.info("@@ Getting museum");
        Optional<Museum> museum = museumService.getMuseum(id);
        if (museum.isPresent()) {
            return new ResponseEntity<>(museum.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/logs")
    ResponseEntity<ArrayList<String>> getLogs() throws IOException {

        logger.info("@@ Getting logs");
        ArrayList<String> filtered = new ArrayList<>();
        for(String line : Files.readAllLines(Paths.get("log.txt"))){
            String[] match = line.split("@@");
            if(match.length > 1) {
                filtered.add(match[1]);
            }
        }
        return new ResponseEntity<>(filtered,HttpStatus.OK);
    }

    @GetMapping("/logs/download")
    ResponseEntity<InputStreamResource> downloadLogs()  {

        logger.info("@@ Getting logs in pdf");
        mailService.preparePDF();
        try
        {
            File file = new File("log.pdf");
            HttpHeaders respHeaders = new HttpHeaders();
            MediaType mediaType = MediaType.parseMediaType("application/pdf");
            respHeaders.setContentType(mediaType);
            respHeaders.setContentLength(file.length());
            respHeaders.setContentDispositionFormData("attachment", file.getName());
            InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
            return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/upload/{id}")
    public ResponseEntity<?> handleFileUpload(@RequestParam("files")List<MultipartFile> files , @RequestParam("link") String link, @PathVariable("id") String id) throws IOException {

        logger.info("@@ Uploading museum exhibit");
        String uploadDir = "images/" + id;
        StringBuilder fileName = new StringBuilder();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        for(MultipartFile file : files){
            fileName.append(file.getOriginalFilename());
            try (InputStream inputStream = file.getInputStream()) {

                Path filePath = uploadPath.resolve(StringUtils.cleanPath(file.getOriginalFilename()));
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioe) {
                throw new IOException("Could not save image file: " + fileName, ioe);
            }
        }

        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(link);

        if(matcher.find()){
            link= matcher.group();
        }
        else
            link = "8f_XeVyYouo";

        Files.write(Paths.get(uploadDir+"/url.txt"), link.getBytes());

        return ResponseEntity.ok(fileName.toString());
    }

    @GetMapping("/download/images/{folder}/{image}")
    public ResponseEntity<?> handleFileDownload(@PathVariable String folder, @PathVariable String image) throws IOException {

        logger.info("@@ Downloading an image");
        String uploadDir = "images/" + folder + "/" + image;
        File file = new File(uploadDir);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        ResponseEntity<Object> responseEntity = ResponseEntity.ok().body(resource);
        return responseEntity;

    }

    @GetMapping("/download/video/{folder}")
    public ResponseEntity<?> handleVideoDownload(@PathVariable String folder) throws IOException {

        logger.info("@@ Getting video link");
        String file = "images/" + folder + "/url.txt";
        String url = Files.readAllLines(Paths.get(file)).get(0);
        return ResponseEntity.ok().body(Collections.singletonMap("url", url));
    }

    @GetMapping("/count/{id}")
    public ResponseEntity<?> countImages(@PathVariable String id)  {

        logger.info("@@ Getting image names");
        String uploadDir = "images/" + id;
        File file = new File(uploadDir);
        ArrayList<String> imageNames = new ArrayList<>();
        for (File f : file.listFiles()) {
            imageNames.add(f.getPath());
        }
        return ResponseEntity.ok(imageNames);
    }

    @GetMapping("/hours")
    ResponseEntity<?> hours() {

        logger.info("@@ Getting user activity per hour");
        return new ResponseEntity<>(userService.getUserCountPerHour(), HttpStatus.OK);
    }

    @GetMapping("/user-count")
    ResponseEntity<?> userCount() {

        logger.info("@@ Getting active and total user count");
        Map<String, Long> map = new HashMap<>();
        map.put("total",userService.getTotalUsers());
        map.put("active",userService.getActiveSessionsCount());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

}

