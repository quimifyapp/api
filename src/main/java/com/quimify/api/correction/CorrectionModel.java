package com.quimify.api.correction;

import javax.persistence.*;

// This class represents input corrections.

@Entity
@Table(name = "correction")
class CorrectionModel {

    @Id
    private Integer priority; // "0", "1"... (order of appliance)

    @Column(nullable = false, unique = true)
    private String regexPattern; // ".*teleruro.*", ".*[^h]ex.*"...

    @Column(nullable = false, unique = true)
    private String mistake; // "fosfonio", "iato", "Ch"...

    @Column(nullable = false)
    private String correction; // "fosfanio", "ato", "CH"...

    // Getters and setters:

    String getRegexPattern() {
        return regexPattern;
    }

    String getMistake() {
        return mistake;
    }

    String getCorrection() {
        return correction;
    }

}
