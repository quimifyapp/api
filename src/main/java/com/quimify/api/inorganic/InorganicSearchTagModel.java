package com.quimify.api.inorganic;

import org.hibernate.annotations.Check;

import javax.persistence.*;

// This class represents one of the search tags an inorganic compound might have.

@Entity
@Table(name = "inorganic_search_tag")
@Check(constraints = "REGEXP_LIKE(normalized_tag, '^[a-z0-9]+$', 'c')") // Case-sensitive matching
class InorganicSearchTagModel {

    @Id
    private String normalizedTag; // "hidruromagnesico" | "aguaoxigenada" | ...

    // Getters and setters:

    String getNormalizedTag() {
        return normalizedTag;
    }

    void setNormalizedTag(String normalizedTag) {
        this.normalizedTag = normalizedTag;
    }

}