package com.quimify.servidor.configuracion;

import javax.persistence.*;

// Esta clase representa la configuración de cada versión del servidor.

@Entity // Es un modelo real
@Table(name = "configuracion") // En la tabla 'configuracion' de la DB
public class ConfiguracionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer version; // Se corresponde con la versión del servidor

    // Bienvenida:

    @Column(nullable = false)
    private Boolean actualizacion_disponible;
    @Column()
    private Boolean actualizacion_obligatoria;
    @Column()
    private String actualizacion_detalles;

    @Column(nullable = false)
    private Boolean mensaje_presente;
    @Column()
    private String mensaje_titulo;
    @Column()
    private String mensaje_detalles;
    @Column()
    private Boolean mensaje_enlace_presente;
    @Column()
    private String mensaje_enlace_nombre;
    @Column()
    private String mensaje_enlace;

    // API de Google:

    @Column(nullable = false)
    private Boolean google_on = false; // Interruptor
    @Column(nullable = false)
    private Integer google_limite; // Nº máx. de búsquedas diarias
    @Column(nullable = false)
    private String google_url; // URL + key

    // API de Bing gratis:

    @Column(nullable = false)
    private Boolean bing_gratis_on = false; // Interruptor
    @Column(nullable = false)
    private String bing_gratis_key; // Suscripción

    // API de Bing de pago:

    @Column(nullable = false)
    private Boolean bing_pago_on = false; // Interruptor
    @Column(nullable = false)
    private Integer bing_pago_limite; // Nº máx. de búsquedas diarias
    @Column(nullable = false)
    private String bing_pago_key; // Suscripción

    // API de Bing:

    @Column(nullable = false)
    private String bing_url; // URL

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

    public Boolean getActualizacion_obligatoria() {
        return actualizacion_obligatoria;
    }

    public void setActualizacion_obligatoria(Boolean actualizacion_obligatoria) {
        this.actualizacion_obligatoria = actualizacion_obligatoria;
    }

    public String getActualizacion_detalles() {
        return actualizacion_detalles;
    }

    public void setActualizacion_detalles(String actualizacion_detalles) {
        this.actualizacion_detalles = actualizacion_detalles;
    }

    public Boolean getMensaje_presente() {
        return mensaje_presente;
    }

    public void setMensaje_presente(Boolean mensaje_presente) {
        this.mensaje_presente = mensaje_presente;
    }

    public String getMensaje_titulo() {
        return mensaje_titulo;
    }

    public void setMensaje_titulo(String mensaje_titulo) {
        this.mensaje_titulo = mensaje_titulo;
    }

    public String getMensaje_detalles() {
        return mensaje_detalles;
    }

    public void setMensaje_detalles(String mensaje_detalles) {
        this.mensaje_detalles = mensaje_detalles;
    }

    public Boolean getMensaje_enlace_presente() {
        return mensaje_enlace_presente;
    }

    public void setMensaje_enlace_presente(Boolean mensaje_enlace_presente) {
        this.mensaje_enlace_presente = mensaje_enlace_presente;
    }

    public String getMensaje_enlace_nombre() {
        return mensaje_enlace_nombre;
    }

    public void setMensaje_enlace_nombre(String mensaje_enlace_nombre) {
        this.mensaje_enlace_nombre = mensaje_enlace_nombre;
    }

    public String getMensaje_enlace() {
        return mensaje_enlace;
    }

    public void setMensaje_enlace(String mensaje_enlace) {
        this.mensaje_enlace = mensaje_enlace;
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
