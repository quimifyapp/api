package com.quimify.api.classification;

// This class represents input classifications.

import javax.persistence.*;

@Entity
@Table(name = "classification")
class ClassificationModel {

    @Id
    private Integer priority; // "0", "1"... (order of appliance)

    @Column(nullable = false, unique = true)
    @Lob // Large object
    private String regexPattern; // ".*gramo.*", ".*masa *molecular *de.*"...

    @Column()
    @Enumerated(EnumType.STRING)
    private Classification classification; // "inorganicFormula", "chemicalProblem", null...

    // Getters and setters:

    String getRegexPattern() {
        return regexPattern;
    }

    Classification getClassification() {
        return classification;
    }

}
