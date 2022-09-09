package com.quimify.api.organico.tipos;

import com.quimify.api.organico.Organica;
import com.quimify.api.organico.componentes.Atomo;
import com.quimify.api.organico.componentes.Atomos;
import com.quimify.api.organico.componentes.Cadena;
import com.quimify.api.organico.componentes.Funciones;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

// Esta clase representa una molécula cualquiera a partir de un CML en formato XML para intentar redactarle una fórmula.

// Un compuesto genérico, químicamente hablando, podría ser de otro tipo ya contemplado en este programa (simple, éter,
// éster, cíclico...), pero también podría no encajar en ninguno de esos tipos.

public class Generico extends Organica {

	private final Set<Atomo> molecula;
	private final String smiles;

	// Constructor:

	public Generico(String cml, String smiles) throws ParserConfigurationException, IOException, SAXException {
		molecula = new HashSet<>();
		this.smiles = smiles;

		// Se procesa el Chemical Markup Language:
		DocumentBuilder constructor = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document xml = constructor.parse(new InputSource(new StringReader(cml)));

		// Se recogen los átomos:
		NodeList atomos_xml = xml.getElementsByTagName("atom");
		for(int i = 0; i < atomos_xml.getLength(); i++) {
			Element atomo = (Element) atomos_xml.item(i);

			int id = Integer.parseInt(atomo.getAttribute("id").replace("a", ""));
			String tipo = atomo.getAttribute("elementType");

			molecula.add(new Atomo(id, tipo));
		}

		// Se enlazan entre sí:
		NodeList enlaces_xml = xml.getElementsByTagName("bond");
		for(int i = 0; i < enlaces_xml.getLength(); i++) {
			Element enlace = (Element) enlaces_xml.item(i);

			String[] id_string = enlace.getAttribute("id").replace("a", "").split("_");

			Integer[] id_int = {
					Integer.valueOf(id_string[0]),
					Integer.valueOf(id_string[1])
			};

			Atomo[] atomos = {
					molecula.stream().filter(atomo -> atomo.getId().equals(id_int[0])).findAny()
							.orElseThrow(NoSuchElementException::new),
					molecula.stream().filter(atomo -> atomo.getId().equals(id_int[1])).findAny()
							.orElseThrow(NoSuchElementException::new)
			};

			atomos[0].enlazar(atomos[1]);
			atomos[1].enlazar(atomos[0]);
		}
	}

	// Consultas internas:

	private List<Atomo> getCarbonos() {
		return molecula.stream().filter(atomo -> atomo.getTipo() == Atomos.C).collect(Collectors.toList());
	}

	private List<Atomo> getCarbonosExtremos() {
		List<Atomo> carbonos = getCarbonos();
		return carbonos.stream().filter(carbono -> carbono.getCantidadDe(Atomos.C) < 2).collect(Collectors.toList());
	}

	private List<Atomo> getOxigenosPuente() {
		return molecula.stream().filter(Atomo::esOxigenoPuente).collect(Collectors.toList());
	}

	private int getCarbonosAlAlcanceDe(Atomo carbono) {
		int cantidad;

		List<Atomo> enlazados = carbono.getEnlazadosSeparadosCarbonos();

		cantidad = enlazados.size();
		for(Atomo enlazado : enlazados)
			cantidad += getCarbonosAlAlcanceDe(enlazado);

		return cantidad;
	}

	private Simple construirSimple(Atomo carbono_extremo) {
		Simple simple = new Simple();

		// Primer carbono:
		simple.enlazarCarbono();
		carbono_extremo.getSustituyentes().forEach(simple::enlazar); // Son solo los no-carbonos

		// El resto:
		List<Atomo> carbonos_separados = carbono_extremo.getEnlazadosSeparadosCarbonos();
		while(carbonos_separados.size() > 0) {
			simple.enlazarCarbono();

			Atomo carbono = carbonos_separados.get(0);
			carbono.getSustituyentes().forEach(simple::enlazar); // Son solo los no-carbonos

			carbonos_separados = carbono.getEnlazadosSeparadosCarbonos();
		}

		return simple;
	}

