package com.quimify.api.organic.components;

import com.quimify.api.organic.Organic;

import java.util.*;

public class Chain extends Organic {

	private final List<Carbon> carbons;

	// Constructores:

	public Chain() {
		carbons = new ArrayList<>();
	}

	public Chain(int enlaces_previos) {
		carbons = new ArrayList<>();
		comenzar(enlaces_previos);
	}

	public Chain(Chain nueva) {
		carbons = new ArrayList<>();
		agregarCopiaDe(nueva);
	}

	public Chain(List<Carbon> nueva) {
		carbons = new ArrayList<>();
		agregarCopiaDe(nueva);
	}

	private void agregarCopiaDe(List<Carbon> otros) {
		for(Carbon carbon : otros)
			carbons.add(new Carbon(carbon));
	}

	private void agregarCopiaDe(Chain otra) {
		agregarCopiaDe(otra.carbons);
	}

	private void comenzar(int enlaces_previos) {
		carbons.add(new Carbon(enlaces_previos));
	}

	// Modificadores:

	public void enlazar(Substituent substituent) {
		getUltimo().enlazar(substituent);
	}

	public void enlazar(FunctionalGroup functionalGroup) {
		enlazar(new Substituent(functionalGroup));
	}

	private void enlazar(Substituent substituent, int veces) {
		getUltimo().enlazar(substituent, veces);
	}

	public void enlazar(FunctionalGroup functionalGroup, int veces) {
		enlazar(new Substituent(functionalGroup), veces);
	}

	private void enlazar(List<Carbon> otra) {
		Carbon ultimo = getUltimo();
		ultimo.enlazarCarbono();
		agregarCopiaDe(otra);
	}

	public void enlazarCarbono() {
		if(carbons.size() > 0) {
			if(getEnlacesLibres() > 0) {
				Carbon ultimo = getUltimo();
				ultimo.enlazarCarbono();
				carbons.add(new Carbon(ultimo.getEnlacesLibres() + 1));
			}
			else throw new IllegalStateException("No se puede enlazar un carbono a [" + getStructure() + "].");
		}
		else comenzar(0); // TODO: sirve??
	}

	private void transformarEn(Chain otra) {
		carbons.clear();
		agregarCopiaDe(otra);
	}

	public void invertirOrden() {
		transformarEn(getInversa());
	}

	public void corregirRadicalesPorLaIzquierda() { // CH2(CH3)-C≡ → CH3-CH2-C≡
		boolean hubo_correcion; // Para actualizar el iterador tras iteración

		for (int i = 0; i < carbons.size(); i = hubo_correcion ? 0 : i + 1) { // Sin incremento
			if(carbons.get(i).getSustituyentesTipo(FunctionalGroup.radical).size() > 0) { // Este carbono tiene radicales
				// Se obtiene el mayor radical de este carbono:
				Substituent mayor_radical = carbons.get(i).getMayorRadical();

				// Se calcula si el "camino" por este radical es preferible a la cadena principal:
				int comparacion = Integer.compare(mayor_radical.getCarbonosRectos(), i);

				if(comparacion == 1 || (comparacion == 0 && mayor_radical.getIso())) {
					// Se corrige la cadena por la izquierda:
					if(i != 0) {
						// Se convierte el camino antiguo de la cadena principal en radical:
						Substituent antiguo;

						// Aquí se tiene en cuenta que, de haber un radical, solo podría ser metil
						if(i > 1 && carbons.get(1).contiene(FunctionalGroup.radical) // Hay un metil en el segundo carbono
								&& carbons.get(1).getSubstituentsWithoutHydrogen().get(0).equals(Substituent.CH3))
							antiguo = new Substituent(i + 1, true);
						else antiguo = new Substituent(i);

						// Se enlaza tal radical:
						carbons.get(i).enlazar(antiguo);

						// Se elimina el radical que será el camino de la cadena principal:
						carbons.get(i).eliminarConEnlaces(mayor_radical);

						// Se elimina el camino antiguo de la cadena principal:
						carbons.subList(0, i).clear();
					}
					else carbons.get(0).eliminar(mayor_radical); // Será el camino de la cadena principal

					// Se convierte el radical en el nuevo camino de la cadena principal:
					Chain parte_izquierda = mayor_radical.getCadena();
					parte_izquierda.enlazar(carbons);

					// Se efectúa el cambio:
					transformarEn(parte_izquierda);
					hubo_correcion = true;
				}
				else hubo_correcion = false;
			}
			else hubo_correcion = false;

			// Se comprueba si este carbono no podría estar en un radical, ergo debe pertenecer a la cadena principal:
			List<Substituent> substituents = carbons.get(i).getSubstituentsWithoutHydrogen(); // (Se puede asumir que los
			// carbonos anteriores sí podían estar en un radical gracias a los 'break')

			if(substituents.size() > 0) { // Hay sustituyentes distintos del hidrógeno
				if(!(i == 1 && substituents.size() == 1 && substituents.get(0).getCarbonCount() == 1))
					break; // Y estos no son un solo metil en el segundo carbono (no podría formar un radical 'iso')
				else if(carbons.get(i).getEnlacesLibres() > 0)
					break; // Le sigue un alqueno o alquino
			}
		}
	}

