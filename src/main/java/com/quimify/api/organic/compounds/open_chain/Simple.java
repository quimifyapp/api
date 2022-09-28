package com.quimify.api.organic.compounds.open_chain;

import com.quimify.api.organic.Organic;
import com.quimify.api.organic.components.Chain;
import com.quimify.api.organic.components.Carbon;
import com.quimify.api.organic.components.FunctionalGroup;
import com.quimify.api.organic.components.Substituent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// Esta clase representa compuestos formados por una sola cadena carbonada finita con sustituyentes.

public final class Simple extends Organic implements OpenChain {

    private final Chain chain;

    private static final Chain CO2 = new Chain(List.of(new Carbon(FunctionalGroup.ketone, 2))); // Auxiliar

    private static final Set<FunctionalGroup> bondableGroups = Set.of(
            FunctionalGroup.acid, FunctionalGroup.amide, FunctionalGroup.nitrile, FunctionalGroup.aldehyde,
            FunctionalGroup.ketone, FunctionalGroup.alcohol, FunctionalGroup.amine, FunctionalGroup.nitro,
            FunctionalGroup.bromine, FunctionalGroup.chlorine, FunctionalGroup.fluorine, FunctionalGroup.iodine,
            FunctionalGroup.radical, FunctionalGroup.hydrogen
    );

    public Simple() {
        this.chain = new Chain(0);
    }

    public Simple(int previousBonds) {
        this.chain = new Chain(previousBonds);
    }

    private Simple(Chain chain) {
        this.chain = new Chain(chain);
    }

    // OPEN CHAIN --------------------------------------------------------------------

    public Simple getReversed() {
        return new Simple(chain.getInversa());
    }

    public int getFreeBonds() {
        return chain.getEnlacesLibres();
    }

    public boolean isDone() {
        return chain.isDone();
    }

    public List<FunctionalGroup> getOrderedBondableGroups() {
        List<FunctionalGroup> orderedBondableGroups = new ArrayList<>();

        if (getFreeBonds() > 2)
            orderedBondableGroups.addAll(List.of(
                    FunctionalGroup.acid, FunctionalGroup.amide, FunctionalGroup.nitrile, FunctionalGroup.aldehyde)
            );

        if (getFreeBonds() > 1)
            orderedBondableGroups.add(FunctionalGroup.ketone);

        if (getFreeBonds() > 0) {
            orderedBondableGroups.addAll(List.of(FunctionalGroup.alcohol, FunctionalGroup.amine));

            if (getFreeBonds() == 1)
                orderedBondableGroups.add(FunctionalGroup.ether);

            orderedBondableGroups.addAll(List.of(
                    FunctionalGroup.nitro, FunctionalGroup.bromine, FunctionalGroup.chlorine, FunctionalGroup.fluorine,
                    FunctionalGroup.iodine, FunctionalGroup.radical, FunctionalGroup.hydrogen)
            );
        }

        return orderedBondableGroups;
    }

    public void bondCarbon() {
        chain.enlazarCarbono();
    }

    public void bond(Substituent substituent) {
        if (bondableGroups.contains(substituent.getGroup()))
            chain.enlazar(substituent);
        else throw new IllegalArgumentException("No se puede enlazar [" + substituent.getGroup() + "] a un Simple.");
    }

    public void bond(FunctionalGroup functionalGroup) {
        bond(new Substituent(functionalGroup));
    }

    public void correctSubstituents() {
        if(isDone() && chain.hasGroupsWithoutHydrogenNorEther()) {
            // Estructura de la cadena:
            correctRadicalSubstituents();

            // Composición:

            // Cetona con alcohol → ácido:
            chain.sustituirCetonaConPor(FunctionalGroup.alcohol, FunctionalGroup.acid); // C(O)(OH)- → COOH-

            // Cetona con amina → amida de poder ser principal:
            chain.sustituirCetonaConPor(FunctionalGroup.amine, FunctionalGroup.amide); // C(O)(NH2)- → CONH2-

            // Cetona con hidrógeno → aldehído de poder ser principal:
            chain.componerAldehido(); // CH(O)- → C(HO)

            // Descomposición:

            // Aldehído no principal → cetona con hidrógeno:
            chain.descomponerAldehido(); // COOH-CHO → COOH-CH(O)

            // Amida no principal → carbamoil del anterior:
            chain.sustituirTerminalPor(FunctionalGroup.amide, FunctionalGroup.carbamoyl); // CONH2-COOH → C(OOH)(CONH2)

            // Nitrilos no principal → cianuro del anterior:
            chain.sustituirTerminalPor(FunctionalGroup.nitrile, FunctionalGroup.cyanide); // CN-COOH → C(OOH)(CN)

            // De nuevo (la estructura puede haber cambiado):
            correctRadicalSubstituents();

            // Corrige el orden de la molécula según la prioridad y los localizadores:
            correctOrder(); // butan-3-ol → butan-2-ol
        }
    }

