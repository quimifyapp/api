package com.quimify.api.metrics;

import com.quimify.api.client.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

// Esta clase procesa las métricas diarias.

@Service
public
class MetricsService {

    @Autowired
    MetricsRepository metricsRepository; // Conexión con la DB

    // PRIVADOS ----------------------------------------------------------------------

    private MetricsModel getTodayMetrics() {
        MetricsModel todayMetrics;

        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Optional<MetricsModel> latestMetrics = metricsRepository.findById(today);

        if(latestMetrics.isEmpty())
            todayMetrics = metricsRepository.save(new MetricsModel(today));
        else todayMetrics = latestMetrics.get();

        return todayMetrics;
    }

    // PÚBLICOS ----------------------------------------------------------------------

    public Integer getBusquedasGoogle() {
        MetricsModel metricas = getTodayMetrics();

        return metricas.getGoogleFoundFromPicture() + metricas.getGoogleNotFoundFromPicture() +
                metricas.getGoogleFoundFromText() + metricas.getGoogleNotFoundFromText();
    }

    public Integer getBusquedasBingPago() {
        return getTodayMetrics().getPaidBingQueries();
    }

    // Contadores:

    @Transactional
    public void contarAcceso(Short platform) {
        switch(platform) {
            case ClientService.androidPlatform:
                getTodayMetrics().nuevoAccesoAndroid();
                break;
            case ClientService.iOSPlatform:
                getTodayMetrics().nuevoAccesoIOS();
                break;
            case ClientService.webPlatform:
                getTodayMetrics().nuevoAccesoWeb();
                break;
        }
    }

    @Transactional
    public void contarInorganicoBuscado(boolean encontrado, boolean foto) {
        if(encontrado) {
            if(foto)
                getTodayMetrics().nuevoInorganicoFotoEncontrado();
            else getTodayMetrics().nuevoInorganicoTecladoEncontrado();
        }
        else {
            if(foto)
                getTodayMetrics().nuevoInorganicoFotoNoEncontrado();
            else getTodayMetrics().nuevoInorganicoTecladoNoEncontrado();
        }
    }

    @Transactional
    public void contarGoogle(boolean encontrado, boolean foto) {
        if(encontrado) {
            if(foto)
                getTodayMetrics().nuevoGoogleFotoEncontrado();
            else getTodayMetrics().nuevoGoogleTecladoEncontrado();
        }
        else {
            if(foto)
                getTodayMetrics().nuevoGoogleFotoNoEncontrado();
            else getTodayMetrics().nuevoGoogleTecladoNoEncontrado();
        }
    }

    @Transactional
    public void contarBing(boolean encontrado, boolean foto) {
        if(encontrado) {
            if(foto)
                getTodayMetrics().nuevoBingFotoEncontrado();
            else getTodayMetrics().nuevoBingTecladoEncontrado();
        }
        else {
            if(foto)
                getTodayMetrics().nuevoBingFotoNoEncontrado();
            else getTodayMetrics().nuevoBingTecladoNoEncontrado();
        }
    }

    @Transactional
    public void contarBingPago() {
        getTodayMetrics().nuevoBingPagoBuscado();
    }

    @Transactional
    public void contarInorganicoNuevo() {
        getTodayMetrics().nuevoInorganicoNuevo();
    }

    @Transactional
    public void contarInorganicoAutocompletado() {
        getTodayMetrics().nuevoInorganicoAutocompletado();
    }

    @Transactional
    public void contarFormularOrganico(boolean encontrado, boolean foto) {
        if (encontrado) {
            if (foto)
                getTodayMetrics().nuevoFormularOrganicoFotoEncontrado();
            else getTodayMetrics().nuevoFormularOrganicoTecladoEncontrado();
        } else {
            if (foto)
                getTodayMetrics().nuevoFormularOrganicoFotoNoEncontrado();
            else getTodayMetrics().nuevoFormularOrganicoTecladoNoEncontrado();
        }
    }

    @Transactional
    public void contarNombrarOrganicoAbiertoBuscado() {
        getTodayMetrics().nuevoNombrarOrganicoAbiertoBuscado();
    }

    @Transactional
    public void countOrganicsFailedFromStructure() {
        getTodayMetrics().countOrganicsFailedFromStructure();
    }

    @Transactional
    public void contarMasaMolecular(boolean encontrado) {
        if(encontrado)
            getTodayMetrics().nuevoMasaMolecularEncontrado();
        else getTodayMetrics().nuevoMasaMolecularNoEncontrado();
    }

    @Transactional
    public void contarReporte() {
        getTodayMetrics().nuevoReporte();
    }

}
