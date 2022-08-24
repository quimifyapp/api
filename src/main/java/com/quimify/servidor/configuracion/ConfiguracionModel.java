package com.quimify.servidor.configuracion;

import javax.persistence.*;

// Esta clase representa los compuestos inorgánicos.

@Entity // Es un modelo real
@Table(name = "configuracion") // En la tabla 'configuracion' de la DB
public class ConfiguracionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer version; // Se corresponde con la versión de la API

    // Actualizaciones: esto es una prueba

    @Column(nullable = false)
    private Boolean actualizacion_disponible;
    @Column(nullable = false)
    private Boolean actualizacion_necesaria;

    // API de Google:

    @Column(nullable = false)
    private Boolean google_on = false; // Interruptor
    @Column(nullable = false)
    private Integer google_limite; // Nº máx. de búsquedas diarias
    private String google_url; // URL + key

    // API de Bing:

    private String bing_url; // URL

    // API de Bing gratis:

    @Column(nullable = false)
    private Boolean bing_gratis_on = false; // Interruptor
    private String bing_gratis_key; // Suscripcion

    // API de Bing de pago:

    @Column(nullable = false)
    private Boolean bing_pago_on = false; // Interruptor
    @Column(nullable = false)
    private Integer bing_pago_limite; // Nº máx. de búsquedas diarias
    private String bing_pago_key; // Suscripcion

    // Para FQ.com:

    @Column(nullable = false)
    private String user_agent; // Requisito HTTP para parecer un visitante corriente

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

    public Boolean getGoogle_on() {
        return google_on;
    }

    public void setGoogle_on(Boolean google_on) {
        this.google_on = google_on;
    }

    public Integer getGoogle_limite() {
        return google_limite;
    }

    public void setGoogle_limite(Integer google_limite) {
        this.google_limite = google_limite;
    }

    public String getGoogle_url() {
        return google_url;
    }

    public void setGoogle_url(String google_url) {
        this.google_url = google_url;
    }

    public String getBing_url() {
        return bing_url;
    }

    public void setBing_url(String bing_url) {
        this.bing_url = bing_url;
    }

    public Boolean getBing_gratis_on() {
        return bing_gratis_on;
    }

    public void setBing_gratis_on(Boolean bing_gratis_on) {
        this.bing_gratis_on = bing_gratis_on;
    }

    public String getBing_gratis_key() {
        return bing_gratis_key;
    }

    public void setBing_gratis_key(String bing_gratis_key) {
        this.bing_gratis_key = bing_gratis_key;
    }

    public Boolean getBing_pago_on() {
        return bing_pago_on;
    }

    public void setBing_pago_on(Boolean bing_pago_on) {
        this.bing_pago_on = bing_pago_on;
    }

    public Integer getBing_pago_limite() {
        return bing_pago_limite;
    }

    public void setBing_pago_limite(Integer bing_pago_limite) {
        this.bing_pago_limite = bing_pago_limite;
    }

    public String getBing_pago_key() {
        return bing_pago_key;
    }

    public void setBing_pago_key(String bing_pago_key) {
        this.bing_pago_key = bing_pago_key;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }

}