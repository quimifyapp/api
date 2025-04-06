package com.quimify.api.inorganic;

import org.hibernate.annotations.Check;

import javax.persistence.*;

@Entity
@Table(name = "inorganic_search_tag")
@Check(constraints = "REGEXP_LIKE(normalized_text, '^[a-z0-9]+$', 'c')") // Case-sensitive matching with 'c'
class InorganicSearchTagModel {

    @Id
    private String normalizedText; // "hidruromagnesico", "aguaoxigenada"...

    // Getters and setters:

    String getNormalizedText() {
        return normalizedText;
    }

    void setNormalizedText(String normalizedText) {
        this.normalizedText = normalizedText;
    }

}