package com.quimify.api.inorganic;
import com.quimify.api.Normalized;

import javax.persistence.*;

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

    protected InorganicSearchTagModel() {}

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