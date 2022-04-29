package com.quimify.api.configuracion;

import javax.persistence.*;

// Esta clase representa los compuestos inorgánicos.

@Entity // Es un modelo real
@Table(name = "configuracion") // En la tabla 'configuracion' de la DB
public class ConfiguracionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer version; // Se corresponde con la versión de la API

    // Actualizaciones:

    @Column(nullable = false)
    private Boolean actualizacion_disponible;
    @Column(nullable = false)
    private Boolean actualizacion_necesaria;

    // API de Google:

    @Column(nullable = false)
    private Boolean api_google_on = false; // Interruptor
    @Column(nullable = false)
    private Integer api_google_limite; // Nº máx. de búsquedas diarias
    private String api_google_url; // URL + key

    // API de Bing gratis:

    @Column(nullable = false)
    private Boolean api_bing_gratis_on = false; // Interruptor
    private String api_bing_gratis_url; // URL + key

    // API de Bing de pago:

    @Column(nullable = false)
    private Boolean api_bing_pago_on = false; // Interruptor
    @Column(nullable = false)
    private Integer api_bing_pago_limite; // Nº máx. de búsquedas diarias
    private String api_bing_pago_url; // URL + key

    // --------------------------------------------------------------------------------

    // Getters y setters:

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getActualizacion_disponible() {
        return actualizacion_disponible;
    }

    public void setActualizacion_disponible(Boolean actualizacion_disponible) {
        this.actualizacion_disponible = actualizacion_disponible;
    }

    public Boolean getActualizacion_necesaria() {
        return actualizacion_necesaria;
    }

    public void setActualizacion_necesaria(Boolean actualizacion_necesaria) {
        this.actualizacion_necesaria = actualizacion_necesaria;
    }

    public Boolean getApi_google_on() {
        return api_google_on;
    }

    public void setApi_google_on(Boolean api_google_on) {
        this.api_google_on = api_google_on;
    }

    public Integer getApi_google_limite() {
        return api_google_limite;
    }

    public void setApi_google_limite(Integer api_google_limite) {
        this.api_google_limite = api_google_limite;
    }

    public String getApi_google_url() {
        return api_google_url;
    }

    public void setApi_google_url(String api_google_url) {
        this.api_google_url = api_google_url;
    }

    public Boolean getApi_bing_gratis_on() {
        return api_bing_gratis_on;
    }

    public void setApi_bing_gratis_on(Boolean api_bing_gratis_on) {
        this.api_bing_gratis_on = api_bing_gratis_on;
    }

    public String getApi_bing_gratis_url() {
        return api_bing_gratis_url;
    }

    public void setApi_bing_gratis_url(String api_bing_gratis_url) {
        this.api_bing_gratis_url = api_bing_gratis_url;
    }

    public Boolean getApi_bing_pago_on() {
        return api_bing_pago_on;
    }

    public void setApi_bing_pago_on(Boolean api_bing_pago_on) {
        this.api_bing_pago_on = api_bing_pago_on;
    }

    public Integer getApi_bing_pago_limite() {
        return api_bing_pago_limite;
    }

    public void setApi_bing_pago_limite(Integer api_bing_pago_limite) {
        this.api_bing_pago_limite = api_bing_pago_limite;
    }

    public String getApi_bing_pago_url() {
        return api_bing_pago_url;
    }

    public void setApi_bing_pago_url(String api_bing_pago_url) {
        this.api_bing_pago_url = api_bing_pago_url;
    }
}
