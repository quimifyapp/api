package com.quimify.api.organico.tipos;

import com.quimify.api.organico.Organica;
import com.quimify.api.organico.componentes.Cadena;
import com.quimify.api.organico.componentes.Carbono;
import com.quimify.api.organico.componentes.Funciones;
import com.quimify.api.organico.componentes.Sustituyente;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// Esta clase representa compuestos formados por una sola cadena carbonada finita con sustituyentes.

public final class Simple extends Organica {

    private final Cadena cadena;

    // Constantes:

    private static final Set<Funciones> disponibles = Set.of( // Los tipos de sustiuyente añadibles por la interfaz
            Funciones.acido,
            Funciones.amida,
            Funciones.nitrilo,
            Funciones.aldehido,
            Funciones.cetona,
            Funciones.alcohol,
            Funciones.amina,
            Funciones.nitro,
            Funciones.bromo,
            Funciones.cloro,
            Funciones.fluor,
            Funciones.yodo,
            Funciones.radical,
            Funciones.hidrogeno
    );

    private static final Cadena CO2 = new Cadena(List.of(new Carbono(Funciones.cetona, 2))); // Auxiliar

    // Constructores:

    public Simple() {
        cadena = new Cadena();
    }

    public Simple(List<Integer> secuencia) {
        cadena = new Cadena(0);

        for(Integer eleccion : secuencia) {
            if(eleccion != -1) {
                List<Funciones> disponibles = getSustituyentesDisponibles();
                enlazar(disponibles.get(eleccion));
            }
            else enlazarCarbono();
        }
    }

    private Simple(Cadena nueva) {
        cadena = new Cadena(nueva);
    }

    // Interfaz:

    public boolean estaCompleta() {
        return cadena.estaCompleta();
    }

    public List<Funciones> getSustituyentesDisponibles() {
        List<Funciones> disponibles = new ArrayList<>();

        switch(getEnlacesLibres()) {
            case 4: // El primer carbono
            case 3:
                disponibles.add(Funciones.acido);
                disponibles.add(Funciones.amida);
                disponibles.add(Funciones.nitrilo);
                disponibles.add(Funciones.aldehido);
            case 2:
                disponibles.add(Funciones.cetona);
            case 1:
                disponibles.add(Funciones.alcohol);
                disponibles.add(Funciones.amina);
                disponibles.add(Funciones.nitro);
                disponibles.add(Funciones.bromo);
                disponibles.add(Funciones.cloro);
                disponibles.add(Funciones.fluor);
                disponibles.add(Funciones.yodo);
                disponibles.add(Funciones.radical);
                disponibles.add(Funciones.hidrogeno);
                // Hasta aquí
                break;
        }

        return disponibles;
    }

    public void corregir() {
        if(estaCompleta() && hayFunciones()) {
            // Estructura de carbonos:

            corregirRadicales();

            // Composición:

            // Cetona con alcohol → ácido:
            cadena.sustituirCetonaConPor(Funciones.alcohol, Funciones.acido); // C(O)(OH)- → COOH-

            // Cetona con amina → amida de poder ser principal:
            cadena.sustituirCetonaConPor(Funciones.amina, Funciones.amida); // C(O)(NH2)- → CONH2-

            // Cetona con hidrógeno → aldehído de poder ser principal:
            cadena.componerAldehido(); // CH(O)- → C(HO)

            // Descomposición:

            // Aldehído no principal → cetona con hidrógeno:
            cadena.descomponerAldehido(); // COOH-CHO → COOH-CH(O)

            // Amida no principal → carbamoil del anterior:
            cadena.sustituirTerminalPor(Funciones.amida, Funciones.carbamoil); // CONH2-COOH → C(OOH)(CONH2)

            // Nitrilos no principal → cianuro del anterior:
            cadena.sustituirTerminalPor(Funciones.nitrilo, Funciones.cianuro); // CN-COOH → C(OOH)(CN)

            // De nuevo (la estructura puede haber cambiado):

            corregirRadicales();

            // Orden:

            // Corrige el orden de la molécula según la prioridad y los localizadores:
            corregirOrden(); // butan-3-ol → butan-2-ol
        }
    }

    // Alías de interfaz:

    public void enlazar(Sustituyente sustituyente) {
        if(disponibles.contains(sustituyente.getFuncion()))
            cadena.enlazar(sustituyente);
        else throw new IllegalArgumentException("No se puede enlazar " + sustituyente.getFuncion() + " a un 'Simple'.");
    }

