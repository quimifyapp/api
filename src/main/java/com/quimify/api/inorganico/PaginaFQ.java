package com.quimify.api.inorganico;

import java.text.NumberFormat;

// Esta clase contiene el código para analizar una página de FQ.com.

public class PaginaFQ {

    private String pagina; // Documento HTML
    boolean escaneado_correcto = false; // No ha dado ningún fallo al escanear la página

    // --------------------------------------------------------------------------------

    PaginaFQ(String pagina) {
        this.pagina = pagina;
    }

    public boolean getEscaneadoCorrecto() {
        return escaneado_correcto;
    }

    // Flowchart #5
    public InorganicoModel escanearInorganico() {
        InorganicoModel resultado = new InorganicoModel();

        pagina = pagina.substring(indiceDespuesDeEn("<h1>", pagina)); // Lo relevante

        String nombre, formula;

        int indice = indiceDespuesDeEn("/", pagina);
        int indice_2 = indiceDespuesDeEn("<", pagina) + 1;
        if(indice != indice_2) { // "Co2(CO3)3 / carbonato de cobalto (III)</h1>..."
            nombre = pagina.substring(indice + 1, indice_2 - 2);
            formula = pagina.substring(0, indice - 2);
        }
        else { // "metano</h1>..."
            nombre = pagina.substring(0, indice_2 - 2);

            indice = indiceDespuesDeEn(">Fórmula:", pagina);
            if (indice == -1)
                indice = indiceDespuesDeEn("\"frm\">", pagina);

            formula = pagina.substring(indice);
            formula = formula.substring(0, indiceDespuesDeEn("</p>", formula) - 4);

            formula = formula.replaceAll("</b>", "")
                    .replaceAll("<sub>", "")
                    .replaceAll("</sub>", "")
                    .replaceAll(" ", "");
        }

        /*
        ÁCIDOS:
            nombre:         TRADICIONAL > STOCK > SISTEMÁTICA
            alternativo:    TÍTULO (solo si no es "oxo...")
        OTROS:
            nombre:         TÍTULO
            alternativo:    STOCK > SISTEMÁTICA > TRADICIONAL
        */

        String alternativo = null;

        int indice_tradicional = indiceDespuesDeEn("tradicional:</b>", pagina);
        int indice_stock = indiceDespuesDeEn("stock:</b>", pagina);

        // Si no pone "ácido" ni en el título ni en tradicional, si tuviera
        if(!((indiceDespuesDeEn("ácido", nombre) != -1) ||
                (indice_tradicional != -1 && indiceDespuesDeEn("ácido",
                        pagina.substring(indice_tradicional, indice_tradicional + 6)) != -1))) {

            if (indice_stock != -1) {
                // Hay nomenclatura stock en la página
                String dato = pagina.substring(indice_stock + 1);
                alternativo = dato.substring(0, indiceDespuesDeEn("</p>", dato) - 4);
            }
            if (indice_stock == -1 || nombre.contentEquals(alternativo)) {
                // No había stock o había pero es igual
                int indice_sistematica = indiceDespuesDeEn("sistemática:</b>", pagina);

                if (indice_sistematica != -1) {
                    // Hay sistemática
                    String dato = pagina.substring(indice_sistematica + 1);
                    alternativo = dato.substring(0, indiceDespuesDeEn("</p>", dato) - 4);
                }
                if (indice_sistematica == -1 || nombre.contentEquals(alternativo)) {
                    // No había sistemática o había pero es igual
                    if (indice_tradicional != -1) {
                        String dato = pagina.substring(indice_tradicional + 1);
                        alternativo = dato.substring(0,
                                indiceDespuesDeEn("</p>", dato) - 4);
                    }
                }
            }
        }
        else { // Pone "ácido" en el título o en tradicional si lo tuviera
            if (indice_tradicional != -1) { // Hay tradicional
                alternativo = nombre;
                String dato = pagina.substring(indice_tradicional + 1);
                nombre = dato.substring(0, indiceDespuesDeEn("</p>", dato) - 4);
            }
            if (indice_tradicional == -1 || nombre.contentEquals(alternativo)) {
                // No había tradicional o había pero es igual
                if (indice_stock != -1) { // Hay nomenclatura stock en la página
                    String dato = pagina.substring(indice_stock + 1);
                    alternativo = dato.substring(0, indiceDespuesDeEn("</p>", dato) - 4);
                }
                if (indice_stock == -1 || nombre.contentEquals(alternativo)) {
                    // No había stock o había pero es igual
                    if (indiceDespuesDeEn("sistemática:</b>", pagina) != -1) {
                        // Hay sistemática
                        String dato = pagina.substring(indiceDespuesDeEn(
                                "sistemática:</b>", pagina) + 1);
                        alternativo = dato.substring(0,
                                indiceDespuesDeEn("</p>", dato) - 4);
                    }
                }
            }
        }

        if (alternativo != null && (nombre.contentEquals(alternativo) ||
                (indiceDespuesDeEn("oxo", alternativo) != -1)))
            alternativo = null;

        if(indiceDespuesDeEn("br/>", nombre) != -1) {
            nombre = nombre.substring(4);
            if (nombre.contentEquals(alternativo))
                alternativo = null;

            resultado.setPremium(true);
        }

        if(alternativo != null && indiceDespuesDeEn("br/>", alternativo) != -1) {
            alternativo = alternativo.substring(4);
            if (nombre.contentEquals(alternativo))
                alternativo = null;

            resultado.setPremium(true);
        }

        resultado.setFormula(formula);
        resultado.setNombre(nombre);
        resultado.setAlternativo(alternativo);

        try {
            resultado.setMasa(masaFQ());
            resultado.setDensidad(densidadFQ());
            resultado.setFusion(temperaturaFQ("fusión"));
            resultado.setEbullicion(temperaturaFQ("ebullición"));

            escaneado_correcto = true; // Ha podido con todos
        }
        catch (Exception e) {
            // ...
        }

        return resultado;
    }

