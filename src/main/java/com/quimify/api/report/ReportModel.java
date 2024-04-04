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

    @Lob // Large object
    @Column(nullable = false)
    private String details;

    private String userMessage;

    @Column(nullable = false)
    private Integer clientVersion;

    // Getters and setters:

    void setDateAndTime(Timestamp dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    void setContext(String context) {
        this.context = context;
    }

    void setDetails(String details) {
        this.details = details;
    }

    void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    void setClientVersion(Integer clientVersion) {
        this.clientVersion = clientVersion;
    }

}
