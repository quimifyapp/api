package com.quimify.api.inorganic;

import javax.persistence.*;

@MappedSuperclass
public abstract class InorganicSearchTagModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String normalizedText; // Already normalized

    // Getters and setters:

    public Integer getId() {
        return id;
    }

    public String getNormalizedText() {
        return normalizedText;
    }

    public void setNormalizedText(String normalizedText) {
        this.normalizedText = normalizedText;
    }

}