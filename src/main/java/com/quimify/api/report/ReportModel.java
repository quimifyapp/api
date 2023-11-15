package com.quimify.api.report;

import javax.persistence.*;
import java.sql.Timestamp;

// This class represents a report sent by a user from the client.

@Entity
@Table(name = "report")
class ReportModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Timestamp dateAndTime;
    @Column(nullable = false)
    private String context;

    @Lob // Large object in DB
    @Column(nullable = false)
    private String details;

    private String userMessage;

    @Column(nullable = false)
    private Integer clientVersion;

    // Getters and setters:

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

    String getContext() {
        return context;
    }

    void setContext(String context) {
        this.context = context;
    }

    String getDetails() {
        return details;
    }

    void setDetails(String details) {
        this.details = details;
    }

    String getUserMessage() {
        return userMessage;
    }

    void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    Integer getClientVersion() {
        return clientVersion;
    }

    void setClientVersion(Integer clientVersion) {
        this.clientVersion = clientVersion;
    }

}
