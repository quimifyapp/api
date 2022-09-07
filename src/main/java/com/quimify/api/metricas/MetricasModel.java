package com.quimify.api.metricas;

// Esta clase representa un día de métricas.

import javax.persistence.*;
import java.util.Date;

@Entity // Es un modelo real
@Table(name = "metricas") // En la tabla 'metricas' de la DB
public class MetricasModel {

    @Id
    @Temporal(TemporalType.DATE)
    private Date dia;

    // Su suma equivale al total de accesos al cliente:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer accesos_android = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer accesos_ios = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer accesos_web = 0;

    // Su suma equivale al total de peticiones de inorgánicos:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganico_teclado_encontrados = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganico_teclado_no_encontrados = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganico_foto_encontrados = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganico_foto_no_encontrados = 0;

    // Su suma equivale al total de búsquedas web de inorgánicos:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer google_teclado_encontrados = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer google_teclado_no_encontrados = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer google_foto_encontrados = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer google_foto_no_encontrados = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer bing_teclado_encontrados = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer bing_teclado_no_encontrados = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer bing_foto_encontrados = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer bing_foto_no_encontrados = 0;

    // Aparte, las búsquedas en Bing *de pago* para poder limitarlas:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer bing_pago_buscados = 0;

    // Inorgánicos añadidos a la base de datos:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganico_nuevos = 0;

    // Compleciones correctas de inorgánicos:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer inorganico_autocompletados = 0;

    // Su suma equivale al total de peticiones de formular orgánicos:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer formular_organico_teclado_encontrados = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer formular_organico_teclado_no_encontrados = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer formular_organico_foto_encontrados = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer formular_organico_foto_no_encontrados = 0;

    // Su suma equivale al total de peticiones de nombrar orgánicos:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer nombrar_organico_simple_buscados = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer nombrar_organico_eter_buscados = 0;

    // Su suma equivale al total de peticiones de calcular masas moleculares:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer masa_molecular_encontrados = 0;
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer masa_molecular_no_encontrados = 0;

    // --------------------------------------------------------------------------------

    // Constructores:

    public MetricasModel() {} // Lo pide JPA

    public MetricasModel(Date dia) {
        setDia(dia);
    }

    // Incrementadores:

    public void nuevoAccesoAndroid() {
        accesos_android += 1;
    }

    public void nuevoAccesoIOS() {
        accesos_ios += 1;
    }

    public void nuevoAccesoWeb() {
        accesos_web += 1;
    }

    public void nuevoInorganicoTecladoEncontrado() {
        inorganico_teclado_encontrados += 1;
    }

    public void nuevoInorganicoTecladoNoEncontrado() {
        inorganico_teclado_no_encontrados += 1;
    }

    public void nuevoInorganicoFotoEncontrado() {
        inorganico_foto_encontrados += 1;
    }

    public void nuevoInorganicoFotoNoEncontrado() {
        inorganico_foto_no_encontrados += 1;
    }

    public void nuevoGoogleTecladoEncontrado() {
        google_teclado_encontrados += 1;
    }

    public void nuevoGoogleTecladoNoEncontrado() {
        google_teclado_no_encontrados += 1;
    }

    public void nuevoGoogleFotoEncontrado() {
        google_foto_encontrados += 1;
    }

    public void nuevoGoogleFotoNoEncontrado() {
        google_foto_no_encontrados += 1;
    }

    public void nuevoBingTecladoEncontrado() {
        bing_teclado_encontrados += 1;
    }

    public void nuevoBingTecladoNoEncontrado() {
        bing_teclado_no_encontrados += 1;
    }

    public void nuevoBingFotoEncontrado() {
        bing_foto_encontrados += 1;
    }

    public void nuevoBingFotoNoEncontrado() {
        bing_foto_no_encontrados += 1;
    }

    public void nuevoBingPagoBuscado() {
        bing_pago_buscados += 1;
    }

    public void nuevoInorganicoNuevo() {
        inorganico_nuevos += 1;
    }

    public void nuevoInorganicoAutocompletado() {
        inorganico_autocompletados += 1;
    }

