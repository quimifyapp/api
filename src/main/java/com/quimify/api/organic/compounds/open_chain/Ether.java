package com.quimify.api.organic.compounds.open_chain;

import com.quimify.api.organic.Organic;
import com.quimify.api.organic.components.Chain;
import com.quimify.api.organic.components.FunctionalGroup;
import com.quimify.api.organic.components.Substituent;
import com.quimify.api.organic.compounds.OpenChain;

import java.util.*;

// Esta clase representa éteres: dos cadenas con funciones de prioridad menor a la función éter unidas por un oxígeno.

public final class Ether extends Organic implements OpenChain {

	// R - O - R'

	private final Chain firstChain, secondChain; // R, R'

	private static final List<FunctionalGroup> orderedFunctionalGroups = List.of(
			FunctionalGroup.nitro, FunctionalGroup.bromine, FunctionalGroup.chlorine, FunctionalGroup.fluorine,
			FunctionalGroup.iodine, FunctionalGroup.radical, FunctionalGroup.hydrogen
	);

	public Ether(Simple firstChain) {
		this.firstChain = firstChain.getChain(); // R - O -
		this.secondChain = new Chain(1); // - C
	}

	private Ether(Chain firstChain, Chain secondChain) {
		this.firstChain = firstChain; // R - O -
		this.secondChain = secondChain; // - C
	}

	// TODO: corregir orden eter?

	// OPEN CHAIN --------------------------------------------------------------------

	public Ether getReversed() {
		return new Ether(secondChain.getInversa(), firstChain.getInversa());
	}

	public boolean isDone() {
		return secondChain.isDone();
	}

	public int getFreeBonds() {
		return secondChain.getEnlacesLibres();
	}

	public List<FunctionalGroup> getOrderedBondableGroups() {
		return getFreeBonds() > 1 ? orderedFunctionalGroups : Collections.emptyList();
	}

	public void bondCarbon() {
		secondChain.enlazarCarbono();
	}

	// TODO: sale en consola?
	public void bond(Substituent substituent) {
		if (orderedFunctionalGroups.contains(substituent.getGroup()))
			secondChain.enlazar(substituent);
		else throw new IllegalArgumentException("No se puede enlazar [" + substituent.getGroup() + "] a un Ether.");
	}

	public void bond(FunctionalGroup functionalGroup) {
		bond(new Substituent(functionalGroup));
	}

	public void correctSubstituents() {
		correctRadicalSubstituents();
	}

	public String getName() {
		String name;

		String firstChainName = getChainNameFor(firstChain.getInversa()); // Se empieza a contar desde el oxígeno
		String secondChainName = getChainNameFor(secondChain); // La secundaria ya está en el orden bueno

		if (!firstChainName.equals(secondChainName)) {
			// Chains are alphabetically ordered:
			if (firstChainName.compareTo(secondChainName) < 0)
				name = firstChainName + " " + secondChainName;
			else name = secondChainName + " " + firstChainName;
		} else name = (startsWithDigit(firstChainName) ? "di " : "di") + firstChainName;

		return name + " éter";
	}

	public String getStructure() {
		return firstChain.getStructure() + secondChain.getStructure(); // "R - O" + "R"
	}

	// PRIVATE -----------------------------------------------------------------------

	// Modifiers:

	private void correctRadicalSubstituents() {
		if (isDone() && firstChain.hasGroupsWithoutHydrogenNorEther() || secondChain.hasGroupsWithoutHydrogenNorEther()) {
			firstChain.corregirRadicalesPorLaIzquierda(); // Comprobará internamente si hay radicales
			if (secondChain.hasGroup(FunctionalGroup.radical)) { // Para ahorrar el invertir la cadena
				secondChain.invertirOrden(); // En lugar de corregirlos por la derecha
				secondChain.corregirRadicalesPorLaIzquierda(); // CHF(CH3)(CH2CH3) → CH3-CH2-CHF-CH3
				secondChain.invertirOrden(); // Es necesario para no romper el orden del éter
			}
		}
	}

	// Queries:

	@Override
	public boolean equals(Object other) {
		boolean isEqual;

		if (other != null && other.getClass() == this.getClass()) {
			Ether nuevo = (Ether) other;

			isEqual = (firstChain.equals(nuevo.firstChain) && secondChain.equals(nuevo.secondChain));
		} else isEqual = false;

		return isEqual;
	}

