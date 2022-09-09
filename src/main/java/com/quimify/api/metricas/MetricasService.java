package com.quimify.api.metricas;

import com.quimify.api.cliente.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

// Esta clase procesa las métricas diarias.

@Service
public class MetricasService {

    @Autowired
    MetricasRepository metricaRepository; // Conexión con la DB

    // PRIVADOS ----------------------------------------------------------------------

    private MetricasModel metricasDeHoy() {
        MetricasModel actual;

        Date hoy = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Optional<MetricasModel> ultima = metricaRepository.findById(hoy);

        if(ultima.isEmpty())
            actual = metricaRepository.save(new MetricasModel(hoy));
        else actual = ultima.get();

        return actual;
    }

    // PÚBLICOS ----------------------------------------------------------------------

    public Integer getBusquedasGoogle() {
        MetricasModel metricas = metricasDeHoy();

        return metricas.getGoogle_foto_encontrados() + metricas.getGoogle_foto_no_encontrados() +
                metricas.getGoogle_teclado_encontrados() + metricas.getGoogle_teclado_no_encontrados();
    }

    public Integer getBusquedasBingPago() {
        return metricasDeHoy().getBing_pago_buscados();
    }

    // Contadores:

    @Transactional
    public void contarAcceso(Short plataforma) {
        switch(plataforma) {
            case ClienteService.ANDROID:
                metricasDeHoy().nuevoAccesoAndroid();
                break;
            case ClienteService.IOS:
                metricasDeHoy().nuevoAccesoIOS();
                break;
            case ClienteService.WEB:
                metricasDeHoy().nuevoAccesoWeb();
                break;
        }
    }

    @Transactional
    public void contarInorganicoBuscado(boolean encontrado, boolean foto) {
        if(encontrado) {
            if(foto)
                metricasDeHoy().nuevoInorganicoFotoEncontrado();
            else metricasDeHoy().nuevoInorganicoTecladoEncontrado();
        }
        else {
            if(foto)
                metricasDeHoy().nuevoInorganicoFotoNoEncontrado();
            else metricasDeHoy().nuevoInorganicoTecladoNoEncontrado();
        }
    }

    @Transactional
    public void contarGoogle(boolean encontrado, boolean foto) {
        if(encontrado) {
            if(foto)
                metricasDeHoy().nuevoGoogleFotoEncontrado();
            else metricasDeHoy().nuevoGoogleTecladoEncontrado();
        }
        else {
            if(foto)
                metricasDeHoy().nuevoGoogleFotoNoEncontrado();
            else metricasDeHoy().nuevoGoogleTecladoNoEncontrado();
        }
    }

    @Transactional
    public void contarBing(boolean encontrado, boolean foto) {
        if(encontrado) {
            if(foto)
                metricasDeHoy().nuevoBingFotoEncontrado();
            else metricasDeHoy().nuevoBingTecladoEncontrado();
        }
        else {
            if(foto)
                metricasDeHoy().nuevoBingFotoNoEncontrado();
            else metricasDeHoy().nuevoBingTecladoNoEncontrado();
        }
    }

    @Transactional
    public void contarBingPago() {
        metricasDeHoy().nuevoBingPagoBuscado();
    }

    @Transactional
    public void contarInorganicoNuevo() {
        metricasDeHoy().nuevoInorganicoNuevo();
    }

    @Transactional
    public void contarInorganicoAutocompletado() {
        metricasDeHoy().nuevoInorganicoAutocompletado();
    }

    @Transactional
    public void contarFormularOrganico(boolean encontrado, boolean foto) {
        if(encontrado) {
            if(foto)
                metricasDeHoy().nuevoFormularOrganicoFotoEncontrado();
            else metricasDeHoy().nuevoFormularOrganicoTecladoEncontrado();
        }
        else {
            if(foto)
                metricasDeHoy().nuevoFormularOrganicoFotoNoEncontrado();
            else metricasDeHoy().nuevoFormularOrganicoTecladoNoEncontrado();
        }
    }

    @Transactional
    public void contarNombrarOrganicoSimpleBuscado() {
        metricasDeHoy().nuevoNombrarOrganicoSimpleBuscado();
    }

    @Transactional
    public void contarNombrarOrganicoEterBuscado() {
        metricasDeHoy().nuevoNombrarOrganicoEterBuscado();
    }

    @Transactional
    public void contarMasaMolecular(boolean encontrado) {
        if(encontrado)
            metricasDeHoy().nuevoMasaMolecularEncontrado();
        else metricasDeHoy().nuevoMasaMolecularNoEncontrado();
    }

}
