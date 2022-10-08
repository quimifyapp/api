package com.quimify.api.reporte;

import javax.persistence.*;

// Esta clase representa los reportes del cliente.

@Entity // Es un modelo real
@Table(name = "reporte") // En la tabla 'reporte' de la DB
public class ReporteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private Integer version; // Se corresponde con la versión del cliente
    @Column(nullable = false)
    private String titulo;
    @Column(nullable = false)
    private String detalles;

    // --------------------------------------------------------------------------------

    // Getters y setters:

    public Integer getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

}