	public void componerAldehido() {
		if(getFuncionPrioritaria().compareTo(FunctionalGroup.aldehyde) >= 0) // No hay otra de mayor prioridad, puede haber aldehídos
			sustituirCetonaConPor(FunctionalGroup.hydrogen, FunctionalGroup.aldehyde);
	}

	public void sustituirCetonaConPor(FunctionalGroup complementaria, FunctionalGroup sustituta) { // C(O)(A)- → C(B)-
		sustituirCetonaConPorEn(complementaria, sustituta, carbons.get(0));
		sustituirCetonaConPorEn(complementaria, sustituta, getUltimo());
	}

	private void sustituirCetonaConPorEn(FunctionalGroup complementaria, FunctionalGroup sustituta, Carbon terminal) { // C(O)(A)- → C(B)-
		if(terminal.contiene(FunctionalGroup.ketone) && terminal.contiene(complementaria)) {
			terminal.eliminarConEnlaces(FunctionalGroup.ketone);
			terminal.eliminarConEnlaces(complementaria);
			terminal.enlazar(sustituta);
		}
	}

	public void sustituirTerminalPor(FunctionalGroup terminal, FunctionalGroup functionalGroup) { // COOH-C(A)- → COOH(CA)-
		if(getFuncionPrioritaria() != terminal) { // Hay una función de mayor prioridad, se debe descomponer el terminal
			if(carbons.size() >= 2) // Para poder acceder a cadena.get(1)
				sustituirTerminalDePorEn(terminal, carbons.get(0), functionalGroup, carbons.get(1));
			if(carbons.size() >= 2) // Para poder acceder a cadena.get(cadena.size() - 2)
				sustituirTerminalDePorEn(terminal, getUltimo(), functionalGroup, carbons.get(carbons.size() - 2));
		}
	}

	private void sustituirTerminalDePorEn(FunctionalGroup terminal, Carbon carbon, FunctionalGroup functionalGroup, Carbon otro) { // CX-C≡ → C(CX)≡
		if(carbon.contiene(terminal)) {
			carbons.remove(carbon);
			otro.eliminarEnlace();
			otro.enlazar(functionalGroup);
		}
	}

	public void descomponerAldehido() { // COOH-CHO → COOH-CH(O)
		if(getFuncionPrioritaria() != FunctionalGroup.aldehyde) { // Hay otra de mayor prioridad, se debe descomponer el aldehído
			descomponerAldehidoEn(carbons.get(0));
			descomponerAldehidoEn(getUltimo());
		}
	}

	private void descomponerAldehidoEn(Carbon carbon) { // COOH-CHO → COOH-CH(O)
		if(carbon.contiene(FunctionalGroup.aldehyde)) {
			carbon.eliminarConEnlaces(FunctionalGroup.aldehyde);
			carbon.enlazar(FunctionalGroup.ketone);
			carbon.enlazar(FunctionalGroup.hydrogen);
		}
	}

	// QUERIES -----------------------------------------------------------------------

	public int getSize() {
		return carbons.size();
	}

	public boolean isDone() {
		return getEnlacesLibres() == 0;
	}

	public boolean hasGroup(FunctionalGroup functionalGroup) {
		for(Carbon carbon : carbons)
			if(carbon.contiene(functionalGroup))
				return true;

		return false;
	}

	public boolean hasGroupsWithoutHydrogenNorEther() { // Sin hidrógeno ni éter
		for(FunctionalGroup functionalGroup : FunctionalGroup.values()) // Todas las funciones recogidas en Group
			if(functionalGroup != FunctionalGroup.hydrogen && functionalGroup != FunctionalGroup.ether)
				for(Carbon carbon : carbons)
					if(carbon.contiene(functionalGroup))
						return true;

		return false;
	}

	@Override
	public boolean equals(Object other) {
		boolean isEqual = false;

		if(other != null && other.getClass() == this.getClass()) {
			Chain otherChain = (Chain) other;

			if(carbons.size() == otherChain.getSize())
				for(int i = 0; i < carbons.size(); i++)
					if(carbons.get(i).equals(otherChain.carbons.get(i))) {
						isEqual = true;
						break;
					}
		}

		return isEqual;
	}