    public void nuevoFormularOrganicoTecladoEncontrado() {
        formular_organico_teclado_encontrados += 1;
    }

    public void nuevoFormularOrganicoTecladoNoEncontrado() {
        formular_organico_teclado_no_encontrados += 1;
    }

    public void nuevoFormularOrganicoFotoEncontrado() {
        formular_organico_foto_encontrados += 1;
    }

    public void nuevoFormularOrganicoFotoNoEncontrado() {
        formular_organico_foto_no_encontrados += 1;
    }

    public void nuevoNombrarOrganicoSimpleBuscado() {
        nombrar_organico_simple_buscados += 1;
    }

    public void nuevoNombrarOrganicoEterBuscado() {
        nombrar_organico_eter_buscados += 1;
    }

    public void nuevoMasaMolecularEncontrado() {
        masa_molecular_encontrados += 1;
    }

    public void nuevoMasaMolecularNoEncontrado() {
        masa_molecular_no_encontrados += 1;
    }

    // Getters y setters:

    public Date getDia() {
        return dia;
    }

    public void setDia(Date dia) {
        this.dia = dia;
    }

    public Integer getAccesos_android() {
        return accesos_android;
    }

    public void setAccesos_android(Integer accesos_android) {
        this.accesos_android = accesos_android;
    }

    public Integer getAccesos_ios() {
        return accesos_ios;
    }

    public void setAccesos_ios(Integer accesos_ios) {
        this.accesos_ios = accesos_ios;
    }

    public Integer getAccesos_web() {
        return accesos_web;
    }

    public void setAccesos_web(Integer accesos_web) {
        this.accesos_web = accesos_web;
    }

    public Integer getInorganico_teclado_encontrados() {
        return inorganico_teclado_encontrados;
    }

    public void setInorganico_teclado_encontrados(Integer inorganico_teclado_encontrados) {
        this.inorganico_teclado_encontrados = inorganico_teclado_encontrados;
    }

    public Integer getInorganico_teclado_no_encontrados() {
        return inorganico_teclado_no_encontrados;
    }

    public void setInorganico_teclado_no_encontrados(Integer inorganico_teclado_no_encontrados) {
        this.inorganico_teclado_no_encontrados = inorganico_teclado_no_encontrados;
    }

    public Integer getInorganico_foto_encontrados() {
        return inorganico_foto_encontrados;
    }

    public void setInorganico_foto_encontrados(Integer inorganico_foto_encontrados) {
        this.inorganico_foto_encontrados = inorganico_foto_encontrados;
    }

    public Integer getInorganico_foto_no_encontrados() {
        return inorganico_foto_no_encontrados;
    }

    public void setInorganico_foto_no_encontrados(Integer inorganico_foto_no_encontrados) {
        this.inorganico_foto_no_encontrados = inorganico_foto_no_encontrados;
    }

    public Integer getGoogle_teclado_encontrados() {
        return google_teclado_encontrados;
    }

    public void setGoogle_teclado_encontrados(Integer google_teclado_encontrados) {
        this.google_teclado_encontrados = google_teclado_encontrados;
    }

    public Integer getGoogle_teclado_no_encontrados() {
        return google_teclado_no_encontrados;
    }

    public void setGoogle_teclado_no_encontrados(Integer google_teclado_no_encontrados) {
        this.google_teclado_no_encontrados = google_teclado_no_encontrados;
    }

    public Integer getGoogle_foto_encontrados() {
        return google_foto_encontrados;
    }

    public void setGoogle_foto_encontrados(Integer google_foto_encontrados) {
        this.google_foto_encontrados = google_foto_encontrados;
    }

    public Integer getGoogle_foto_no_encontrados() {
        return google_foto_no_encontrados;
    }

    public void setGoogle_foto_no_encontrados(Integer google_foto_no_encontrados) {
        this.google_foto_no_encontrados = google_foto_no_encontrados;
    }

    public Integer getBing_teclado_encontrados() {
        return bing_teclado_encontrados;
    }

