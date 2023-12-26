package com.example.MuseumSpringBoot.services;

import com.example.MuseumSpringBoot.model.Session;
import com.example.MuseumSpringBoot.model.Token;
import com.example.MuseumSpringBoot.model.User;
import com.example.MuseumSpringBoot.repositories.SessionRepository;
import com.example.MuseumSpringBoot.repositories.TokenRepository;
import com.example.MuseumSpringBoot.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final TokenRepository tokenRepository;


    public UserService(UserRepository userRepository, SessionRepository sessionRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.tokenRepository = tokenRepository;
    }

    public User getAccount(String username, String password){

        User account = userRepository.findByUsernameAndPassword(username, password);
        if(account != null && account.getActive())
            return account;
        else
            return null;

    }

    public void login(int userId){
        List<Session> sessions = sessionRepository.findAll().stream().filter(session -> session.getLogout() != null).collect(Collectors.toList());
        Optional<Session> optionalSession = sessions.stream().filter(s -> s.getUser() == userId &&
                        s.getLogout().getDayOfMonth() == LocalDateTime.now().getDayOfMonth() &&
                        s.getLogout().getHour() == LocalDateTime.now().getHour()).findFirst();
        Session newSession = new Session();
        if(optionalSession.isPresent()) {
            newSession = optionalSession.get();
            newSession.setLogout(null);
        }
        else {
            newSession.setUser(userId);
            newSession.setLogin(LocalDateTime.now());
        }
        newSession.setActive(true);
        sessionRepository.save(newSession);
    }

    public void logout(int userId) {
        List<Session> sessions = sessionRepository.findAll();
        Optional<Session> optionalSession = sessions.stream().filter(s -> s.getUser() == userId && s.getLogout() == null).findFirst();
        Session session;
        if(optionalSession.isPresent()) {
            session = optionalSession.get();
            session.setLogout(LocalDateTime.now());
            session.setActive(false);
            sessionRepository.save(session);
        }
    }

    public long getActiveSessionsCount(){

        List<Session> sessions = sessionRepository.findAll();
        return sessions.stream().filter(session -> session.getActive()).count();
    }

    public long getTotalUsers(){

        List<User> users = userRepository.findAll();
        return users.size();
    }

    public String getToken(int id){
        Token token = tokenRepository.findByAdminId(id);
        String uuid = "none";
        if(token != null){
            uuid = token.getUuid();
        }
        return uuid;
    }

    public long[] getUserCountPerHour(){

        List<Session> sessions = sessionRepository.findAll();

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime currentTimeMinus24Hours = currentTime.minusHours(24);

        long[] activePerHour = new long[25];

//        List<Session> filteredSessions = sessions.stream().filter(session -> session.getLogin().
//                isAfter(currentTimeMinus24Hours)).collect(Collectors.toList());

        long activeUsers = sessions.stream().filter(session -> session.getLogin().
                isBefore(currentTimeMinus24Hours) && (session.getLogout() == null ||
                    session.getLogout().isAfter(currentTimeMinus24Hours))).count();
        activePerHour[0] = activeUsers;

        List<Session> filteredSessions = sessions.stream().filter(session -> session.getLogout() == null || session.getLogout().
                isAfter(currentTimeMinus24Hours)).collect(Collectors.toList());

//        Map<Integer, Map<Integer, Long>> loginsByDay = filteredSessions.stream()
//                .collect(Collectors.groupingBy(s -> s.getLogin().getDayOfMonth(),
//                        Collectors.groupingBy(s -> s.getLogin().getHour(),
//                                Collectors.counting())));
//
//        Map<Integer, Map<Integer, Long>> logoutsByDay = filteredSessions.stream()
//                .collect(Collectors.groupingBy(s -> s.getLogout().getDayOfMonth(),
//                        Collectors.groupingBy(s -> s.getLogout().getHour(),
//                                Collectors.counting())));

//        Map<Integer, Long> yesterdayLogins = loginsByDay.get(currentTimeMinus24Hours.getDayOfMonth());
//        Map<Integer, Long> yesterdayLogouts = logoutsByDay.get(currentTimeMinus24Hours.getDayOfMonth());
//        int hour = currentTimeMinus24Hours.getHour();
//        int i = 0;
//        //int activeUsers = Math.toIntExact(yesterdayLogouts.get(hour) != null ? yesterdayLogouts.get(hour) : 0);
//        if(yesterdayLogins != null) {
//            while (hour + i < 24) {
//
//                Long value = yesterdayLogins.get(hour + i);
//                occurencePerHour[i] = Math.toIntExact(value != null ? value : 0);
//                i++;
//            }
//        }
//        Map<Integer, Long> today = collect.get(currentTime.getDayOfMonth());
//        if(today != null) {
//            for (int y = 0; y <= hour; y++) {
//
//                Long value = today.get(y);
//                occurencePerHour[i + y] = Math.toIntExact(value != null ? value : 0);
//            }
//        }
        long numberOfLogins = 0;
        long numberOfLogouts = 0;
        int hour = currentTimeMinus24Hours.getHour();
        int i = 0;

        while (hour + i < 24) {

            int finalI = i;
            numberOfLogins = filteredSessions.stream().filter(stream -> stream.getLogin().getHour() == hour + finalI &&
                    currentTimeMinus24Hours.getDayOfMonth() == stream.getLogin().getDayOfMonth()).count();

            numberOfLogouts = filteredSessions.stream().filter(stream -> stream.getLogout() != null &&
                    stream.getLogout().getHour() == hour + finalI &&
                    currentTimeMinus24Hours.getDayOfMonth() == stream.getLogout().getDayOfMonth()).count();

            activePerHour[i] = activeUsers + numberOfLogins;
            activeUsers += (numberOfLogins - numberOfLogouts);
            i++;
        }
        for (int j = 0; j <= hour; j++) {

            int finalJ = j;
            numberOfLogins = filteredSessions.stream().filter(stream -> stream.getLogin().getHour() == finalJ &&
                    currentTime.getDayOfMonth() == stream.getLogin().getDayOfMonth()).count();

            numberOfLogouts = filteredSessions.stream().filter(stream -> stream.getLogout() != null &&
                    stream.getLogout().getHour() == finalJ &&
                            currentTime.getDayOfMonth() == stream.getLogout().getDayOfMonth()).count();

            activePerHour[i + j] = activeUsers + numberOfLogins;
            activeUsers += (numberOfLogins - numberOfLogouts);

        }

        return activePerHour;
    }

    public boolean registerUser(User user) {

        User account = userRepository.findByUsername(user.getUsername());
        if(account != null){
            return false;
        }
        user.setActive(false);
        user.setAdmin(false);
        userRepository.save(user);
        return true;
    }
}
