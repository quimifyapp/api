package com.quimify.api.correction;

import javax.persistence.*;

// This class represents input corrections.

@Entity
@Table(name = "correction")
class CorrectionModel {

    // Non-nullable:

    @Id
    private Integer id;

    @Column(nullable = false)
    private String mistake; // "fosfonio", "iato", "Ch"...

    @Column(nullable = false)
    private String correction; // "fosfanio", "ato", "CH"...

    @Column(nullable = false)
    private Integer priority; // "0", "1"... (order of appliance)

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

    Integer getPriority() {
        return priority;
    }

    void setPriority(Integer priority) {
        this.priority = priority;
    }

}