	public Chain getInversa() {
		Chain inversa = new Chain(carbons);

		// Le da la vuelta a los carbonos:
		Collections.reverse(inversa.carbons);

		// Ajusta los enlaces (no son simétricos):
		if(inversa.getSize() > 1) {
			for(int i = 0, j = carbons.size() - 2; i < inversa.getSize() - 1; i++)
				inversa.carbons.get(i).setEnlacesLibres(carbons.get(j--).getEnlacesLibres());

			inversa.carbons.get(inversa.getSize() - 1).setEnlacesLibres(0); // Se supone que no tiene enlaces sueltos
		}

		return inversa;
	}

	private Carbon getUltimo() {
		return carbons.get(carbons.size() - 1);
	}

	public int getEnlacesLibres() {
		return getUltimo().getEnlacesLibres();
	}

	public int getNumberOf(FunctionalGroup functionalGroup) {
		int cantidad = 0;

		for(Carbon carbon : carbons)
			cantidad += carbon.getCantidadDe(functionalGroup);

		return cantidad;
	}

	public FunctionalGroup getFuncionPrioritaria() { // Con hidrógeno
		for(FunctionalGroup functionalGroup : FunctionalGroup.values()) // Todas las funciones recogidas en Id
			for(Carbon carbon : carbons)
				if(carbon.contiene(functionalGroup))
					return functionalGroup;

		return null;
	}

	public List<FunctionalGroup> getOrderedGroupsWithoutHydrogenNorEther() { // Sin hidrógeno ni éter
		List<FunctionalGroup> funciones = new ArrayList<>(); // Funciones presentes sin repetición y en orden

		for(FunctionalGroup functionalGroup : FunctionalGroup.values()) // Todas las funciones recogidas en Id
			if(functionalGroup != FunctionalGroup.hydrogen && functionalGroup != FunctionalGroup.ether) // Excepto hidrógeno y éter
				for(Carbon carbon : carbons)
					if(carbon.contiene(functionalGroup)) {
						funciones.add(functionalGroup);
						break;
					}

		return funciones;
	}

	public List<Integer> getIndexesOfAll(FunctionalGroup functionalGroup) {
		List<Integer> posiciones = new ArrayList<>(); // Posiciones de los carbonos con la función

		for(int i = 0; i < carbons.size(); i++) {
			int cantidad = carbons.get(i).getCantidadDe(functionalGroup);

			for(int j = 0; j < cantidad; j++)
				posiciones.add(i);
		}

		return posiciones;
	}

	public List<Integer> getIndexesOfAll(Substituent substituent) {
		List<Integer> posiciones = new ArrayList<>(); // Posiciones de los carbonos enlazados al sustituyente

		for(int i = 0; i < carbons.size(); i++) {
			int cantidad = carbons.get(i).getCantidadDe(substituent);

			for(int j = 0; j < cantidad; j++)
				posiciones.add(i);
		}

		return posiciones;
	}

	public List<Substituent> getRadicalSubstituents() {
		List<Substituent> substituents = new ArrayList<>();

		for(Carbon carbon : carbons)
			substituents.addAll(carbon.getSustituyentesTipo(FunctionalGroup.radical));

		return substituents;
	}

	public List<Substituent> getUniqueRadicals() {
		List<Substituent> unicos = new ArrayList<>();

		for(Carbon carbon : carbons)
			for(Substituent substituent : carbon.getSustituyentesTipo(FunctionalGroup.radical))
				if(!unicos.contains(substituent))
					unicos.add(substituent);

		return unicos;
	}

	public List<Substituent> getSubstituentsWithoutHydrogen() {
		List<Substituent> substituentsWithoutHydrogen = new ArrayList<>();

		for(Carbon carbon : carbons)
			substituentsWithoutHydrogen.addAll(carbon.getSubstituentsWithoutHydrogen());

		return substituentsWithoutHydrogen;
	}
	
	public boolean hasMethylAt(int index) {
		return getSize() > index && carbons.get(getSize() - index - 1).contiene(Substituent.CH3);
	}

	// Texto:

	public String getStructure() {
		StringBuilder formula = new StringBuilder();

		if(carbons.size() > 0) {
			// Se escribe el primero:
			Carbon primero = carbons.get(0);
			formula.append(primero); // Como CH

			// Se escribe el resto con los enlaces libres del anterior:
			int enlaces_libres_anterior = primero.getEnlacesLibres();
			for(int i = 1; i < carbons.size(); i++) {
				formula.append(getBondSymbol(enlaces_libres_anterior)); // Como CH=
				formula.append(carbons.get(i)); // Como CH=CH

				enlaces_libres_anterior = carbons.get(i).getEnlacesLibres();
			}

			// Se escribe los enlaces libres del último:
			if(enlaces_libres_anterior > 0 && enlaces_libres_anterior < 4) // Ni está completo ni es el primero vacío
				formula.append(getBondSymbol(enlaces_libres_anterior - 1)); // Como CH=CH-CH2-C≡
		}

		return formula.toString();
	}

}
