package com.quimify.api.report;

import javax.persistence.*;
import java.sql.Timestamp;

// This class represents a report sent by a user from the client.

@Entity
@Table(name = "report")
class ReportModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private Timestamp dateAndTime;
    @Column(nullable = false)
    private String context;

    @Lob // Large objects
    @Column(nullable = false)
    private String details;

    private String userMessage;

    @Column(nullable = false)
    private Integer clientVersion;

    // Getters and setters:

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

    protected String getContext() {
        return context;
    }

    protected void setContext(String context) {
        this.context = context;
    }

    protected String getDetails() {
        return details;
    }

    protected void setDetails(String details) {
        this.details = details;
    }

    protected String getUserMessage() {
        return userMessage;
    }

    protected void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    protected Integer getClientVersion() {
        return clientVersion;
    }

    protected void setClientVersion(Integer clientVersion) {
        this.clientVersion = clientVersion;
    }

}
