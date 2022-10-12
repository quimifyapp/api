package com.quimify.api.organic;

import com.quimify.api.organic.components.FunctionalGroup;
import com.quimify.api.organic.components.Substituent;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.swap;

// Esta clase generaliza distintos tipos de compuestos orgánicos: cadenas simples, cíclicos, ésteres...

public class Organic {

    private static final List<FunctionalGroup> halogenos = Arrays.asList(FunctionalGroup.bromine, FunctionalGroup.chlorine, FunctionalGroup.fluorine, FunctionalGroup.iodine);

    // Consultas:

    protected static boolean esHalogeno(FunctionalGroup functionalGroup) {
        return halogenos.contains(functionalGroup);
    }

    protected static boolean esAlquenoOAlquino(FunctionalGroup functionalGroup) {
        return functionalGroup == FunctionalGroup.alkene || functionalGroup ==  FunctionalGroup.alkyne;
    }

    protected static void ordenarPorFunciones(List<Substituent> substituents) {
        for(int i = 0; i < substituents.size() - 1;) // Sin incremento
            if(substituents.get(i).getGroup().compareTo(substituents.get(i + 1).getGroup()) > 0) {
                swap(substituents, i, i + 1); // get(i) > get(i + 1)
                i = 0;
            }
            else i++; // get(i) <= get(i + 1)
    }

    // Texto:

    private static String prefijoGriegoDe(int numero) {
        String resultado;

        switch(numero) {
            case 0:
                resultado = "";
                break;
            case 1:
                resultado = "hen";
                break;
            case 2:
                resultado = "do";
                break;
            case 3:
                resultado = "tri";
                break;
            case 4:
                resultado = "tetra";
                break;
            case 5:
                resultado = "pent";
                break;
            case 6:
                resultado = "hex";
                break;
            case 7:
                resultado = "hept";
                break;
            case 8:
                resultado = "oct";
                break;
            case 9:
                resultado = "non";
                break;
            default:
                throw new IllegalArgumentException("No se contempla el prefijo griego para: " + numero);
        }

        return resultado;
    }

    protected static String cuantificadorDe(int numero) {
        String resultado;

        if(numero < 10) { // [1, 9]
            switch(numero) {
                case 1:
                    resultado = "met";
                    break;
                case 2:
                    resultado = "et";
                    break;
                case 3:
                    resultado = "prop";
                    break;
                case 4:
                    resultado = "but";
                    break;
                default: // 0, 5, 6, 7, 8, 9
                    resultado = prefijoGriegoDe(numero);
                    break;
            }
        }
        else if(numero == 11) // 11
            resultado = "undec";
        else { // 10 U [12, 999]
            int decenas = numero / 10;
            int unidades = numero - (decenas * 10);

            if(numero < 15) // 10 U [12, 14]
                resultado = prefijoGriegoDe(unidades) + "dec";
            else if(numero < 20) // [15, 19]
                resultado = prefijoGriegoDe(unidades) + "adec";
            else if(numero == 20) // 20
                resultado = "icos";
            else if(numero == 21) // 21
                resultado = "heneicos";
            else if(numero < 25) // [22, 25]
                resultado = prefijoGriegoDe(unidades) + "cos";
            else if(numero < 30) // [26, 29]
                resultado = prefijoGriegoDe(unidades) + "acos";
            else if(numero < 100) { // [30, 99]
                resultado = prefijoGriegoDe(unidades);

                if(unidades > 4)
                    resultado += "a";

                resultado += prefijoGriegoDe(decenas);

                if(decenas == 4)
                    resultado += "cont";
                else resultado += "acont";
            }
            else if(numero == 100) // 100
                resultado = "hect";
            else if(numero < 999) {  // [101, 999]
                int centenas = numero / 100;
                decenas = decenas - (centenas * 10);

                resultado = cuantificadorDe(10 * decenas + unidades); // Recursivo

                switch(centenas) {
                    case 1: // [101, 199]
                        resultado += "ahect";
                        break;
                    case 2: // [200, 299]
                        resultado += "adict";
                        break;
                    case 3: // [300, 399]
                        resultado += "atrict";
                        break;
                    case 4: // [400, 499]
                        resultado += "atetract";
                        break;
                    default: // [500, 999]
                        resultado += "a" + prefijoGriegoDe(centenas) + "act";
                        break;
                }
            }
            else throw new IllegalArgumentException("No se contempla este número de carbonos: " + numero); // > 999
        }

        return resultado;
    }

    protected static String multiplicadorDe(int numero) {
        String resultado;

        switch(numero) {
            case 1:
                resultado = "";
                break;
            case 2:
                resultado = "di";
                break;
            case 3:
                resultado = "tri";
                break;
            case 4:
                resultado = "tetra";
                break;
            default:
                resultado = cuantificadorDe(numero) + "a";
                break;
        }

        return resultado;
    }

    protected static class Localizador {

        // Esta clase representa un localizador de un nombre IUPAC, como "2,3-diol".

        private String posiciones, multiplicador, lexema;

