package com.quimify.api.error;

import javax.persistence.*;
import java.sql.Timestamp;

// This class represent errors that occur during the execution of this project.

@Entity
@Table(name = "error")
class ErrorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Timestamp dateAndTime;

    @Lob // Large object
    @Column(nullable = false)
    private String title;

    @Lob // Large object
    @Column(nullable = false)
    private String details;

    @Column(nullable = false)
    private String location;

    // Getters and setters:

    void setDateAndTime(Timestamp dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    void setTitle(String title) {
        this.title = title;
    }

    void setDetails(String details) {
        this.details = details;
    }

    void setLocation(String location) {
        this.location = location;
    }

}
