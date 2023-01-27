package com.quimify.api.metrics;

import com.quimify.api.client.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.Date;
import java.util.Optional;

// Esta clase procesa las métricas diarias.

@Service
public
class MetricsService {

    @Autowired
    MetricsRepository metricsRepository; // Conexión con la DB

    // Day starts at 13:00 of Spain and 07:00 of Bolivia
    private static final Duration offSet = Duration.ofHours(-13);

    // TODO remove picture parameters
    // TODO translate

    // Private:

    private MetricsModel getTodayMetrics() {
        MetricsModel todayMetrics;

        // Day starts at 13:00 of Spain and 07:00 of Bolivia
        Date today = Date.from(Instant.now().plus(offSet));
        Optional<MetricsModel> latestMetrics = metricsRepository.findById(today);

        if(latestMetrics.isEmpty())
            todayMetrics = metricsRepository.save(new MetricsModel(today));
        else todayMetrics = latestMetrics.get();

        return todayMetrics;
    }

    // Queries:

    public Integer getGoogleQueries() {
        MetricsModel todayMetrics = getTodayMetrics();

        return todayMetrics.getGoogleFoundFromPicture() + todayMetrics.getGoogleNotFoundFromPicture() +
                todayMetrics.getGoogleFoundFromText() + todayMetrics.getGoogleNotFoundFromText();
    }

    public Integer getPaidBingQueries() {
        return getTodayMetrics().getPaidBingQueries();
    }

    // Counters:

    @Transactional
    public void countAccess(Short platform) {
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
    public void countInorganicSearched(boolean encontrado, boolean foto) {
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
    public void countGoogleSearch(boolean encontrado, boolean foto) {
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
    public void countBingSearch(boolean encontrado, boolean foto) {
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
    public void countPaidBingSearch() {
        getTodayMetrics().nuevoBingPagoBuscado();
    }

    @Transactional
    public void countInorganicLearned() {
        getTodayMetrics().nuevoInorganicoNuevo();
    }

    @Transactional
    public void countInorganicAutocompleted() {
        getTodayMetrics().nuevoInorganicoAutocompletado();
    }

    @Transactional
    public void countOrganicSearchedFromName(boolean found, boolean picture) {
        if (found) {
            if (picture)
                getTodayMetrics().nuevoFormularOrganicoFotoEncontrado();
            else getTodayMetrics().nuevoFormularOrganicoTecladoEncontrado();
        } else {
            if (picture)
                getTodayMetrics().nuevoFormularOrganicoFotoNoEncontrado();
            else getTodayMetrics().nuevoFormularOrganicoTecladoNoEncontrado();
        }
    }

    @Transactional
    public void countOrganicSearchedFromStructure(boolean found) {
        if(found)
            getTodayMetrics().countOrganicSucceededFromStructure();
        else getTodayMetrics().countOrganicFailedFromStructure();
    }

    @Transactional
    public void countMolecularMassSearched(boolean found) {
        if(found)
            getTodayMetrics().nuevoMasaMolecularEncontrado();
        else getTodayMetrics().nuevoMasaMolecularNoEncontrado();
    }

    @Transactional
    public void countErrorOccurred() {
        getTodayMetrics().countErrorOccurred();
    }

    @Transactional
    public void countReportSent() {
        getTodayMetrics().countReportSent();
    }

}
