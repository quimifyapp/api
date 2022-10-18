package com.quimify.api.organic.compounds.open_chain;

import com.quimify.api.organic.Organic;
import com.quimify.api.organic.components.Chain;
import com.quimify.api.organic.components.FunctionalGroup;
import com.quimify.api.organic.components.Substituent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Esta clase representa éteres: dos cadenas con funciones de prioridad menor a la función éter unidas por un oxígeno.

public final class Ether extends Organic implements OpenChain {

	private final Chain firstChain; // R
	private Chain secondChain, currentChain; // R', ->

	private static final List<FunctionalGroup> orderedBondableGroups = List.of(
			FunctionalGroup.nitro, FunctionalGroup.bromine, FunctionalGroup.chlorine, FunctionalGroup.fluorine,
			FunctionalGroup.iodine, FunctionalGroup.radical, FunctionalGroup.hydrogen
	);

	public Ether(Simple firstChain) {
		this.firstChain = firstChain.getChain(); // R - O

		if(firstChain.isDone())
			startSecondChain(); // R - O - C≡
		else currentChain = this.firstChain;
	}

	private Ether(Chain firstChain, Chain secondChain) {
		this.firstChain = firstChain; // [R - O] - R'
		this.secondChain = secondChain; // R - O [- R']
		currentChain = this.firstChain;
	}

	// OPEN CHAIN --------------------------------------------------------------------

	public Ether getReversed() {
		return new Ether(secondChain.getInversa(), firstChain.getInversa());
	}

	public int getFreeBonds() {
		return currentChain.getEnlacesLibres();
	}

	public boolean isDone() {
		return currentChain.isDone();
	}

	public void bondCarbon() {
		secondChain.enlazarCarbono();
	}

	public void bond(Substituent substituent) {
		if (orderedBondableGroups.contains(substituent.getGroup())) {
			currentChain.enlazar(substituent);

			if (currentChain == firstChain && firstChain.isDone())
				if (currentChain.isDone())
					startSecondChain();
		}
		else throw new IllegalArgumentException("No se puede enlazar [" + substituent.getGroup() + "] a un Ether.");
	}

	public void bond(FunctionalGroup functionalGroup) {
		bond(new Substituent(functionalGroup));
	}

	public void correctSubstituents() {
		correctRadicalSubstituents();
	}

	public List<FunctionalGroup> getOrderedBondableGroups() {
		return getFreeBonds() > 0 ? orderedBondableGroups : Collections.emptyList();
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
		String firstChainStructure = firstChain.getStructure();

		return currentChain == firstChain
				? firstChainStructure.substring(0, firstChainStructure.length() - 1)
				: firstChainStructure + secondChain.getStructure();
	}

	// PRIVATE -----------------------------------------------------------------------

	// Modifiers:

	private void correctRadicalSubstituents() {
		// Se corrigen los radicales que podrían formar parte de las cadenas principales:
		firstChain.corregirRadicalesPorLaIzquierda(); // Si no tiene radicales, no hará nada

		if (secondChain.hasFunctionalGroup(FunctionalGroup.radical)) { // Para ahorrar el invertir la cadena
			secondChain.invertirOrden(); // En lugar de corregirlos por la derecha
			secondChain.corregirRadicalesPorLaIzquierda(); // CHF(CH3)(CH2CH3) → CH3-CH2-CHF-CH3
			secondChain.invertirOrden(); // Es necesario para no romper el orden del éter
		}
	}

	private void startSecondChain() {
		secondChain = new Chain(1);
		currentChain = secondChain;
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
		boolean isRedundant;

		// Derivados del propil:
		if (chain.getSize() == 3)
			isRedundant = functionalGroup == FunctionalGroup.alkene && chain.getNumberOf(FunctionalGroup.alkene) == 2; // Es propadienil
			// Derivados del etil:
		else if (chain.getSize() == 2)
			isRedundant = esAlquenoOAlquino(functionalGroup); // Solo hay una posición posible para el enlace
			// Derivados del metil:
		else isRedundant = chain.getSize() == 1;

		return isRedundant;
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

		StringBuilder prefijo = new StringBuilder(chain.hasFunctionalGroup(FunctionalGroup.acid) ? "ácido " : "");
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
