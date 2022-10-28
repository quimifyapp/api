package com.quimify.api.inorganic;
import com.quimify.api.Normalized;

import javax.persistence.*;

// Esta clase representa una de las etiquetas de un elemento inorgánico.

@Entity // Es un modelo real
@Table(name = "search_tag") // En la tabla 'etiqueta' de la DB
public class SearchTagModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String normalizedText; // "hidruromagnesico" | "aguaoxigenada" | ...

    // --------------------------------------------------------------------------------

    // Constructor:

    public SearchTagModel(String text) {
        this.normalizedText = Normalized.of(text);
    }

    public SearchTagModel() {}

    // Getters y setters:

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNormalizedText() {
        return normalizedText;
    }

    public void setNormalizedText(String etiqueta) {
        this.normalizedText = etiqueta;
    }
}