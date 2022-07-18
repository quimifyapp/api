package com.quimify.api.inorganico;
import javax.persistence.*;

// Esta clase representa una de las etiquetas de un elemento inorgánico.

@Entity // Es un modelo real
@Table(name = "etiqueta") // En la tabla 'etiqueta' de la DB
public class EtiquetaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String texto_normalizado; // "hidruromagnesico" | "aguaoxigenada" | ...

    // --------------------------------------------------------------------------------

    // Constructor:

    public EtiquetaModel(String texto_normalizado) {
        this.texto_normalizado = texto_normalizado;
    }

    public EtiquetaModel() {}

    // Getters y setters:

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTexto_normalizado() {
        return texto_normalizado;
    }

    public void setTexto_normalizado(String etiqueta) {
        this.texto_normalizado = etiqueta;
    }
}