    private int indiceDespuesDeEn(String fragmento, String texto) {
        int indice = texto.indexOf(fragmento);
        if(indice != -1)
            indice += fragmento.length();

        return indice;
    }

    private String masaFQ() {
        String masa;

        int indice = indiceDespuesDeEn("Masa molar:", pagina);
        if(indice == -1)
            indice = indiceDespuesDeEn("Masa Molar:", pagina);
        if(indice == -1)
            indice = indiceDespuesDeEn("Peso Molecular:", pagina);
        if(indice != -1) { // Aparece la masa molecular
            masa = pagina.substring(indice + 1);
            masa = masa.substring(0, indiceDespuesDeEn("g", masa) - 1);

            masa = primeroDelIntervalo(masa);
            masa = quitarEspaciosYComas(masa);
            masa = formatearDosDecimales(masa);
        }
        else masa = null;

        return masa;
    }

    private String densidadFQ() {
        String densidad;

        int indice = indiceDespuesDeEn("Densidad:", pagina);
        if (indice != -1) { // Aparece la densidad
            String dato = pagina.substring(indice + 1);

            indice = indiceDespuesDeEn("g", dato);
            if (indice != -1) { // Aparece la unidad ("g", "Kg", "kg")
                dato = dato.substring(0, indiceDespuesDeEn("</", dato) - 2);
                indice -= 2;
                if (dato.charAt(indice) == 'k' || dato.charAt(indice) == 'K') {
                    dato = dato.substring(0, indice - 1);

                    float valor_densidad = Float.parseFloat(quitarEspaciosYComas(dato));
                    valor_densidad /= 1000; // De kg/m3 a g/cm3

                    NumberFormat formato = NumberFormat.getInstance();
                    formato.setGroupingUsed(false);
                    formato.setMaximumFractionDigits(6);
                    densidad = quitarEspaciosYComas(formato.format(valor_densidad));

                    // El número se adapta para tener 3 decimales significativos
                    // Ej.: "X.000ABCD" -> "X.000ABC"
                    indice = indiceDespuesDeEn(".", densidad);
                    if(indice != -1)
                        for(int i = indice, digitos = 0; i < densidad.length() && digitos < 3; i++)
                            if(densidad.charAt(i) != '0')
                                if(++digitos == 3)
                                    densidad = densidad.substring(0, i + 1);
                }
                else densidad = quitarEspaciosYComas(dato.substring(0, indice + 1));

                densidad = quitarCerosDecimalesALaDerecha(densidad);
            }
            else densidad = null;
        }
        else densidad = null;

        return densidad;
    }

    private String temperaturaFQ(String tipo) {
        String temperatura;

        int indice = indiceDespuesDeEn("Punto de " + tipo + ":", pagina);
        if(indice == -1)
            indice = indiceDespuesDeEn("Temperatura de " + tipo + ":", pagina);
        if(indice != -1) {
            StringBuilder dato = new StringBuilder(pagina.substring(indice + 1));
            dato = new StringBuilder(dato.substring(0,
                    indiceDespuesDeEn("</", dato.toString()) - 2));

            indice = indiceDespuesDeEn("°", dato.toString()); // Es distinto al siguiente
            if(indice != -1 || indiceDespuesDeEn("º", dato.toString()) != - 1) {
                dato = new StringBuilder(quitarEspaciosYComas(dato.toString()));
                dato = new StringBuilder(primeroDelIntervalo(dato.toString()));

                for(int i = 0; i < dato.length(); i++)
                    if(noEsNumero(dato.charAt(i)))
                        dato.deleteCharAt(i--);

                temperatura = formatearDosDecimales(dato.toString());
                temperatura = String.valueOf(273.15 + Float.parseFloat(temperatura)); // De ºC a K
                temperatura = formatearDosDecimales(temperatura); // Float produce más decimales
            }
            else temperatura = null;
        }
        else temperatura = null;

        return temperatura;
    }

    // Ej.: "12.104 - 13.2" -> "12.104"
    private String primeroDelIntervalo(String dato) {
        int indice = indiceDespuesDeEn("-", dato);
        if(indice > 1) // No es signo negativo
            dato = dato.substring(0, indice - 1);

        return dato;
    }

    // Ej.: " 12 ,104 " -> "12.104"
    private String quitarEspaciosYComas(String numero) {
        return numero.replaceAll(" ", "").replaceAll(",", ".");
    }

    // Ej.: "12.104" -> "12.1"
    private String formatearDosDecimales(String dato) {
        int indice = indiceDespuesDeEn(".", dato);
        if(indice != -1) {
            indice += 2;
            if(dato.length() > indice) // Hay más de dos decimales
                dato = dato.substring(0, indice);

            dato = quitarCerosDecimalesALaDerecha(dato);

            if(dato.charAt(dato.length() - 1) == '.') // Por si se queda el punto suelto
                dato = dato.substring(0, dato.length() - 1);
        }

        return dato;
    }

    // Ej.: "13.450" -> "13.45"
    // Ej.: "6.000" -> "6"
    private String quitarCerosDecimalesALaDerecha(String numero) {
        if(indiceDespuesDeEn(".", numero) != -1) {
            while (numero.charAt(numero.length() - 1) == '0')
                numero = numero.substring(0, numero.length() - 1);
        }

        return numero;
    }

    private static boolean noEsNumero(char c) {
        return (c < '0' || c > '9') && c != '-' && c != '.'; // Signo negativo y punto decimal
    }

}
