package com.example.MuseumSpringBoot.model;

import javax.persistence.*;

@Entity
public class Token {
    private int tokenId;
    private String uuid;
    private int adminId;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "token_id")
    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    @Basic
    @Column(name = "uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Basic
    @Column(name = "admin_id")
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

}
