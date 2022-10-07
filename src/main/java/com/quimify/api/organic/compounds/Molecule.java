package com.quimify.api.organic.compounds;

import com.quimify.api.organic.Organic;
import com.quimify.api.organic.components.Atom;
import com.quimify.api.organic.components.Element;
import com.quimify.api.organic.compounds.open_chain.Ether;
import com.quimify.api.organic.compounds.open_chain.OpenChain;
import com.quimify.api.organic.compounds.open_chain.Simple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
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

public class Molecule extends Organic {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Set<Atom> molecula;
	private final String smiles;

	// Constructor:

	public Molecule(String cml, String smiles) throws ParserConfigurationException, IOException, SAXException {
		molecula = new HashSet<>();
		this.smiles = smiles;

		// Se procesa el Chemical Markup Language:
		DocumentBuilder constructor = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document xml = constructor.parse(new InputSource(new StringReader(cml)));

		// Se recogen los átomos:
		NodeList atomos_xml = xml.getElementsByTagName("atom");
		for(int i = 0; i < atomos_xml.getLength(); i++) {
			org.w3c.dom.Element atomo = (org.w3c.dom.Element) atomos_xml.item(i);

			int id = Integer.parseInt(atomo.getAttribute("id").replace("a", ""));
			String tipo = atomo.getAttribute("elementType");

			molecula.add(new Atom(id, tipo));
		}

		// Se enlazan entre sí:
		NodeList enlaces_xml = xml.getElementsByTagName("bond");
		for(int i = 0; i < enlaces_xml.getLength(); i++) {
			org.w3c.dom.Element enlace = (org.w3c.dom.Element) enlaces_xml.item(i);

			String[] id_string = enlace.getAttribute("id").replace("a", "").split("_");

			Integer[] id_int = {
					Integer.valueOf(id_string[0]),
					Integer.valueOf(id_string[1])
			};

			Atom[] atoms = {
					molecula.stream().filter(atom -> atom.getId().equals(id_int[0])).findAny()
							.orElseThrow(NoSuchElementException::new),
					molecula.stream().filter(atom -> atom.getId().equals(id_int[1])).findAny()
							.orElseThrow(NoSuchElementException::new)
			};

			atoms[0].enlazar(atoms[1]);
			atoms[1].enlazar(atoms[0]);
		}
	}

	// Consultas internas:

	private List<Atom> getCarbonos() {
		return molecula.stream().filter(atom -> atom.getElement() == Element.C).collect(Collectors.toList());
	}

	private List<Atom> getCarbonosExtremos() {
		List<Atom> carbonos = getCarbonos();
		return carbonos.stream().filter(carbono -> carbono.getNumberOf(Element.C) < 2 || carbono.isBondedToEther())
				.collect(Collectors.toList());
	}

	private List<Atom> getOxigenosPuente() {
		return molecula.stream().filter(Atom::esOxigenoPuente).collect(Collectors.toList());
	}

	private int getCarbonosAlAlcanceDe(Atom carbono) {
		int cantidad;

		List<Atom> enlazados = carbono.getBondedCarbonsSeparated();

		cantidad = enlazados.size();
		for(Atom enlazado : enlazados)
			cantidad += getCarbonosAlAlcanceDe(enlazado);

		return cantidad;
	}

	private void buildOpenChainStartingFrom(OpenChain openChain, Atom startingCarbon) {
		// First carbon:
		startingCarbon.getSubstituentsWithoutRadicals().forEach(openChain::bond);

		// The rest of them:
		List<Atom> bondedCarbonsSeparated = startingCarbon.getBondedCarbonsSeparated();

		while(bondedCarbonsSeparated.size() > 0) {
			openChain.bondCarbon();

			Atom carbon = bondedCarbonsSeparated.get(0); // A path of multiple possible
			carbon.getSubstituentsWithoutRadicals().forEach(openChain::bond);

			bondedCarbonsSeparated = carbon.getBondedCarbonsSeparated();
		}
	}

	private Simple buildSimple(Atom startingCarbon) {
		Simple simple = new Simple();

		buildOpenChainStartingFrom(simple, startingCarbon);

		return simple;
	}

	private Ether buildEther(List<Atom> bondedToTheOxygen) {
		// First chain: [R - O -] R'
		Atom firstCarbon = bondedToTheOxygen.get(0); // [C] - O - C'
		firstCarbon.removeEther(); // Ether class will bond it

		Simple firstChain = new Simple(1);
		buildOpenChainStartingFrom(firstChain, firstCarbon); // - O - R

		Ether ether = new Ether(firstChain.getReversed()); // R - O - C ≡

		// Second chain: R - O - [R']
		firstCarbon = bondedToTheOxygen.get(1); // C - O - [C']
		firstCarbon.removeEther(); // It's already bonded
		buildOpenChainStartingFrom(ether, firstCarbon); // R - O - R'

		return ether;
	}

	private boolean soloHayUnaRama() {
		for(Atom atom : getCarbonos())
			if(atom.getNumberOf(Element.C) > 2)
				return false;

		return true;
	}

	// Texto:

	public Optional<String> getStructure() {
		Optional<String> formula = Optional.empty();

		// Se comprueba que hay ningún ciclo:
		if (!smiles.matches(".*[0-9].*")) {
			// Se buscan los extremos de la molécula:
			List<Atom> carbonos_extremos = getCarbonosExtremos();

			// Se buscan oxígenos que unan cadenas (R-O-R'):
			List<Atom> oxigenos_puente = getOxigenosPuente();

			if (soloHayUnaRama()) {
				if (oxigenos_puente.size() == 0) {
					Atom carbono_extremo = carbonos_extremos.get(0);
					int contiguos = 1 + getCarbonosAlAlcanceDe(carbono_extremo);

					if (contiguos == getCarbonos().size()) { // No tiene otros puentes
						// Podría ser un 'Simple':
						Simple simple = buildSimple(carbono_extremo);
						simple.correctSubstituents();
						formula = Optional.of(simple.getStructure());
					}
				} else if (oxigenos_puente.size() == 1) { // No tiene más que un puente de oxígeno
					if (carbonos_extremos.size() >= 2 && carbonos_extremos.size() <= 4) {
						List<Atom> enlazados_al_oxigeno = oxigenos_puente.get(0).getBondedCarbons();

						int contiguos_izquierda = 1 + getCarbonosAlAlcanceDe(enlazados_al_oxigeno.get(0));
						int contiguos_derecha = 1 + getCarbonosAlAlcanceDe(enlazados_al_oxigeno.get(1));

						int extremosPosibles = 4;
						if (contiguos_izquierda == 1)
							extremosPosibles--;
						if (contiguos_derecha == 1)
							extremosPosibles--;

						if (contiguos_izquierda + contiguos_derecha == getCarbonos().size()) { // No tiene otros puentes
							if (carbonos_extremos.size() == extremosPosibles) { // No tiene radicales
								// Podría ser un 'Eter':
								Ether ether = buildEther(enlazados_al_oxigeno);
								ether.correctSubstituents();
								formula = Optional.of(ether.getStructure());
							}
						}
					} else if (carbonos_extremos.size() == 0) {
						logger.error("Hay un puente de oxigeno y 0 carbonos extremos.");

						return Optional.empty();
					}
				}
			}
		}

		return formula;
	}

}