    public void enlazar(Funciones funcion) {
        enlazar(new Sustituyente(funcion));
    }

    public void enlazarCarbono() {
        cadena.enlazarCarbono();
    }

    // Modificadores:

    private void corregirRadicales() {
        // Se corrigen los radicales que podrían formar parte de la cadena principal:
        cadena.corregirRadicalesPorLaIzquierda(); // Comprobará internamente si hay radicales
        if(contiene(Funciones.radical)) { // Para ahorrar el invertir la cadena
            invertirOrden(); // En lugar de corregirlos por la derecha
            cadena.corregirRadicalesPorLaIzquierda(); // CHF(CH3)(CH2CH3) → CH3-CH2-CHF-CH3
        }
    }

    private void corregirOrden() {
        boolean corregido = false;

        Simple inversa = new Simple(cadena);
        inversa.invertirOrden();

        List<Funciones> funciones = getFuncionesOrdenadas();
        for(int i = 0; i < funciones.size() && !corregido; i++) {
            // Se calculan las sumas de sus posiciones:
            int suma_normal = getPosicionesDe(funciones.get(i)).stream().mapToInt(Integer::intValue).sum();
            int suma_inversa = inversa.getPosicionesDe(funciones.get(i)).stream().mapToInt(Integer::intValue).sum();

            // Se comparan las sumas de sus posiciones:
            corregido = corregirOrdenSegun(suma_normal - suma_inversa);
        }

        // Los radicales determinan el orden alfabéticamente como última instancia, solo cuando lo demás es indiferente.
        if(!corregido && contiene(Funciones.radical)) {
            // Se obtienen los radicales de ambas versiones, ordenados por sus carbonos:
            List<String> normales = new ArrayList<>();
            getRadicales().forEach(radical -> normales.add(Organica.nombreDeRadical(radical)));

            List<String> inversos = new ArrayList<>();
            inversa.getRadicales().forEach(radical -> inversos.add(Organica.nombreDeRadical(radical)));

            // Se comparan los radicales dos a dos desde ambos extremos alfabéticamente:
            for(int i = 0; i < normales.size() && !corregido; i++)
                corregido = corregirOrdenSegun(normales.get(i).compareTo(inversos.get(i)));
        }
    }

    private boolean corregirOrdenSegun(int comparacion) {
        boolean corregido;

        if(comparacion != 0) { // No son iguales
            if(comparacion > 0) // El inverso va antes alfabéticamente
                invertirOrden();

            corregido = true; // Ya se ha corregido el orden según los radicales alfabéticamente
        }
        else corregido = false; // Indecidible

        return corregido;
    }

    // Internos:

    private boolean esRedundante(Funciones funcion) {
        boolean es_redundante;

        // Sustituyentes terminales:
        if(funcion != Funciones.radical && !(esAlquenoOAlquino(funcion))) // Por: new Sustituyente(funcion)
            es_redundante = new Sustituyente(funcion).getEnlaces() == 3; // Solo puede ir en el primero y/o último
        // Derivados del propeno:
        else if(getSize() == 3)
            es_redundante = funcion == Funciones.alqueno && getCantidadDe(Funciones.alqueno) == 2; // Es propadieno
        // Derivados del etano:
        else if(getSize() == 2) {
            if(esAlquenoOAlquino(funcion) || contiene(Funciones.alquino)) // Solo hay una posición posible
                es_redundante = true;
            else es_redundante = getSustituyentesSinHidrogeno().size() == 1; // Solo hay uno, como cloroetino o etanol
        }
        // Derivados del metano:
        else es_redundante = getSize() == 1;

        return es_redundante;
    }

    // Texto: TODO: poner en común en Cadena

    private Localizador getPrefijoPara(Funciones funcion) {
        Localizador prefijo;

        List<Integer> posiciones = getPosicionesDe(funcion);
        String nombre = nombreDePrefijo(funcion);

        if(esRedundante(funcion)) // Sobran los localizadores porque son evidentes
            prefijo = new Localizador(multiplicadorDe(posiciones.size()), nombre); // Como "difluoro"
        else prefijo = new Localizador(posiciones, nombre); // Como "1,2-difluoro"

        return prefijo;
    }

