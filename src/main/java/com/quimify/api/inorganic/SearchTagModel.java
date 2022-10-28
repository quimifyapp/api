package com.quimify.api.inorganic;
import com.quimify.api.Normalized;

import javax.persistence.*;

// Esta clase representa una de las etiquetas de un elemento inorgánico.

@Entity // Es un modelo real
@Table(name = "search_tag") // En la tabla 'etiqueta' de la DB
class SearchTagModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String normalizedText; // "hidruromagnesico" | "aguaoxigenada" | ...

    // --------------------------------------------------------------------------------

    // Constructor:

    protected SearchTagModel(String text) {
        this.normalizedText = Normalized.of(text);
    }

    protected SearchTagModel() {}

    // Getters y setters:

    protected Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    protected String getNormalizedText() {
        return normalizedText;
    }

    protected void setNormalizedText(String etiqueta) {
        this.normalizedText = etiqueta;
    }
}