package com.quimify.servidor.metricas;

import com.quimify.servidor.ContextoCliente;
import com.quimify.servidor.inorganico.InorganicoResultado;
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

    // TODO: los que faltan y ordenarlos

    public Integer getGoogle() {
        MetricasModel metricas = metricasDeHoy();

        return metricas.getTeclado_google() + metricas.getCamara_google() + metricas.getGaleria_google();
    }

    public Integer getBingPago() {
        MetricasModel metricas = metricasDeHoy();

        return metricas.getTeclado_bing_pago() + metricas.getCamara_bing_pago() + metricas.getGaleria_bing_pago();
    }

    @Transactional
    public void contarAcceso() {
        MetricasModel metricas = metricasDeHoy();

        metricas.nuevoAcceso();
    }

    @Transactional
    public void contarBusqueda(InorganicoResultado inorganico, ContextoCliente contexto) {
        MetricasModel metricas = metricasDeHoy();

        Short resultado = inorganico.getResultado();
        Short pantalla = contexto.getPantalla();
        Boolean premium = inorganico.getPremium();

        if(resultado.equals(InorganicoResultado.ENCONTRADO)) {
            if(pantalla.equals(ContextoCliente.TECLADO)) {
                metricas.nuevoEncontradoPorTeclado();
                if(premium)
                    metricas.nuevoPremiumPorTeclado();
            }
            else if(pantalla.equals(ContextoCliente.CAMARA)) {
                metricas.nuevoEncontradoPorCamara();
                if(premium)
                    metricas.nuevoPremiumPorCamara();
            }
            else if(pantalla.equals(ContextoCliente.GALERIA)) {
                metricas.nuevoEncontradoPorGaleria();
                if(premium)
                    metricas.nuevoPremiumPorGaleria();
            }
        }
        else if(resultado.equals(InorganicoResultado.SUGERENCIA)) {
            if(pantalla.equals(ContextoCliente.TECLADO)) {
                metricas.nuevaSugerenciaPorTeclado();
                if(premium)
                    metricas.nuevoPremiumPorTeclado();
            }
            else if(pantalla.equals(ContextoCliente.CAMARA)) {
                metricas.nuevaSugerenciaPorCamara();
                if(premium)
                    metricas.nuevoPremiumPorCamara();
            }
            else if(pantalla.equals(ContextoCliente.GALERIA)) {
                metricas.nuevaSugerenciaPorGaleria();
                if(premium)
                    metricas.nuevoPremiumPorGaleria();
            }
        }
        else if(resultado.equals(InorganicoResultado.NO_ENCONTRADO)) {
            if(pantalla.equals(ContextoCliente.TECLADO)) {
                metricas.nuevoNoEncontradoPorTeclado();
            }
            else if(pantalla.equals(ContextoCliente.CAMARA)) {
                metricas.nuevoNoEncontradoPorCamara();
            }
            else if(pantalla.equals(ContextoCliente.GALERIA)) {
                metricas.nuevoNoEncontradoPorGaleria();
            }
        }
    }

    @Transactional
    public void contarSugerenciaOk(ContextoCliente contexto) {
        MetricasModel metricas = metricasDeHoy();

        Short pantalla = contexto.getPantalla();

        if(pantalla.equals(ContextoCliente.TECLADO)) {
            metricas.nuevaSugerenciaOkPorTeclado();
        }
        else if(pantalla.equals(ContextoCliente.CAMARA)) {
            metricas.nuevaSugerenciaOkPorCamara();
        }
        else if(pantalla.equals(ContextoCliente.GALERIA)) {
            metricas.nuevaSugerenciaOkPorGaleria();
        }
    }

    @Transactional
    public void contarComplecionOk() {
        MetricasModel metricas = metricasDeHoy();

        metricas.nuevaComplecionOkPorTeclado();
    }

    @Transactional
    public void contarGoogle(ContextoCliente contexto) {
        MetricasModel metricas = metricasDeHoy();

        Short pantalla = contexto.getPantalla();

        if(pantalla.equals(ContextoCliente.TECLADO)) {
            metricas.nuevoGooglePorTeclado();
        }
        else if(pantalla.equals(ContextoCliente.CAMARA)) {
            metricas.nuevoGooglePorCamara();
        }
        else if(pantalla.equals(ContextoCliente.GALERIA)) {
            metricas.nuevoGooglePorGaleria();
        }
    }

    @Transactional
    public void contarBingGratis(ContextoCliente contexto) {
        MetricasModel metricas = metricasDeHoy();

        Short pantalla = contexto.getPantalla();

        if(pantalla.equals(ContextoCliente.TECLADO)) {
            metricas.nuevoBingGratisPorTeclado();
        }
        else if(pantalla.equals(ContextoCliente.CAMARA)) {
            metricas.nuevoBingGratisPorCamara();
        }
        else if(pantalla.equals(ContextoCliente.GALERIA)) {
            metricas.nuevoBingGratisPorGaleria();
        }
    }

    @Transactional
    public void contarBingPago(ContextoCliente contexto) {
        MetricasModel metricas = metricasDeHoy();

        Short pantalla = contexto.getPantalla();

        if(pantalla.equals(ContextoCliente.TECLADO)) {
            metricas.nuevoBingPagoPorTeclado();
        }
        else if(pantalla.equals(ContextoCliente.CAMARA)) {
            metricas.nuevoBingPagoPorCamara();
        }
        else if(pantalla.equals(ContextoCliente.GALERIA)) {
            metricas.nuevoBingPagoPorGaleria();
        }
    }

}
