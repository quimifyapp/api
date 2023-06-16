package com.quimify.api.clienterror;

import javax.persistence.*;
import java.sql.Timestamp;

// This class represents an error sent by the client.

@Entity
@Table(name = "client_error")
class ClientErrorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private Timestamp dateAndTime;
    @Column(nullable = false)
    private String context;

    @Lob // Large object in DB
    @Column(nullable = false)
    private String details;
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

    Integer getClientVersion() {
        return clientVersion;
    }

    void setClientVersion(Integer clientVersion) {
        this.clientVersion = clientVersion;
    }

}