    private String getEnlacePara(Funciones tipo) {
        String enlace = "";

        List<Integer> posiciones = getPosicionesDe(tipo);
        String nombre = nombreDeEnlace(tipo);

        if(posiciones.size() > 0) {
            Localizador localizador;

            if(esRedundante(tipo)) // Sobran los localizadores porque son evidentes
                localizador = new Localizador(multiplicadorDe(posiciones.size()), nombre); // Como "dien"
            else localizador = new Localizador(posiciones, nombre); // Como "1,2-dien"

            String localizador_to_string = localizador.toString();

            if(empiezaPorDigito(localizador_to_string))
                enlace += "-"; // Guion antes de los localizadores

            enlace += localizador_to_string;
        }

        return enlace;
    }

    private String getSufijoPara(Funciones funcion) {
        String sufijo;

        List<Integer> posiciones = getPosicionesDe(funcion);
        String nombre = nombreDeSufijo(funcion);

        if(esRedundante(funcion)) // Sobran los localizadores porque son evidentes
            sufijo = multiplicadorDe(posiciones.size()) + nombre; // Como "dioico"
        else sufijo = new Localizador(posiciones, nombre).toString(); // Como "2-3-diona"

        return sufijo;
    }

    // Se asume que ya está corregida con corregir()
    public String getNombre() {
        // Se anticipan los casos excepcionales:
        if(cadena.equals(CO2))
            return "dióxido de carbono";

        List<Funciones> funciones = getFuncionesOrdenadas(); // Sin hidrógeno
        int funcion = 0;

        // Se procesa el sufijo:
        String sufijo;
        if(funciones.size() > 0 && funciones.get(0) != Funciones.nitro && funciones.get(0) != Funciones.radical // Nunca son sufijos
                && !esHalogeno(funciones.get(0)) && !esAlquenoOAlquino(funciones.get(0)))
            sufijo = getSufijoPara(funciones.get(funcion++));
        else sufijo = "";

        // Se procesan los prefijos:
        List<Localizador> prefijos = new ArrayList<>();

        while(funcion < funciones.size()) {
            if(!esAlquenoOAlquino(funciones.get(funcion)) && funciones.get(funcion) != Funciones.radical)
                prefijos.add(getPrefijoPara(funciones.get(funcion)));

            funcion++;
        }

        List<Sustituyente> radicales = getRadicalesUnicos();
        for(Sustituyente radical : radicales)
            prefijos.add(new Localizador(getPosicionesDe(radical), nombreDeRadical(radical)));

        StringBuilder prefijo = new StringBuilder(contiene(Funciones.acido) ? "ácido " : "");
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
        String enlaces = getEnlacePara(Funciones.alqueno) + getEnlacePara(Funciones.alquino);

        if(enlaces.equals(""))
            enlaces = "an";
        if(sufijo.equals("") || Organica.noEmpiezaPorVocal(sufijo))
            enlaces += "o";
        if(!sufijo.equals("") && Organica.empiezaPorDigito(sufijo))
            enlaces += "-";

        // Se procesa el cuantificador:
        String cuantificador = cuantificadorDe(getSize());

        if(Organica.noEmpiezaPorVocal(enlaces))
            cuantificador += "a";

        return prefijo + cuantificador + enlaces + sufijo;
    }

    public String getFormula() {
        return cadena.getFormula();
    }

    // Alias:

    private void invertirOrden() {
        cadena.invertirOrden();
    }

    private int getSize() {
        return cadena.getSize();
    }

    private int getEnlacesLibres() {
        return cadena.getEnlacesLibres();
    }

    private int getCantidadDe(Funciones funcion) {
        return cadena.getCantidadDe(funcion);
    }

    private boolean hayFunciones() { // Sin hidrógeno
        return cadena.hayFunciones();
    }

    private boolean contiene(Funciones funcion) {
        return cadena.contiene(funcion);
    }

    private List<Integer> getPosicionesDe(Funciones funcion) {
        return cadena.getPosicionesDe(funcion);
    }

    private List<Integer> getPosicionesDe(Sustituyente sustituyente) {
        return cadena.getPosicionesDe(sustituyente);
    }

    private List<Funciones> getFuncionesOrdenadas() {
        return cadena.getFuncionesOrdenadas();
    }

    private List<Sustituyente> getRadicales() {
        return cadena.getRadicales();
    }

    private List<Sustituyente> getRadicalesUnicos() {
        return cadena.getRadicalesUnicos();
    }

    private List<Sustituyente> getSustituyentesSinHidrogeno() {
        return cadena.getSustituyentesSinHidrogeno();
    }

    @Override
    public String toString() {
        return getFormula();
    }

}
