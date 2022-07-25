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

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer accesos = 0; // Accesos al cliente

    // Su suma equivale al total de peticiones:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer teclado_encontrados = 0; // Búsquedas por teclado encontradas

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer teclado_sugerencias = 0; // Búsquedas por teclado que devuelven sugerencia

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer teclado_no_encontrados = 0; // Búsquedas por teclado no encontradas

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer camara_encontrados = 0; // Búsquedas por cámara encontradas

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer camara_sugerencias = 0; // Búsquedas por cámara que devuelven sugerencia

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer camara_no_encontrados = 0; // Búsquedas por cámara no encontradas

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer galeria_encontrados = 0; // Búsquedas por galería encontradas

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer galeria_sugerencias = 0; // Búsquedas por galería que devuelven sugerencia

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer galeria_no_encontrados = 0; // Búsquedas por galería no encontradas

    // Otras:

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer teclado_premiums = 0; // Búsquedas por teclado encontradas que son premium

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer teclado_sugerencias_ok = 0; // Búsquedas por teclado que devuelven sugerencia acertada

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer teclado_google = 0; // Búsquedas por teclado que usaron la API de Google

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer teclado_bing_gratis = 0; // Búsquedas por teclado que usaron la API de Bing gratis

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer teclado_bing_pago = 0; // Búsquedas por teclado que usaron la API de Bing de pago

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer teclado_compleciones_ok = 0; // Compleciones clickadas por el usuario

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer camara_premiums = 0; // Búsquedas por cámara encontradas que son premium

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer camara_sugerencias_ok = 0; // Búsquedas por cámara que devuelven sugerencia acertada

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer camara_google = 0; // Búsquedas por cámara que usaron la API de Google

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer camara_bing_gratis = 0; // Búsquedas por cámara que usaron la API de Bing gratis

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer camara_bing_pago = 0; // Búsquedas por cámara que usaron la API de Bing de pago

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer galeria_premiums = 0; // Búsquedas por galería encontradas que son premium

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer galeria_sugerencias_ok = 0; // Búsquedas por galería que devuelven sugerencia acertada

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer galeria_google = 0; // Búsquedas por galería que usaron la API de Google

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer galeria_bing_gratis = 0; // Búsquedas por galería que usaron la API de Bing gratis

    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer galeria_bing_pago = 0; // Búsquedas por galería que usaron la API de Bing de pago

    // --------------------------------------------------------------------------------

    // Constructores:

    public MetricasModel() {}

    public MetricasModel(Date dia) {
        setDia(dia);
    }

    // Incrementadores:

    public void nuevoAcceso() {
       accesos += 1;
    }
    public void nuevoEncontradoPorTeclado() {
        teclado_encontrados += 1;
    }

    public void nuevaSugerenciaPorTeclado() {
       teclado_sugerencias += 1;
    }

    public void nuevoNoEncontradoPorTeclado() {
       teclado_no_encontrados += 1;
    }

    public void nuevoEncontradoPorCamara() {
        camara_encontrados += 1;
    }

    public void nuevaSugerenciaPorCamara() {
        camara_sugerencias += 1;
    }

    public void nuevoNoEncontradoPorCamara() {
        camara_no_encontrados += 1;
    }

    public void nuevoEncontradoPorGaleria() {
        galeria_encontrados += 1;
    }

    public void nuevaSugerenciaPorGaleria() {
        galeria_sugerencias += 1;
    }

    public void nuevoNoEncontradoPorGaleria() {
        galeria_no_encontrados += 1;
    }

    public void nuevoPremiumPorTeclado(){
        teclado_premiums += 1;
    }

    public void nuevaSugerenciaOkPorTeclado() {
        teclado_sugerencias_ok += 1;
    }

    public void nuevoGooglePorTeclado() {
        teclado_google += 1;
    }

    public void nuevoBingGratisPorTeclado() {
        teclado_bing_gratis += 1;
    }

    public void nuevoBingPagoPorTeclado() {
        teclado_bing_pago += 1;
    }

    public void nuevaComplecionOkPorTeclado() {
        teclado_compleciones_ok += 1;
    }

    public void nuevoPremiumPorCamara(){
        camara_premiums += 1;
    }

    public void nuevaSugerenciaOkPorCamara() {
        camara_sugerencias_ok += 1;
    }

    public void nuevoGooglePorCamara() {
        camara_google += 1;
    }

    public void nuevoBingGratisPorCamara() {
        camara_bing_gratis += 1;
    }

    public void nuevoBingPagoPorCamara() {
        camara_bing_pago += 1;
    }

    public void nuevoPremiumPorGaleria(){
        galeria_premiums += 1;
    }

    public void nuevaSugerenciaOkPorGaleria() {
        galeria_sugerencias_ok += 1;
    }

    public void nuevoGooglePorGaleria() {
        galeria_google += 1;
    }

    public void nuevoBingGratisPorGaleria() {
        galeria_bing_gratis += 1;
    }

    public void nuevoBingPagoPorGaleria() {
        galeria_bing_pago += 1;
    }

    // Getters y setters:

    public Date getDia() {
        return dia;
    }

    public void setDia(Date dia) {
        this.dia = dia;
    }

    public Integer getAccesos() {
        return accesos;
    }

    public void setAccesos(Integer accesos) {
        this.accesos = accesos;
    }

    public Integer getTeclado_encontrados() {
        return teclado_encontrados;
    }

    public void setTeclado_encontrados(Integer teclado_encontrados) {
        this.teclado_encontrados = teclado_encontrados;
    }

    public Integer getTeclado_sugerencias() {
        return teclado_sugerencias;
    }

    public void setTeclado_sugerencias(Integer teclado_sugerencias) {
        this.teclado_sugerencias = teclado_sugerencias;
    }

    public Integer getTeclado_no_encontrados() {
        return teclado_no_encontrados;
    }

    public void setTeclado_no_encontrados(Integer teclado_no_encontrados) {
        this.teclado_no_encontrados = teclado_no_encontrados;
    }

    public Integer getCamara_encontrado() {
        return camara_encontrados;
    }

    public void setCamara_encontrados(Integer camara_encontrados) {
        this.camara_encontrados = camara_encontrados;
    }

    public Integer getCamara_sugerencia() {
        return camara_sugerencias;
    }

    public void setCamara_sugerencias(Integer camara_sugerencias) {
        this.camara_sugerencias = camara_sugerencias;
    }

    public Integer getCamara_no_encontrado() {
        return camara_no_encontrados;
    }

    public void setCamara_no_encontrados(Integer camara_no_encontrados) {
        this.camara_no_encontrados = camara_no_encontrados;
    }

    public Integer getGaleria_encontrado() {
        return galeria_encontrados;
    }

    public void setGaleria_encontrados(Integer galeria_encontrados) {
        this.galeria_encontrados = galeria_encontrados;
    }

    public Integer getGaleria_sugerencia() {
        return galeria_sugerencias;
    }

    public void setGaleria_sugerencias(Integer galeria_sugerencias) {
        this.galeria_sugerencias = galeria_sugerencias;
    }

    public Integer getGaleria_no_encontrado() {
        return galeria_no_encontrados;
    }

    public void setGaleria_no_encontrados(Integer galeria_no_encontrados) {
        this.galeria_no_encontrados = galeria_no_encontrados;
    }

    public Integer getTeclado_premium() {
        return teclado_premiums;
    }

    public void setTeclado_premiums(Integer teclado_premiums) {
        this.teclado_premiums = teclado_premiums;
    }

    public Integer getTeclado_sugerencia_ok() {
        return teclado_sugerencias_ok;
    }

    public void setTeclado_sugerencias_ok(Integer teclado_sugerencias_ok) {
        this.teclado_sugerencias_ok = teclado_sugerencias_ok;
    }

    public Integer getTeclado_google() {
        return teclado_google;
    }

    public void setTeclado_google(Integer teclado_google) {
        this.teclado_google = teclado_google;
    }

    public Integer getTeclado_bing_gratis() {
        return teclado_bing_gratis;
    }

    public void setTeclado_bing_gratis(Integer teclado_bing_gratis) {
        this.teclado_bing_gratis = teclado_bing_gratis;
    }

    public Integer getTeclado_bing_pago() {
        return teclado_bing_pago;
    }

    public void setTeclado_bing_pago(Integer teclado_bing_pago) {
        this.teclado_bing_pago = teclado_bing_pago;
    }

    public Integer getTeclado_complecion_ok() {
        return teclado_compleciones_ok;
    }

    public void setTeclado_complecion_ok(Integer teclado_complecion_ok) {
        this.teclado_compleciones_ok = teclado_complecion_ok;
    }

    public Integer getCamara_premium() {
        return camara_premiums;
    }

    public void setCamara_premiums(Integer camara_premiums) {
        this.camara_premiums = camara_premiums;
    }

    public Integer getCamara_sugerencia_ok() {
        return camara_sugerencias_ok;
    }

    public void setCamara_sugerencias_ok(Integer camara_sugerencias_ok) {
        this.camara_sugerencias_ok = camara_sugerencias_ok;
    }

    public Integer getCamara_google() {
        return camara_google;
    }

    public void setCamara_google(Integer camara_google) {
        this.camara_google = camara_google;
    }

    public Integer getCamara_bing_gratis() {
        return camara_bing_gratis;
    }

    public void setCamara_bing_gratis(Integer camara_bing_gratis) {
        this.camara_bing_gratis = camara_bing_gratis;
    }

    public Integer getCamara_bing_pago() {
        return camara_bing_pago;
    }

    public void setCamara_bing_pago(Integer camara_bing_pago) {
        this.camara_bing_pago = camara_bing_pago;
    }

    public Integer getGaleria_premium() {
        return galeria_premiums;
    }

    public void setGaleria_premiums(Integer galeria_premiums) {
        this.galeria_premiums = galeria_premiums;
    }

    public Integer getGaleria_sugerencia_ok() {
        return galeria_sugerencias_ok;
    }

    public void setGaleria_sugerencias_ok(Integer galeria_sugerencias_ok) {
        this.galeria_sugerencias_ok = galeria_sugerencias_ok;
    }

    public Integer getGaleria_google() {
        return galeria_google;
    }

    public void setGaleria_google(Integer galeria_google) {
        this.galeria_google = galeria_google;
    }

    public Integer getGaleria_bing_gratis() {
        return galeria_bing_gratis;
    }

    public void setGaleria_bing_gratis(Integer galeria_bing_gratis) {
        this.galeria_bing_gratis = galeria_bing_gratis;
    }

    public Integer getGaleria_bing_pago() {
        return galeria_bing_pago;
    }

    public void setGaleria_bing_pago(Integer galeria_bing_pago) {
        this.galeria_bing_pago = galeria_bing_pago;
    }

}