	// Text: TODO: poner en común en Cadena

	private boolean isRedundantInName(FunctionalGroup functionalGroup, Chain chain) {
		boolean es_redundante;

		// Derivados del propil:
		if (chain.getSize() == 3)
			es_redundante = functionalGroup == FunctionalGroup.alkene && chain.getNumberOf(FunctionalGroup.alkene) == 2; // Es propadienil
			// Derivados del etil:
		else if (chain.getSize() == 2)
			es_redundante = esAlquenoOAlquino(functionalGroup); // Solo hay una posición posible para el enlace
			// Derivados del metil:
		else es_redundante = chain.getSize() == 1;

		return es_redundante;
	}

	private Localizador getPrefixFor(FunctionalGroup functionalGroup, Chain chain) {
		Localizador prefijo;

		List<Integer> posiciones = chain.getIndexesOfAll(functionalGroup);
		String nombre = getPrefixNameParticle(functionalGroup);

		if (isRedundantInName(functionalGroup, chain)) // Sobran los localizadores porque son evidentes
			prefijo = new Localizador(multiplicadorDe(posiciones.size()), nombre); // Como "difluoro"
		else prefijo = new Localizador(posiciones, nombre); // Como "1,2-difluoro"

		return prefijo;
	}

	private String getBondNameForIn(FunctionalGroup bond, Chain chain) {
		String bondName = "";

		List<Integer> indexes = chain.getIndexesOfAll(bond);
		String nameParticle = getBondNameParticle(bond);

		if (indexes.size() > 0) {
			Localizador localizador;

			if (isRedundantInName(bond, chain)) // Sobran los localizadores porque son evidentes
				localizador = new Localizador(multiplicadorDe(indexes.size()), nameParticle); // Como "dien"
			else localizador = new Localizador(indexes, nameParticle); // Como "1,2-dien"

			String localizador_to_string = localizador.toString();

			if (startsWithDigit(localizador_to_string))
				bondName += "-"; // Guion *antes* de los localizadores

			bondName += localizador_to_string;
		}

		return bondName;
	}

	private String getChainNameFor(Chain chain) {
		// Exceptional cases:
		if (chain.getOrderedGroupsWithoutHydrogenNorEther().size() == 1) {
			if (chain.hasMethylAt(1))
				return "iso" + cuantificadorDe(chain.getSize() + 1) + "il";
			else if (chain.hasMethylAt(2))
				return "sec" + cuantificadorDe(chain.getSize() + 1) + "il";
		}

		List<FunctionalGroup> functionalGroups = chain.getOrderedGroupsWithoutHydrogenNorEther(); // Sin hidrógeno ni éter
		int groupsIndex = 0;

		// Se procesan los prefijos:
		List<Localizador> prefijos = new ArrayList<>();

		while (groupsIndex < functionalGroups.size()) {
			if (!esAlquenoOAlquino(functionalGroups.get(groupsIndex)) && functionalGroups.get(groupsIndex) != FunctionalGroup.radical)
				prefijos.add(getPrefixFor(functionalGroups.get(groupsIndex), chain));

			groupsIndex++;
		}

		List<Substituent> radicales = chain.getUniqueRadicals();
		for (Substituent radical : radicales)
			prefijos.add(new Localizador(chain.getIndexesOfAll(radical), getRadicalNameParticle(radical)));

		StringBuilder prefijo = new StringBuilder(chain.hasGroup(FunctionalGroup.acid) ? "ácido " : "");
		if (prefijos.size() > 0) {
			Localizador.ordenarAlfabeticamente(prefijos);

			for (int i = 0; i < prefijos.size() - 1; i++) {
				prefijo.append(prefijos.get(i).toString());

				if (doesNotStartWithLetter(prefijos.get(i + 1).toString()))
					prefijo.append("-");
			}

			prefijo.append(prefijos.get(prefijos.size() - 1));
		}

		// Se procesan los enlaces:
		String enlaces = getBondNameForIn(FunctionalGroup.alkene, chain) + getBondNameForIn(FunctionalGroup.alkyne, chain);

		// Se procesa el cuantificador:
		String cuantificador = cuantificadorDe(chain.getSize());

		if (!enlaces.equals("") && Organic.doesNotStartWithVowel(enlaces))
			cuantificador += "a";

		return prefijo + cuantificador + enlaces + "il";
	}

	@Override
	public String toString() {
		return getStructure();
	}

}
