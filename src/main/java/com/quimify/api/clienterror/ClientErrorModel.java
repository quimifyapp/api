package com.quimify.api.clienterror;

import javax.persistence.*;
import java.sql.Timestamp;

// This class represents an error sent by the client.

@Entity
@Table(name = "client_error")
class ClientErrorModel {

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

    void setClientVersion(Integer clientVersion) {
        this.clientVersion = clientVersion;
    }

}
