package com.quimify.api.inorganic;
import com.quimify.api.Normalized;

import javax.persistence.*;
import java.util.Objects;

// Esta clase representa una de las etiquetas de un elemento inorgánico.

@Entity // Es un modelo real
@Table(name = "inorganic_search_tag") // En la tabla 'inorganic_search_tag' de la DB
class InorganicSearchTagModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String normalizedTag; // "hidruromagnesico" | "aguaoxigenada" | ...

    // --------------------------------------------------------------------------------

    // Constructor:

    protected InorganicSearchTagModel(String tag) {
        this.normalizedTag = Normalized.of(tag);
    }

    protected InorganicSearchTagModel() {} // Needed by JPA

    // Queries:

    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass())
            return false;

        InorganicSearchTagModel otherInorganicSearchTag = (InorganicSearchTagModel) other;

        if(!Objects.equals(id, otherInorganicSearchTag.id))
            return false;

        return normalizedTag.contentEquals(otherInorganicSearchTag.normalizedTag);
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