	private Eter construirEter(List<Atomo> enlazados_al_oxigeno) {
		Eter eter;

		// Por un lado (C*-(...)-O-C):

		Cadena primaria = new Cadena();
		Atomo carbono_extremo = enlazados_al_oxigeno.get(0);

		// Primer carbono:
		primaria.enlazarCarbono();
		carbono_extremo.getSustituyentes().forEach(primaria::enlazar); // Son solo los no-carbonos

		// El resto:
		List<Atomo> carbonos_separados = carbono_extremo.getEnlazadosSeparadosCarbonos();
		while(carbonos_separados.size() > 0) {
			primaria.enlazarCarbono();

			Atomo carbono = carbonos_separados.get(0);
			carbono.getSustituyentes().forEach(primaria::enlazar); // Son solo los no-carbonos

			carbonos_separados = carbono.getEnlazadosSeparadosCarbonos();
		}

		// Por el otro lado (C-O-C*):

		Cadena secundaria = new Cadena(1);
		carbono_extremo = enlazados_al_oxigeno.get(1);

		// Primer carbono:
		carbono_extremo.getSustituyentes().stream()
				.filter(sustituyente -> !sustituyente.esTipo(Funciones.eter)) // El éter no se duplica
				.forEach(secundaria::enlazar); // Son solo los no-carbonos

		// El resto:
		carbonos_separados = carbono_extremo.getEnlazadosSeparadosCarbonos();
		while(carbonos_separados.size() > 0) {
			secundaria.enlazarCarbono();

			Atomo carbono = carbonos_separados.get(0);
			carbono.getSustituyentes().forEach(secundaria::enlazar); // Son solo los no-carbonos

			carbonos_separados = carbono.getEnlazadosSeparadosCarbonos();
		}

		// Finalmente:
		eter = new Eter(primaria.getInversa(), secundaria);

		return eter;
	}

	private boolean soloHayUnaRama() {
		for(Atomo atomo : getCarbonos())
			if(atomo.getCantidadDe(Atomos.C) > 2)
				return false;

		return true;
	}

	// Texto:

	public Optional<String> getFormula() {
		Optional<String> formula = Optional.empty();

		// Se comprueba que hay ningún ciclo:
		if(!smiles.matches(".*[0-9].*")) {
			// Se buscan los extremos de la molécula:
			List<Atomo> carbonos_extremos = getCarbonosExtremos();

			// Se buscan oxígenos que unan cadenas (R-O-R'):
			List<Atomo> oxigenos_puente = getOxigenosPuente();

			if(soloHayUnaRama()) {
				if(oxigenos_puente.size() == 0) {
					Atomo carbono_extremo = carbonos_extremos.get(0);
					int contiguos = 1 + getCarbonosAlAlcanceDe(carbono_extremo);

					if (contiguos == getCarbonos().size()) { // No tiene otros puentes
						// Podría ser un 'Simple':
						Simple simple = construirSimple(carbono_extremo);
						simple.corregir();
						formula = Optional.of(simple.getFormula());
					}
				}
				else if(oxigenos_puente.size() == 1) { // No tiene más que un puente de oxígeno
					if(carbonos_extremos.size() >= 2 && carbonos_extremos.size() <= 4) {
						List<Atomo> enlazados_al_oxigeno = oxigenos_puente.get(0).getEnlazadosCarbonos();
						int contiguos_izquierda = 1 + getCarbonosAlAlcanceDe(enlazados_al_oxigeno.get(0));
						int contiguos_derecha = 1 + getCarbonosAlAlcanceDe(enlazados_al_oxigeno.get(1));

						if(contiguos_izquierda + contiguos_derecha == getCarbonos().size()) { // No tiene otros puentes
							// Podría ser un 'Eter':
							Eter eter = construirEter(enlazados_al_oxigeno);
							eter.corregir();
							formula = Optional.of(eter.getFormula());
						}
					}
					else if (carbonos_extremos.size() == 0) {
						// Error...
					}
				}
			}
		}

		return formula;
	}

}
