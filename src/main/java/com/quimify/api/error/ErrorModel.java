package com.quimify.api.error;

import javax.persistence.*;
import java.sql.Timestamp;

// This class represent errors that occur during the execution of this project.

@Entity
@Table(name = "error") // In table 'error' of DB
class ErrorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Timestamp dateAndTime;

    @Lob // Large object in DB
    @Column(nullable = false)
    private String title;

    @Lob // Large object in DB
    @Column(nullable = false)
    private String details;

    @Column(nullable = false)
    private String location;

    // Getters y setters:

    Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    Timestamp getDateAndTime() {
        return dateAndTime;
    }

    void setDateAndTime(Timestamp dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getDetails() {
        return details;
    }

    void setDetails(String details) {
        this.details = details;
    }

    String getLocation() {
        return location;
    }

    void setLocation(String location) {
        this.location = location;
    }

}
