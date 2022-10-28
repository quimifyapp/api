package com.quimify.api.reporte;

import javax.persistence.*;

// Esta clase representa los reportes del cliente.

@Entity // Es un modelo real
@Table(name = "report") // En la tabla 'report' de la DB
public class ReportModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private Integer clientVersion; // Se corresponde con la versión del cliente
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String details;

    // --------------------------------------------------------------------------------

    // Getters y setters:

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(Integer clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
    
}
