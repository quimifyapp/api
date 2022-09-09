package com.quimify.api.organico;

import com.quimify.api.metricas.MetricasService;
import com.quimify.api.organico.intermediarios.opsin.Opsin;
import com.quimify.api.organico.intermediarios.opsin.OpsinResultado;
import com.quimify.api.organico.intermediarios.pubchem.PubChem;
import com.quimify.api.organico.intermediarios.pubchem.PubChemResultado;
import com.quimify.api.organico.tipos.Eter;
import com.quimify.api.organico.tipos.Generico;
import com.quimify.api.organico.tipos.Simple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// Esta clase procesa los compuestos orgánicos.

@Service
public class OrganicoService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	MetricasService metricaService; // Procesos de las metricas diarias

	public static final OrganicoResultado NO_ENCONTRADO = new OrganicoResultado(false); // Constante auxiliar

	// CLIENTE -----------------------------------------------------------------------

	public OrganicoResultado formular(String nombre, Boolean foto) {
		OrganicoResultado resultado;

		Optional<OpsinResultado> opsin_resultado = Opsin.procesarNombreES(nombre);
		if(opsin_resultado.isPresent()) {
			resultado = new OrganicoResultado(true);

			resultado.setNombre(nombre); // El input del usuario (puede estar mal escrito)

			completarConPubChem(resultado, opsin_resultado.get().getSmiles()); // Características

			// Fórmula:
			try {
				Generico generico = new Generico(opsin_resultado.get().getCml(), opsin_resultado.get().getSmiles());

				Optional<String> formula = generico.getFormula();
				formula.ifPresent(resultado::setFormula);
			}
			catch(IllegalArgumentException ignore) {} // Es común que se produzcan errores
			catch (Exception exception) {
				logger.error("Excepción al procesar un Generico de nombre \"" + nombre + "\": " + exception);
			}
		}
		else resultado = NO_ENCONTRADO;

		metricaService.contarFormularOrganico(resultado.getEncontrado(), foto);

		return resultado;
	}

	public OrganicoResultado nombrar(Simple simple) {
		OrganicoResultado resultado = new OrganicoResultado(true);

		simple.corregir(); // Es necesario

		// Nombre:
		String nombre = simple.getNombre();
		resultado.setNombre(nombre);

		// Características:
		Optional<OpsinResultado> opsin_resultado = Opsin.procesarNombreES(nombre);
		opsin_resultado.ifPresent(opsinResultado -> completarConPubChem(resultado, opsinResultado.getSmiles()));

		// Fórmula:
		resultado.setFormula(simple.getFormula());

		return resultado;
	}

	public OrganicoResultado nombrar(Eter eter) {
		OrganicoResultado resultado = new OrganicoResultado(true);

		eter.corregir(); // Es necesario

		// Nombre:
		String nombre = eter.getNombre();
		resultado.setNombre(nombre);

		// Características:
		Optional<OpsinResultado> opsin_resultado = Opsin.procesarNombreES(nombre);
		opsin_resultado.ifPresent(opsinResultado -> completarConPubChem(resultado, opsinResultado.getSmiles()));

		// Fórmula:
		resultado.setFormula(eter.getFormula());

		return resultado;
	}

	// INTERNOS ----------------------------------------------------------------------

	private void completarConPubChem(OrganicoResultado resultado, String smiles) {
		PubChemResultado pub_chem_resultado = new PubChem(smiles).procesar();

		if(pub_chem_resultado.getMasa().isPresent())
			resultado.setMasa(pub_chem_resultado.getMasa().get());

		resultado.setUrl_2d(pub_chem_resultado.getUrl_2d());
	}

}
