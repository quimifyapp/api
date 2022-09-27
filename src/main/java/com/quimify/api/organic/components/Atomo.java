package com.quimify.api.organic.components;

import java.util.*;
import java.util.stream.Collectors;

public class Atomo {

	private final Integer id;
	private final Element functionalGroup;
	private final List<Atomo> bondedAtoms;

	// Constantes:

	public static final Atomo H = new Atomo(Element.H);
	public static final Atomo N = new Atomo(Element.N);
	public static final Atomo O = new Atomo(Element.O);
	public static final Atomo OH = new Atomo(Element.O, List.of(H));
	public static final Atomo NH2 = new Atomo(Element.N, List.of(H, H));

	public static final Atomo OC = new Atomo(Element.O, List.of(new Atomo(Element.C)));
	public static final Atomo NO2 = new Atomo(Element.N, List.of(O, O));
	public static final Atomo Br = new Atomo(Element.Br);
	public static final Atomo Cl = new Atomo(Element.Cl);
	public static final Atomo F = new Atomo(Element.F);
	public static final Atomo I = new Atomo(Element.I);

	// Constructor:

	public Atomo(int id, String simbolo) {
		this.id = id;
		bondedAtoms = new ArrayList<>();

		switch (simbolo) {
			case "C":
				functionalGroup = Element.C;
				break;
			case "H":
				functionalGroup = Element.H;
				break;
			case "N":
				functionalGroup = Element.N;
				break;
			case "O":
				functionalGroup = Element.O;
				break;
			case "Br":
				functionalGroup = Element.Br;
				break;
			case "Cl":
				functionalGroup = Element.Cl;
				break;
			case "F":
				functionalGroup = Element.F;
				break;
			case "I":
				functionalGroup = Element.I;
				break;
			default:
				throw new IllegalArgumentException("No se contempla el átomo \"" + simbolo + "\".");
		}
	}

	public Atomo(Element functionalGroup, List<Atomo> enlazados) {
		id = null;
		this.functionalGroup = functionalGroup;
		this.bondedAtoms = enlazados;
	}

	private Atomo(Element functionalGroup) {
		id = null;
		this.functionalGroup = functionalGroup;
		bondedAtoms = new ArrayList<>();
	}

	private Atomo(Atomo otro) {
		id = otro.id;
		functionalGroup = otro.functionalGroup;
		bondedAtoms = new ArrayList<>(otro.bondedAtoms);
	}

	// Modificadores:

	public void enlazar(Atomo otro) {
		bondedAtoms.add(otro);
	}

	// Consultas:

	public boolean esTipo(Element tipo) {
		return this.functionalGroup == tipo;
	}

	public boolean todosSusEnlazadosSonTipo(Element tipo) {
		for (Atomo enlazado : bondedAtoms)
			if (!enlazado.esTipo(tipo))
				return false;

		return true;
	}

	public boolean esOxigenoPuente() {
		return esTipo(Element.O) && getCantidadDeEnlazados() == 2 && todosSusEnlazadosSonTipo(Element.C); // C-O-C
	}

	@Override
	public boolean equals(Object otro) {
		boolean es_igual;

		if (otro != null && otro.getClass() == this.getClass()) {
			Atomo nuevo = (Atomo) otro;

			if (Objects.equals(id, nuevo.id)) { // Nota: Objects.equals(null, null) = true
				if (functionalGroup == nuevo.functionalGroup && bondedAtoms.size() == nuevo.bondedAtoms.size()) {
					es_igual = true;

					List<Atomo> separados = getBondedAtomsSeparated();
					List<Atomo> nuevos_separados = nuevo.getBondedAtomsSeparated();

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

	private List<Atomo> getBondedAtomsSeparated(List<Atomo> bondedAtoms) {
		List<Atomo> enlazados_separados = new ArrayList<>();

		for (Atomo enlazado : bondedAtoms) {
			Atomo nuevo = new Atomo(enlazado);
			nuevo.bondedAtoms.removeIf(otro -> Objects.equals(id, otro.id));
			enlazados_separados.add(nuevo);
		}

		return enlazados_separados;
	}

	private Atomo toAnonymous() {
		Atomo anonymousAtom = new Atomo(functionalGroup, getBondedAtomsSeparated());
		anonymousAtom.bondedAtoms.replaceAll(Atomo::toAnonymous);
		return anonymousAtom;
	}

	// Métodos get:

	public int getCantidadDeEnlazados() {
		return bondedAtoms.size();
	}

	public int getNumberOf(Element atom) {
		int number = 0;

		for (Atomo bonded : bondedAtoms)
			if (bonded.esTipo(atom))
				number++;

		return number;
	}

	public List<Atomo> getBondedCarbons() {
		return bondedAtoms.stream().filter(bonded -> bonded.esTipo(Element.C)).collect(Collectors.toList());
	}

	public List<Atomo> getBondedAtomsWithoutCarbon() {
		return bondedAtoms.stream().filter(bonded -> !bonded.esTipo(Element.C)).collect(Collectors.toList());
	}

	public List<Atomo> getBondedAtomsSeparated() {
		return getBondedAtomsSeparated(bondedAtoms);
	}

	public List<Atomo> getBondedCarbonsSeparated() {
		return getBondedAtomsSeparated(getBondedCarbons());
	}

	public List<Atomo> getBondedAtomsSeparatedWithoutCarbon() {
		return getBondedAtomsSeparated(getBondedAtomsWithoutCarbon());
	}

	public List<Substituent> getSubstituentsWithoutRadicals() {
		List<Substituent> substituents = new ArrayList<>();

		for (Atomo bonded : getBondedAtomsSeparatedWithoutCarbon())
			substituents.add(new Substituent(bonded.toFunctionalGroup()));

		return substituents;
	}

	private FunctionalGroup toFunctionalGroup() {
		FunctionalGroup functionalGroup;

		Atomo anonymous = toAnonymous(); // Sin 'id'

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
		else throw new ClassCastException(); // Es un átomo no reconocido, como: Pb, OCl2...

		return functionalGroup;
	}

	// Getters:

	public Integer getId() {
		return id;
	}

	public Element getFunctionalGroup() {
		return functionalGroup;
	}

}
