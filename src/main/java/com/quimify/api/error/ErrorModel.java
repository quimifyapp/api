package com.quimify.api.error;

import javax.persistence.*;
import java.sql.Timestamp;

// This class represent errors that occur during the execution of this project.

@Entity
@Table(name = "error") // In table 'error' of DB
class ErrorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private Timestamp dateAndTime;
    @Column(nullable = false)
    private String title;

    @Lob // Large objects
    @Column(nullable = false)
    private String details;

    @Column(nullable = false)
    private String location;

    // Getters y setters:

    protected Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    protected Timestamp getDateAndTime() {
        return dateAndTime;
    }

    protected void setDateAndTime(Timestamp dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    protected String getTitle() {
        return title;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    protected String getDetails() {
        return details;
    }

    protected void setDetails(String details) {
        this.details = details;
    }

    protected String getLocation() {
        return location;
    }

    protected void setLocation(String location) {
        this.location = location;
    }

}
