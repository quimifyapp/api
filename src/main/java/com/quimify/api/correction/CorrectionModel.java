package com.quimify.api.correction;

import javax.persistence.*;

// This class represents input corrections.

@Entity
@Table(name = "correction")
class CorrectionModel {

    @Id
    private Integer priority; // "0", "1"... (order of appliance)

    @Column(nullable = false, unique = true)
    private String mistake; // "fosfonio", "iato", "Ch"...

    @Column(nullable = false)
    private String correction; // "fosfanio", "ato", "CH"...

    // Getters and setters:

    Integer getPriority() {
        return priority;
    }

    void setPriority(Integer priority) {
        this.priority = priority;
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
