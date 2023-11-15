package com.quimify.api.classification;

// This class represents input classifications.

import javax.persistence.*;

@Entity
@Table(name = "classification")
class ClassificationModel {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String regexPattern; // ".*gramo.*", ".*masa *molecular *de.*"...

    @Column()
    private Classification classification; // Classification.organicFormula (1), null...

    @Column(nullable = false)
    private Integer priority; // "0", "1"... (order of appliance)

    // Getters and setters:

    Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    String getRegexPattern() {
        return regexPattern;
    }

    void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
    }

    Classification getClassification() {
        return classification;
    }

    void setClassification(Classification classification) {
        this.classification = classification;
    }

    Integer getPriority() {
        return priority;
    }

    void setPriority(Integer priority) {
        this.priority = priority;
    }

}
