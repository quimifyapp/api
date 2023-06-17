package com.quimify.api.inorganic;
import com.quimify.api.utils.Normalizer;

import javax.persistence.*;
import java.util.Objects;

// This class represents one of the search tags an inorganic compound might have.

@Entity
@Table(name = "inorganic_search_tag")
class InorganicSearchTagModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String normalizedTag; // "hidruromagnesico" | "aguaoxigenada" | ...

    // Constructors:

    InorganicSearchTagModel(String text) {
        this.normalizedTag = Normalizer.get(text);
    }

    protected InorganicSearchTagModel() {} // Needed by JPA, don't touch

    // Getters and setters:

    Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    String getNormalizedTag() {
        return normalizedTag;
    }

    void setNormalizedTag(String normalizedTag) {
        this.normalizedTag = normalizedTag;
    }

}