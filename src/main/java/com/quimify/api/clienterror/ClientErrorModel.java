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

    @Lob // Large objects
    @Column(nullable = false)
    private String details;
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

    protected Integer getClientVersion() {
        return clientVersion;
    }

    protected void setClientVersion(Integer clientVersion) {
        this.clientVersion = clientVersion;
    }

}
