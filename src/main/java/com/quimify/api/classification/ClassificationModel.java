package com.quimify.api.classification;

// This class represents input classifications.

import javax.persistence.*;

@Entity
@Table(name = "classification")
class ClassificationModel {

    @Id
    private Integer priority; // "0", "1"... (order of appliance)

    @Column(nullable = false, unique = true)
    private String regexPattern; // ".*gramo.*", ".*masa *molecular *de.*"...

    @Column()
    private String name; // "inorganic-formula", "chemical-problem", null...

    // Getters and setters:

    Integer getPriority() {
        return priority;
    }

    void setPriority(Integer priority) {
        this.priority = priority;
    }

    String getRegexPattern() {
        return regexPattern;
    }

    void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

}