    public String getName() {
        // Se anticipan los casos excepcionales:
        if(chain.equals(CO2))
            return "dióxido de carbono";

        List<FunctionalGroup> funciones = chain.getOrderedGroupsWithoutHydrogenNorEther(); // Sin hidrógeno
        int functionalGroup = 0;

        // Se procesa el sufijo:
        String sufijo;
        if(funciones.size() > 0 && funciones.get(0) != FunctionalGroup.nitro && funciones.get(0) != FunctionalGroup.radical // Not suffixes
                && !esHalogeno(funciones.get(0)) && !esAlquenoOAlquino(funciones.get(0)))
            sufijo = getSuffixNameFor(funciones.get(functionalGroup++));
        else sufijo = "";

        // Se procesan los prefijos:
        List<Localizador> prefijos = new ArrayList<>();

        while(functionalGroup < funciones.size()) {
            if(!esAlquenoOAlquino(funciones.get(functionalGroup)) && funciones.get(functionalGroup) != FunctionalGroup.radical)
                prefijos.add(getPrefixFor(funciones.get(functionalGroup)));

            functionalGroup++;
        }

        List<Substituent> radicales = chain.getUniqueRadicals();
        for(Substituent radical : radicales)
            prefijos.add(new Localizador(chain.getIndexesOfAll(radical), getRadicalNameParticle(radical)));

        StringBuilder prefijo = new StringBuilder(chain.hasGroup(FunctionalGroup.acid) ? "ácido " : "");
        if(prefijos.size() > 0) {
            Localizador.ordenarAlfabeticamente(prefijos);

            for(int i = 0; i < prefijos.size() - 1; i++) {
                prefijo.append(prefijos.get(i).toString());

                if(doesNotStartWithLetter(prefijos.get(i + 1).toString()))
                    prefijo.append("-");
            }

            prefijo.append(prefijos.get(prefijos.size() - 1));
        }

        // Se procesan los enlaces:
        String enlaces = getBondNameFor(FunctionalGroup.alkene) + getBondNameFor(FunctionalGroup.alkyne);

        if(enlaces.equals(""))
            enlaces = "an";
        if(sufijo.equals("") || Organic.doesNotStartWithVowel(sufijo))
            enlaces += "o";
        if(!sufijo.equals("") && Organic.startsWithDigit(sufijo))
            enlaces += "-";

        // Se procesa el cuantificador:
        String cuantificador = cuantificadorDe(chain.getSize());

        if(Organic.doesNotStartWithVowel(enlaces))
            cuantificador += "a";

        return prefijo + cuantificador + enlaces + sufijo;
    }

    public String getStructure() {
        return chain.getStructure();
    }

    // EXTRA -------------------------------------------------------------------------

    Chain getChain() {
        return chain;
    }

    // PRIVATE -----------------------------------------------------------------------

    // Modifiers:

    private void reverse() {
        chain.invertirOrden();
    }

    private void correctRadicalSubstituents() {
        // Se corrigen los radicales que podrían formar parte de la cadena principal:
        chain.corregirRadicalesPorLaIzquierda(); // Comprobará internamente si hay radicales
        if(chain.hasGroup(FunctionalGroup.radical)) { // Para ahorrar el invertir la cadena
            reverse(); // En lugar de corregirlos por la derecha
            chain.corregirRadicalesPorLaIzquierda(); // CHF(CH3)(CH2CH3) → CH3-CH2-CHF-CH3
        }
    }

    private boolean correctOrderBasedOn(int comparaison) {
        boolean corrected;

        if(comparaison != 0) { // No son iguales
            if(comparaison > 0) // El inverso va antes alfabéticamente
                reverse();

            corrected = true; // Ya se ha corregido el orden según los radicales alfabéticamente
        }
        else corrected = false; // Indecidible

        return corrected;
    }

