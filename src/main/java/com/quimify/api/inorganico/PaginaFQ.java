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
            masa = masa.substring(0, indiceDespuesDeEn("</", masa) - 2);

            indice = indiceDespuesDeEn("g", masa);
            if(indice != -1) { // Aparece la unidad
                masa = masa.substring(0, indice - 1);

                masa = masa.replaceAll(" ", "")
                        .replaceAll(",", ".");
                masa = soloNumeros(masa);
                masa = primeroDelIntervalo(masa);
                masa = quitarCerosDecimalesALaDerecha(masa);
            }
            else masa = null;
        }
        else masa = null;

        return masa;
    }

    private String densidadFQ() {
        String densidad;

        int indice = indiceDespuesDeEn("Densidad:", pagina);
        if (indice != -1) { // Aparece la densidad
            densidad = pagina.substring(indice + 1);
            densidad = densidad.substring(0, indiceDespuesDeEn("</", densidad) - 2);

            indice = indiceDespuesDeEn("g", densidad);
            if (indice != -1) { // Aparece la unidad ("g", "Kg", "kg")
                boolean en_kilogramos;
                if (densidad.charAt(indice - 2) == 'k' || densidad.charAt(indice - 2) == 'K') {
                    densidad = densidad.substring(0, indice - 2);
                    en_kilogramos = true;
                }
                else {
                    densidad = densidad.substring(0, indice - 1);
                    en_kilogramos = false;
                }

                densidad = densidad.replaceAll(" ", "")
                        .replaceAll(",", ".");
                densidad = soloNumeros(densidad);
                densidad = primeroDelIntervalo(densidad);

                if(en_kilogramos) {
                    float valor_densidad = Float.parseFloat(densidad);
                    valor_densidad /= 1000; // De kg/m3 a g/cm3

                    NumberFormat formato = NumberFormat.getInstance();
                    formato.setGroupingUsed(false);
                    formato.setMaximumFractionDigits(6);
                    densidad = formato.format(valor_densidad).replaceAll(",", ".");

                    densidad = tresDecimalesSignificativos(densidad);
                }

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
            temperatura = pagina.substring(indice + 1);
            temperatura = temperatura.substring(0,
                    indiceDespuesDeEn("</", temperatura) - 2);

            indice = indiceDespuesDeEn("°", temperatura);
            if(indice == -1)
                indice = indiceDespuesDeEn("º", temperatura); // Es distinto al anterior
            if(indice != -1) { // Aparece el símbolo de grado
                temperatura = temperatura.substring(0, indice - 1);

                temperatura = temperatura.replaceAll(" ", "")
                        .replaceAll(",", ".");
                temperatura = soloNumeros(temperatura);
                temperatura = primeroDelIntervalo(temperatura);
                temperatura = String.valueOf(273.15 + Float.parseFloat(temperatura));

                temperatura = truncarDosDecimales(temperatura);
                temperatura = quitarCerosDecimalesALaDerecha(temperatura);
            }
            else temperatura = null;
        }
        else temperatura = null;

        return temperatura;
    }

    // Ej.: "12.104 - 13.2" -> "12.104"
    private String primeroDelIntervalo(String dato) {
        String resultado = dato;

        if(dato.charAt(0) == '-' && dato.length() > 1)
            dato = dato.substring(1); // Para el signo negativo

        int indice = indiceDespuesDeEn("-", dato);
        if(indice != -1)
            resultado = resultado.substring(0, indice);

        return resultado;
    }

    // Ej.: "-1,104 ºC - 0.34 " -> "-1,104-0.34"
    private String soloNumeros(String dato) {
        StringBuilder resultado = new StringBuilder();

        for(int i = 0; i < dato.length(); i++)
            if(esNumero(dato.charAt(i)))
                resultado.append(dato.charAt(i));

        return resultado.toString();
    }

    // Ej.: "13.450" -> "13.45"
    // Ej.: "6.000" -> "6"
    private String quitarCerosDecimalesALaDerecha(String numero) {
        if(indiceDespuesDeEn(".", numero) != -1)
            while (numero.charAt(numero.length() - 1) == '0')
                numero = numero.substring(0, numero.length() - 1);

        if(numero.charAt(numero.length() - 1) == '.') // Por si se queda el punto suelto
            numero = numero.substring(0, numero.length() - 1);

        return numero;
    }

    private static boolean esNumero(char c) {
        return (c >= '0' && c <= '9') || c == '-' || c == '.'; // Signo negativo y punto decimal
    }

    // Ej.: "14.457 -> 14.45"
    private String truncarDosDecimales(String numero) {
        int indice = indiceDespuesDeEn(".", numero);
        if(indice != -1) {
            indice += 2;
            if(numero.length() > indice) // Hay más de dos decimales
                numero = numero.substring(0, indice);
        }

        return numero;
    }

    // Ej.: "X.000ABCD" -> "X.000ABC"
    private String tresDecimalesSignificativos(String numero) {
        int punto = indiceDespuesDeEn(".", numero);
        if(punto != -1)
            for(int i = punto, digitos = 0; i < numero.length() && digitos < 3; i++)
                if(numero.charAt(i) != '0')
                    if(++digitos == 3)
                        numero = numero.substring(0, i + 1);

        return numero;
    }

}