        // EJEMPLOS:
        /*
            "2,3-diol"  =   { posiciones: "2,3",  multiplicador: "di",     lexema: "ol"     }
            "tetrain"   =   { posiciones: "",     multiplicador: "tetra",  lexema: "in"     }
            "fluoro"    =   { posiciones: "",     multiplicador: "",       lexema: "fluoro" }
        */

        private void construir(String posiciones, String multiplicador, String nombre) {
            this.posiciones = posiciones;
            this.multiplicador = multiplicador;
            this.lexema = nombre;
        }

        public Localizador(String multiplicador, String lexema) {
            construir("", multiplicador, lexema);
        }

        public Localizador(List<Integer> posiciones, String lexema) {
            StringBuilder auxiliar = new StringBuilder();

            if(posiciones.size() > 0) {
                for(int i = 0; i < posiciones.size() - 1; i++)
                    auxiliar.append(posiciones.get(i) + 1).append(",");
                auxiliar.append(posiciones.get(posiciones.size() - 1) + 1);
            }

            construir(auxiliar.toString(), Organic.multiplicadorDe(posiciones.size()), lexema);
        }

        // No se tienen en cuenta los multiplicadores ni las posiciones, como propone la IUPAC.
        // Ej.: "2,3-diol" → "ol"
        public static void ordenarAlfabeticamente(List<Localizador> localizadores) {
            localizadores.sort(Comparator.comparing(Localizador::getLexema));
        }

        @Override
        public String toString() {
            String resultado = "";

            if(!posiciones.equals(""))
                resultado = posiciones + "-";
            resultado += multiplicador + lexema;

            return resultado;
        }

        // Getters y setters:

        public String getLexema() {
            return lexema;
        }

    }

    protected static String getRadicalNameParticle(Substituent radical) {
        String nameParticle;

        if(radical.getIso())
            nameParticle = "iso";
        else nameParticle = "";

        nameParticle += cuantificadorDe(radical.getCarbonCount()) + "il";

        return nameParticle;
    }

    protected static String getPrefixNameParticle(FunctionalGroup functionalGroup) {
        String preffixNameParticle;

        switch(functionalGroup) {
            case carbamoyl:
                preffixNameParticle = "carbamoil";
                break;
            case cyanide:
                preffixNameParticle = "ciano";
                break;
            case ketone:
                preffixNameParticle = "oxo";
                break;
            case alcohol:
                preffixNameParticle = "hidroxi";
                break;
            case amine:
                preffixNameParticle = "amino";
                break;
            case nitro:
                preffixNameParticle = "nitro";
                break;
            case bromine:
                preffixNameParticle = "bromo";
                break;
            case chlorine:
                preffixNameParticle = "cloro";
                break;
            case fluorine:
                preffixNameParticle = "fluoro";
                break;
            case iodine:
                preffixNameParticle = "yodo";
                break;
            default:
                throw new IllegalArgumentException("No existen prefijos para la función " + functionalGroup + ".");
        }

        return preffixNameParticle;
    }

    protected static String getBondNameParticle(FunctionalGroup enlace) {
        String bondNameParticle;

        switch(enlace) {
            case alkene:
                bondNameParticle = "en";
                break;
            case alkyne:
                bondNameParticle = "in";
                break;
            default:
                throw new IllegalArgumentException("La función " + enlace + " no es un tipo de enlace.");
        }

        return bondNameParticle;
    }

    protected static String getSuffixNameParticle(FunctionalGroup functionalGroup) {
        String suffixNameParticle;

        switch(functionalGroup) {
            case acid:
                suffixNameParticle = "oico";
                break;
            case amide:
                suffixNameParticle = "amida";
                break;
            case nitrile:
                suffixNameParticle = "nitrilo";
                break;
            case aldehyde:
                suffixNameParticle = "al";
                break;
            case ketone:
                suffixNameParticle = "ona";
                break;
            case alcohol:
                suffixNameParticle = "ol";
                break;
            case amine:
                suffixNameParticle = "amina";
                break;
            default:
                throw new IllegalArgumentException("No existen sufijos para la función " + functionalGroup + ".");
        }

        return suffixNameParticle;
    }

    protected static String getBondSymbol(int bondOrder) {
        switch(bondOrder) {
            case 0:
                return "-";
            case 1:
                return "=";
            case 2:
                return "≡";
            default:
                throw new IllegalArgumentException("No existen enlaces de orden " + bondOrder + ".");
        }
    }

    protected static String getMolecularQuantifier(int count) {
        return count != 1 ? String.valueOf(count) : ""; // As in "CO2" or "CO"
    }

    protected static char firstLetterOf(String text) {
        return (char) text.chars().filter(c -> String.valueOf((char) c).matches("[a-zA-Z]"))
                .findFirst().orElse(0);
    }

    protected static boolean doesNotStartWithVowel(String text) {
        return "aeiou".indexOf(firstLetterOf(text)) == -1;
    }

    protected static boolean doesNotStartWithLetter(String text) {
        return text.charAt(0) != firstLetterOf(text);
    }

    protected static boolean startsWithDigit(String text) {
        return text.matches("^\\d.*$") ;
    }

}