    private void correctOrder() {
        boolean corrected = false;

        Simple reversed = getReversed();

        List<FunctionalGroup> funciones = chain.getOrderedGroupsWithoutHydrogenNorEther();
        for(int i = 0; i < funciones.size() && !corrected; i++) {
            // Se calculan las sumas de sus posiciones:
            int suma_normal = chain.getIndexesOfAll(funciones.get(i))
                    .stream().mapToInt(Integer::intValue).sum();
            int suma_inversa = reversed.chain.getIndexesOfAll(funciones.get(i))
                    .stream().mapToInt(Integer::intValue).sum();

            // Se comparan las sumas de sus posiciones:
            corrected = correctOrderBasedOn(suma_normal - suma_inversa);
        }

        // Los radicales determinan el orden alfabéticamente como última instancia, solo cuando lo demás es indiferente.
        if(!corrected && chain.hasGroup(FunctionalGroup.radical)) {
            // Se obtienen los radicales de ambas versiones, ordenados por sus carbonos:
            List<String> normales = new ArrayList<>();
            chain.getRadicalSubstituents().forEach(radical -> normales.add(Organic.getRadicalNameParticle(radical)));

            List<String> inversos = new ArrayList<>();
            reversed.chain.getRadicalSubstituents().forEach(radical -> inversos.add(Organic.getRadicalNameParticle(radical)));

            // Se comparan los radicales dos a dos desde ambos extremos alfabéticamente:
            for(int i = 0; i < normales.size() && !corrected; i++)
                corrected = correctOrderBasedOn(normales.get(i).compareTo(inversos.get(i)));
        }
    }
    
    // Text: TODO: poner en común en Cadena

    private boolean isRedundantInName(FunctionalGroup bond) {
        boolean es_redundante;

        // Sustituyentes terminales:
        if(bond != FunctionalGroup.radical && !(esAlquenoOAlquino(bond))) // Por: new Sustituyente(functionalGroup)
            es_redundante = new Substituent(bond).getEnlaces() == 3; // Solo puede ir en el primero y/o último
            // Derivados del propeno:
        else if(chain.getSize() == 3)
            es_redundante = bond == FunctionalGroup.alkene && chain.getNumberOf(FunctionalGroup.alkene) == 2; // Es propadieno
            // Derivados del etano:
        else if(chain.getSize() == 2) {
            if(esAlquenoOAlquino(bond) || chain.hasGroup(FunctionalGroup.alkyne)) // Solo hay una posición posible
                es_redundante = true;
            else es_redundante = chain.getSubstituentsWithoutHydrogen().size() == 1; // Solo hay uno, como cloroetino o etanol
        }
        // Derivados del metano:
        else es_redundante = chain.getSize() == 1;

        return es_redundante;
    }

    private Localizador getPrefixFor(FunctionalGroup functionalGroup) {
        Localizador preffix;

        List<Integer> posiciones = chain.getIndexesOfAll(functionalGroup);
        String nombre = getPrefixNameParticle(functionalGroup);

        if(isRedundantInName(functionalGroup)) // Sobran los localizadores porque son evidentes
            preffix = new Localizador(multiplicadorDe(posiciones.size()), nombre); // Como "difluoro"
        else preffix = new Localizador(posiciones, nombre); // Como "1,2-difluoro"

        return preffix;
    }

    private String getBondNameFor(FunctionalGroup bond) {
        String bondName = "";

        List<Integer> posiciones = chain.getIndexesOfAll(bond);
        String nombre = getBondNameParticle(bond);

        if(posiciones.size() > 0) {
            Localizador localizador;

            if(isRedundantInName(bond)) // Sobran los localizadores porque son evidentes
                localizador = new Localizador(multiplicadorDe(posiciones.size()), nombre); // Como "dien"
            else localizador = new Localizador(posiciones, nombre); // Como "1,2-dien"

            String localizador_to_string = localizador.toString();

            if(startsWithDigit(localizador_to_string))
                bondName += "-"; // Guion antes de los localizadores

            bondName += localizador_to_string;
        }

        return bondName;
    }

    private String getSuffixNameFor(FunctionalGroup bond) {
        String suffixName;

        List<Integer> posiciones = chain.getIndexesOfAll(bond);
        String nombre = getSuffixNameParticle(bond);

        if(isRedundantInName(bond)) // Sobran los localizadores porque son evidentes
            suffixName = multiplicadorDe(posiciones.size()) + nombre; // Como "dioico"
        else suffixName = new Localizador(posiciones, nombre).toString(); // Como "2-3-diona"

        return suffixName;
    }

    @Override
    public String toString() {
        return getStructure();
    }

}
