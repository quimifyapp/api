package com.quimify.api.inorganic;

import javax.persistence.*;

@MappedSuperclass
public abstract class InorganicSearchTagModel {

    @Id
    @Column(nullable = false)
    private String normalizedText; // Already normalized

    // Getters and setters:

    public String getNormalizedText() {
        return normalizedText;
    }

    public void setNormalizedText(String normalizedText) {
        this.normalizedText = normalizedText;
    }

}