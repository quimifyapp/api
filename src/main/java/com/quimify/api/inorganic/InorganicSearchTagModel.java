package com.quimify.api.inorganic;

import org.hibernate.annotations.Check;

import javax.persistence.*;

// This class represents one of the search tags an inorganic compound might have.

@Entity
@Table(name = "inorganic_search_tag")
@Check(constraints = "REGEXP_LIKE(normalized_text, '^[a-z0-9]+$', 'c')") // Case-sensitive matching
class InorganicSearchTagModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String normalizedText; // "hidruromagnesico", "aguaoxigenada"...

    // Getters and setters:

    Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    String getNormalizedText() {
        return normalizedText;
    }

    void setNormalizedText(String normalizedText) {
        this.normalizedText = normalizedText;
    }

}