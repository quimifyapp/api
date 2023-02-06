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

    protected InorganicSearchTagModel(String text) {
        this.normalizedTag = Normalizer.get(text);
    }

    protected InorganicSearchTagModel() {} // Needed by JPA

    // Queries:

    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass())
            return false;

        InorganicSearchTagModel otherSearchTag = (InorganicSearchTagModel) other;

        if(!Objects.equals(id, otherSearchTag.id))
            return false;

        return normalizedTag.contentEquals(otherSearchTag.normalizedTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, normalizedTag);
    }

    // Getters y setters:

    protected Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    protected String getNormalizedTag() {
        return normalizedTag;
    }

    protected void setNormalizedTag(String normalizedTag) {
        this.normalizedTag = normalizedTag;
    }

}