package com.quimify.api.reporte;

import javax.persistence.*;

// Esta clase representa los reportes del cliente.

@Entity // Es un modelo real
@Table(name = "report") // En la tabla 'report' de la DB
class ReportModel {

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

    protected Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    protected Integer getClientVersion() {
        return clientVersion;
    }

    protected void setClientVersion(Integer clientVersion) {
        this.clientVersion = clientVersion;
    }

    protected String getTitle() {
        return title;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    protected String getDetails() {
        return details;
    }

    protected void setDetails(String details) {
        this.details = details;
    }
    
}
