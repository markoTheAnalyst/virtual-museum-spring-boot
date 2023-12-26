package com.example.MuseumSpringBoot.model;

import org.springframework.data.geo.Point;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Museum {
    private int museumId;
    private String name;
    private String address;
    private String phoneNumber;
    private String city;
    private String country;
    private String type;
    private Point geolocation;
    private BigDecimal longitude;
    private BigDecimal latitude;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "museum_id")
    public int getMuseumId() {
        return museumId;
    }

    public void setMuseumId(int museumId) {
        this.museumId = museumId;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Basic
    @Column(name = "phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Basic
    @Column(name = "city")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Basic
    @Column(name = "country")
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Basic
    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "geolocation")
    public Point getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(Point geolocation) {
        this.geolocation = geolocation;
    }

    @Basic
    @Column(name = "longitude")
    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    @Basic
    @Column(name = "latitude")
    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

}
