package com.example.MuseumSpringBoot.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Session {
    private int sessionId;
    private int user;
    private LocalDateTime login;
    private LocalDateTime logout;
    private boolean active;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "session_id")
    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    @Basic
    @Column(name = "user")
    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    @Basic
    @Column(name = "login")
    public LocalDateTime getLogin() {
        return login;
    }

    public void setLogin(LocalDateTime login) {
        this.login = login;
    }

    @Basic
    @Column(name = "logout")
    public LocalDateTime getLogout() {
        return logout;
    }

    public void setLogout(LocalDateTime logout) {
        this.logout = logout;
    }

    @Basic
    @Column(name = "active")
    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