    public void setBing_teclado_encontrados(Integer bing_teclado_encontrados) {
        this.bing_teclado_encontrados = bing_teclado_encontrados;
    }

    public Integer getBing_teclado_no_encontrados() {
        return bing_teclado_no_encontrados;
    }

    public void setBing_teclado_no_encontrados(Integer bing_teclado_no_encontrados) {
        this.bing_teclado_no_encontrados = bing_teclado_no_encontrados;
    }

    public Integer getBing_foto_encontrados() {
        return bing_foto_encontrados;
    }

    public void setBing_foto_encontrados(Integer bing_foto_encontrados) {
        this.bing_foto_encontrados = bing_foto_encontrados;
    }

    public Integer getBing_foto_no_encontrados() {
        return bing_foto_no_encontrados;
    }

    public void setBing_foto_no_encontrados(Integer bing_foto_no_encontrados) {
        this.bing_foto_no_encontrados = bing_foto_no_encontrados;
    }

    public Integer getBing_pago_buscados() {
        return bing_pago_buscados;
    }

    public void setBing_pago_buscados(Integer bing_pago_buscados) {
        this.bing_pago_buscados = bing_pago_buscados;
    }

    public Integer getInorganico_nuevos() {
        return inorganico_nuevos;
    }

    public void setInorganico_nuevos(Integer inorganico_nuevos) {
        this.inorganico_nuevos = inorganico_nuevos;
    }

    public Integer getInorganico_autocompletados() {
        return inorganico_autocompletados;
    }

    public void setInorganico_autocompletados(Integer inorganico_autocompletados) {
        this.inorganico_autocompletados = inorganico_autocompletados;
    }

    public Integer getFormular_organico_teclado_encontrados() {
        return formular_organico_teclado_encontrados;
    }

    public void setFormular_organico_teclado_encontrados(Integer formular_organico_teclado_encontrados) {
        this.formular_organico_teclado_encontrados = formular_organico_teclado_encontrados;
    }

    public Integer getFormular_organico_teclado_no_encontrados() {
        return formular_organico_teclado_no_encontrados;
    }

    public void setFormular_organico_teclado_no_encontrados(Integer formular_organico_teclado_no_encontrados) {
        this.formular_organico_teclado_no_encontrados = formular_organico_teclado_no_encontrados;
    }

    public Integer getFormular_organico_foto_encontrados() {
        return formular_organico_foto_encontrados;
    }

    public void setFormular_organico_foto_encontrados(Integer formular_organico_foto_encontrados) {
        this.formular_organico_foto_encontrados = formular_organico_foto_encontrados;
    }

    public Integer getFormular_organico_foto_no_encontrados() {
        return formular_organico_foto_no_encontrados;
    }

    public void setFormular_organico_foto_no_encontrados(Integer formular_organico_foto_no_encontrados) {
        this.formular_organico_foto_no_encontrados = formular_organico_foto_no_encontrados;
    }

    public Integer getNombrar_organico_simple_buscados() {
        return nombrar_organico_simple_buscados;
    }

    public void setNombrar_organico_simple_buscados(Integer nombrar_organico_simple_buscados) {
        this.nombrar_organico_simple_buscados = nombrar_organico_simple_buscados;
    }

    public Integer getNombrar_organico_eter_buscados() {
        return nombrar_organico_eter_buscados;
    }

    public void setNombrar_organico_eter_buscados(Integer nombrar_organico_eter_buscados) {
        this.nombrar_organico_eter_buscados = nombrar_organico_eter_buscados;
    }

    public Integer getMasa_molecular_encontrados() {
        return masa_molecular_encontrados;
    }

    public void setMasa_molecular_encontrados(Integer masa_molecular_encontrados) {
        this.masa_molecular_encontrados = masa_molecular_encontrados;
    }

    public Integer getMasa_molecular_no_encontrados() {
        return masa_molecular_no_encontrados;
    }

    public void setMasa_molecular_no_encontrados(Integer masa_molecular_no_encontrados) {
        this.masa_molecular_no_encontrados = masa_molecular_no_encontrados;
    }

}
