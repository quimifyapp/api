package com.quimify.api.organico.tipos;



import com.quimify.api.organico.Organica;
import com.quimify.api.organico.componentes.Cadena;
import com.quimify.api.organico.componentes.Funciones;
import com.quimify.api.organico.componentes.Sustituyente;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Esta clase representa éteres: dos cadenas con funciones de prioridad menor a la función éter unidas por un oxígeno.

public final class Eter extends Organica {

	// R - O - R'

	private final Cadena primaria; // R
	private boolean hay_eter; // -O-
	private final Cadena secundaria; // R'

	private Cadena seleccionada; // Apunta a la cadena que se está formando (primarios | secundarios)

	// Constante:

	private static final Set<Funciones> disponibles = Set.of( // Los tipos de sustiuyente añadibles por la interfaz
			Funciones.eter,
			Funciones.nitro,
			Funciones.bromo,
			Funciones.cloro,
			Funciones.fluor,
			Funciones.yodo,
			Funciones.radical,
			Funciones.hidrogeno
	);

	// Constructores:

	public Eter() {
		primaria = new Cadena();
		secundaria = new Cadena(1); // (-C)

		seleccionada = primaria;
		hay_eter = false;
	}

	public Eter(Cadena primaria, Cadena secundaria) {
		Set<Funciones> funciones = new HashSet<>(primaria.getFuncionesOrdenadas());
		funciones.addAll(secundaria.getFuncionesOrdenadas());

		for(Funciones funcion : funciones)
			if(!disponibles.contains(funcion))
				throw new IllegalArgumentException("No se puede enlazar " + funcion + " a un 'Eter'.");

		this.primaria = primaria;
		this.secundaria = secundaria;
		empezarCadenaSecundaria();
	}

	public Eter(List<Integer> secuencia) {
		primaria = new Cadena(0);
		secundaria = new Cadena(1); // (-C)

		seleccionada = primaria;
		hay_eter = false;

		for(Integer eleccion : secuencia) {
			if(eleccion != -1) {
				List<Funciones> disponibles = getSustituyentesDisponibles();
				Funciones elegida = disponibles.get(eleccion);



				enlazar(disponibles.get(eleccion));
			}
			else enlazarCarbono();
		}
	}

	private void empezarCadenaSecundaria() {
		seleccionada = secundaria;
		hay_eter = true;
	}

	// Interfaz:

	public boolean estaCompleta() {
		return seleccionada.estaCompleta();
	}

	public List<Funciones> getSustituyentesDisponibles() {
		List<Funciones> disponibles = new ArrayList<>();

		switch(getEnlacesLibres()) {
			case 4: // El primer carbono
			case 3:
			case 2:
			case 1:
				if(hay_eter || getEnlacesLibres() > 1) {
					disponibles.add(Funciones.nitro);
					disponibles.add(Funciones.bromo);
					disponibles.add(Funciones.cloro);
					disponibles.add(Funciones.fluor);
					disponibles.add(Funciones.yodo);
					disponibles.add(Funciones.radical);
					disponibles.add(Funciones.hidrogeno);
				}
				else disponibles.add(Funciones.eter); // Los éteres admiten funciones de prioridad menor al éter
				// Hasta aquí
				break;
		}

		return disponibles;
	}

	public void enlazar(Sustituyente sustituyente) {
		if(disponibles.contains(sustituyente.getFuncion())) {
			seleccionada.enlazar(sustituyente);

			if (sustituyente.esTipo(Funciones.eter))
				empezarCadenaSecundaria();
		}
		else throw new IllegalArgumentException("No se puede enlazar " + sustituyente.getFuncion() + " a un 'Eter'.");
	}

	public void enlazar(Funciones funcion) {
		enlazar(new Sustituyente(funcion));
	}

	public void enlazarCarbono() {
		seleccionada.enlazarCarbono();
	}

	public void corregir() {
		if(estaCompleta() && hayFunciones()) {
			primaria.corregirRadicalesPorLaIzquierda(); // Comprobará internamente si hay radicales
			if(secundaria.contiene(Funciones.radical)) { // Para ahorrar el invertir la cadena
				secundaria.invertirOrden(); // En lugar de corregirlos por la derecha
				secundaria.corregirRadicalesPorLaIzquierda(); // CHF(CH3)(CH2CH3) → CH3-CH2-CHF-CH3
				secundaria.invertirOrden(); // Es necesario para no romper el orden del éter
			}
		}
	}

	// Modificadores:

	// ...

	// Internos:

	private boolean esRedundante(Funciones funcion, Cadena cadena) {
		boolean es_redundante;

		// Derivados del propil:
		if(cadena.getSize() == 3)
			es_redundante = funcion == Funciones.alqueno && cadena.getCantidadDe(Funciones.alqueno) == 2; // Es propadienil
		// Derivados del etil:
        else if(cadena.getSize() == 2)
			es_redundante = esAlquenoOAlquino(funcion); // Solo hay una posición posible para el enlace
		// Derivados del metil:
		else es_redundante = cadena.getSize() == 1;

		return es_redundante;
	}

	// Consultas:

	@Override
	public boolean equals(Object otro) {
		boolean es_igual;

		if(otro != null && otro.getClass() == this.getClass()) {
			Eter nuevo = (Eter) otro;

			es_igual = (primaria.equals(nuevo.primaria) && secundaria.equals(nuevo.secundaria))
					|| (primaria.equals(nuevo.secundaria) && secundaria.equals(nuevo.primaria));
		}
		else es_igual = false;

		return es_igual;
	}

	// Texto: TODO: poner en común en Cadena

	private Localizador getPrefijoParaEn(Funciones funcion, Cadena cadena) {
		Localizador prefijo;

		List<Integer> posiciones = cadena.getPosicionesDe(funcion);
		String nombre = nombreDePrefijo(funcion);

		if(esRedundante(funcion, cadena)) // Sobran los localizadores porque son evidentes
			prefijo = new Localizador(multiplicadorDe(posiciones.size()), nombre); // Como "difluoro"
		else prefijo = new Localizador(posiciones, nombre); // Como "1,2-difluoro"

		return prefijo;
	}

	private String getEnlaceParaEn(Funciones tipo, Cadena cadena) {
		String enlace = "";

		List<Integer> posiciones = cadena.getPosicionesDe(tipo);
		String nombre = nombreDeEnlace(tipo);

		if(posiciones.size() > 0) {
			Localizador localizador;

			if(esRedundante(tipo, cadena)) // Sobran los localizadores porque son evidentes
				localizador = new Localizador(multiplicadorDe(posiciones.size()), nombre); // Como "dien"
			else localizador = new Localizador(posiciones, nombre); // Como "1,2-dien"

			String localizador_to_string = localizador.toString();

			if(empiezaPorDigito(localizador_to_string))
				enlace += "-"; // Guion *antes* de los localizadores

			enlace += localizador_to_string;
		}

		return enlace;
	}

	private String getNombreCadena(Cadena cadena) {
		// Se anticipan los casos excepcionales:
		if(cadena.getFuncionesOrdenadas().size() == 1 && cadena.getRadicales().size() == 1) {
			if(cadena.getSize() > 1 && cadena.get(cadena.getSize() - 2).contiene(Sustituyente.CH3))
				return "iso" + cuantificadorDe(cadena.getSize() + 1) + "il";
			else if(cadena.getSize() > 2 && cadena.get(cadena.getSize() - 3).contiene(Sustituyente.CH3))
				return "sec" + cuantificadorDe(cadena.getSize() + 1) + "il";
		}

		List<Funciones> funciones = cadena.getFuncionesOrdenadas(); // Sin hidrógeno ni éter
		int funcion = 0;

		// Se procesan los prefijos:
		List<Localizador> prefijos = new ArrayList<>();

		while(funcion < funciones.size()) {
			if(!esAlquenoOAlquino(funciones.get(funcion)) && funciones.get(funcion) != Funciones.radical)
				prefijos.add(getPrefijoParaEn(funciones.get(funcion), cadena));

			funcion++;
		}

		List<Sustituyente> radicales = cadena.getRadicalesUnicos();
		for(Sustituyente radical : radicales)
			prefijos.add(new Localizador(cadena.getPosicionesDe(radical), nombreDeRadical(radical)));

		StringBuilder prefijo = new StringBuilder(cadena.contiene(Funciones.acido) ? "ácido " : "");
		if(prefijos.size() > 0) {
			Localizador.ordenarAlfabeticamente(prefijos);

			for(int i = 0; i < prefijos.size() - 1; i++) {
				prefijo.append(prefijos.get(i).toString());

				if(noEmpiezaPorLetra(prefijos.get(i + 1).toString()))
					prefijo.append("-");
			}

			prefijo.append(prefijos.get(prefijos.size() - 1));
		}

		// Se procesan los enlaces:
		String enlaces = getEnlaceParaEn(Funciones.alqueno, cadena) + getEnlaceParaEn(Funciones.alquino, cadena);

		// Se procesa el cuantificador:
		String cuantificador = cuantificadorDe(cadena.getSize());

		if(!enlaces.equals("") && Organica.noEmpiezaPorVocal(enlaces))
			cuantificador += "a";

		return prefijo + cuantificador + enlaces + "il";
	}

	public String getNombre() { // Se asume que ya está corregida con corregir()
		String nombre;

		String nombre_primaria = getNombreCadena(primaria.getInversa()); // Se empieza a contar desde el oxígeno
		String nombre_secundaria = getNombreCadena(secundaria); // La secundaria ya está en el orden bueno

		if(!nombre_primaria.equals(nombre_secundaria)) {
			// Se disponen en orden alfabético:
			if(nombre_primaria.compareTo(nombre_secundaria) < 0)
				nombre = nombre_primaria + " " + nombre_secundaria;
			else nombre = nombre_secundaria + " " + nombre_primaria;
		}
		else nombre = (empiezaPorDigito(nombre_primaria) ? "di " : "di") + nombre_primaria;

		return nombre + " éter";
	}

	public String getFormula() {
		String formula = "";

		formula += primaria.getFormula();

		if(hay_eter)
			formula += secundaria.getFormula();

		return formula;
	}

	// Alias:

	private int getEnlacesLibres() {
		return seleccionada.getEnlacesLibres();
	}

	private boolean hayFunciones() { // Sin hidrógeno ni éter
		return primaria.hayFunciones() || secundaria.hayFunciones();
	}

	@Override
	public String toString() {
		return getFormula();
	}

}
