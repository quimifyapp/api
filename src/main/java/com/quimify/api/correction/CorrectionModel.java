package com.quimify.api.correction;

import javax.persistence.*;

// This class represents input corrections.

@Entity
@Table(name = "correction")
class CorrectionModel {

    // Non-nullable:

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String mistake; // "fosfonio", "iato", "Ch"...

    @Column(nullable = false)
    private String correction; // "fosfanio", "ato", "CH"...

    // Getters and setters:

    Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    String getMistake() {
        return mistake;
    }

    void setMistake(String mistake) {
        this.mistake = mistake;
    }

    String getCorrection() {
        return correction;
    }

    void setCorrection(String correction) {
        this.correction = correction;
    }

}
