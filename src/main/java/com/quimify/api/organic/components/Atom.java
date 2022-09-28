package com.quimify.api.organic.components;

import java.util.*;
import java.util.stream.Collectors;

public class Atom {

	private final Integer id;
	private final Element element;
	private final List<Atom> bondedAtoms;

	// Constantes:

	public static final Atom H = new Atom(Element.H);
	public static final Atom N = new Atom(Element.N);
	public static final Atom O = new Atom(Element.O);
	public static final Atom OH = new Atom(Element.O, List.of(H));
	public static final Atom NH2 = new Atom(Element.N, List.of(H, H));

	public static final Atom OC = new Atom(Element.O, List.of(new Atom(Element.C)));
	public static final Atom NO2 = new Atom(Element.N, List.of(O, O));
	public static final Atom Br = new Atom(Element.Br);
	public static final Atom Cl = new Atom(Element.Cl);
	public static final Atom F = new Atom(Element.F);
	public static final Atom I = new Atom(Element.I);

	// Constructor:

	public Atom(int id, String simbolo) {
		this.id = id;
		bondedAtoms = new ArrayList<>();

		switch (simbolo) {
			case "C":
				element = Element.C;
				break;
			case "H":
				element = Element.H;
				break;
			case "N":
				element = Element.N;
				break;
			case "O":
				element = Element.O;
				break;
			case "Br":
				element = Element.Br;
				break;
			case "Cl":
				element = Element.Cl;
				break;
			case "F":
				element = Element.F;
				break;
			case "I":
				element = Element.I;
				break;
			default:
				throw new IllegalArgumentException("No se contempla el átomo \"" + simbolo + "\".");
		}
	}

	public Atom(Element element, List<Atom> enlazados) {
		id = null;
		this.element = element;
		this.bondedAtoms = enlazados;
	}

	private Atom(Element element) {
		id = null;
		this.element = element;
		bondedAtoms = new ArrayList<>();
	}

	private Atom(Atom otro) {
		id = otro.id;
		element = otro.element;
		bondedAtoms = new ArrayList<>(otro.bondedAtoms);
	}

	// Modificadores:

	public void enlazar(Atom otro) {
		bondedAtoms.add(otro);
	}

	public void removeEther() {
		getBondedAtomsSeparatedWithoutCarbon().stream()
				.filter(bondedAtom -> bondedAtom.toFunctionalGroup() == FunctionalGroup.ether)
				.forEach(etherAtom -> bondedAtoms.removeIf(bondedAtom -> Objects.equals(bondedAtom.id, etherAtom.id)));
	}

	// Consultas:

	public boolean esTipo(Element tipo) {
		return this.element == tipo;
	}

	public boolean todosSusEnlazadosSonTipo(Element tipo) {
		for (Atom enlazado : bondedAtoms)
			if (!enlazado.esTipo(tipo))
				return false;

		return true;
	}

	public boolean esOxigenoPuente() {
		return esTipo(Element.O) && getCantidadDeEnlazados() == 2 && todosSusEnlazadosSonTipo(Element.C); // C-O-C
	}

	public boolean isBondedToEther() {
		return getBondedAtomsSeparatedWithoutCarbon().stream().map(Atom::toFunctionalGroup)
				.collect(Collectors.toSet()).contains(FunctionalGroup.ether);
	}

	@Override
	public boolean equals(Object otro) {
		boolean es_igual;

		if (otro != null && otro.getClass() == this.getClass()) {
			Atom nuevo = (Atom) otro;

			if (Objects.equals(id, nuevo.id)) { // Nota: Objects.equals(null, null) = true
				if (element == nuevo.element && bondedAtoms.size() == nuevo.bondedAtoms.size()) {
					es_igual = true;

					List<Atom> separados = getBondedAtomsSeparated();
					List<Atom> nuevos_separados = nuevo.getBondedAtomsSeparated();

					for (int i = 0; i < separados.size(); i++)
						if (!separados.get(i).equals(nuevos_separados.get(i))) {
							es_igual = false;
							break;
						}
				} else es_igual = false;
			} else es_igual = false;
		} else es_igual = false;

		return es_igual;
	}

	// Internos:

	private List<Atom> getBondedAtomsSeparated(List<Atom> bondedAtoms) {
		List<Atom> enlazados_separados = new ArrayList<>();

		for (Atom enlazado : bondedAtoms) {
			Atom nuevo = new Atom(enlazado);
			nuevo.bondedAtoms.removeIf(otro -> Objects.equals(id, otro.id));
			enlazados_separados.add(nuevo);
		}

		return enlazados_separados;
	}

	private Atom toAnonymous() {
		Atom anonymousAtom = new Atom(element, getBondedAtomsSeparated());
		anonymousAtom.bondedAtoms.replaceAll(Atom::toAnonymous);
		return anonymousAtom;
	}

	// Métodos get:

	public int getCantidadDeEnlazados() {
		return bondedAtoms.size();
	}

	public int getNumberOf(Element atom) {
		int number = 0;

		for (Atom bonded : bondedAtoms)
			if (bonded.esTipo(atom))
				number++;

		return number;
	}

	public List<Atom> getBondedCarbons() {
		return bondedAtoms.stream().filter(bonded -> bonded.esTipo(Element.C)).collect(Collectors.toList());
	}

	public List<Atom> getBondedAtomsWithoutCarbon() {
		return bondedAtoms.stream().filter(bonded -> !bonded.esTipo(Element.C)).collect(Collectors.toList());
	}

	public List<Atom> getBondedAtomsSeparated() {
		return getBondedAtomsSeparated(bondedAtoms);
	}

	public List<Atom> getBondedCarbonsSeparated() {
		return getBondedAtomsSeparated(getBondedCarbons());
	}

	public List<Atom> getBondedAtomsSeparatedWithoutCarbon() {
		return getBondedAtomsSeparated(getBondedAtomsWithoutCarbon());
	}

	public List<Substituent> getSubstituentsWithoutRadicals() {
		List<Substituent> substituents = new ArrayList<>();

		for (Atom bonded : getBondedAtomsSeparatedWithoutCarbon())
			substituents.add(new Substituent(bonded.toFunctionalGroup()));

		return substituents;
	}

	private FunctionalGroup toFunctionalGroup() {
		FunctionalGroup functionalGroup;

		Atom anonymous = toAnonymous(); // Sin 'id'

		if (anonymous.equals(N))
			functionalGroup = FunctionalGroup.nitrile;
		else if (anonymous.equals(O))
			functionalGroup = FunctionalGroup.ketone;
		else if (anonymous.equals(OH))
			functionalGroup = FunctionalGroup.alcohol;
		else if (anonymous.equals(NH2))
			functionalGroup = FunctionalGroup.amine;
		else if (anonymous.equals(OC))
			functionalGroup = FunctionalGroup.ether;
		else if (anonymous.equals(NO2))
			functionalGroup = FunctionalGroup.nitro;
		else if (anonymous.equals(Br))
			functionalGroup = FunctionalGroup.bromine;
		else if (anonymous.equals(Cl))
			functionalGroup = FunctionalGroup.chlorine;
		else if (anonymous.equals(F))
			functionalGroup = FunctionalGroup.fluorine;
		else if (anonymous.equals(I))
			functionalGroup = FunctionalGroup.iodine;
		else if (anonymous.equals(H))
			functionalGroup = FunctionalGroup.hydrogen;
		else throw new ClassCastException("No se reconoce el átomo [" + element + "]."); // Unrecognized: Pb, OCl2...

		return functionalGroup;
	}

	// Getters:

	public Integer getId() {
		return id;
	}

	public Element getElement() {
		return element;
	}

}
