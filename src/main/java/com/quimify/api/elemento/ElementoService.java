package com.quimify.api.elemento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Esta clase procesa los elementos químicos.

@Service
public class ElementoService {

    @Autowired
    private ElementoRepository elementoRepository; // Conexión con la DB

    // ADMIN --------------------------------------------------------------------------

    public Optional<ElementoModel> seleccionar(Integer id) {
        return elementoRepository.findById(id);
    }

    public Optional<ElementoModel> reemplazar(ElementoModel nuevo) {
        Optional<ElementoModel> reemplazado = elementoRepository.findById(nuevo.getId());

        if(reemplazado.isPresent()) // Si existe
            elementoRepository.save(nuevo); // De la DB

        return reemplazado;
    }

    public ElementoModel insertar(ElementoModel nuevo) {
        return elementoRepository.save(nuevo); // En la DB
    }

    public Optional<ElementoModel> eliminar(Integer id) {
        Optional<ElementoModel> eliminado = elementoRepository.findById(id);

        if(eliminado.isPresent())
            elementoRepository.deleteById(id); // De la DB

        return eliminado;
    }

    // INTERNOS ----------------------------------------------------------------------

    public Float masaMolecular(String formula) {
        Float resultado;

        Optional<ElementoModel> buscado = elementoRepository.findBySimbolo(formula);

        if(buscado.isPresent())
            resultado = buscado.get().getMasa();
        else resultado = null;

        return resultado;
        /*
class calc_masmol {
    float masmol;

    final String symb[] = { "He", "Fr", "Ar", "Ne", "Rn", "Cm", "Mc", "Xe", "Ac", "Ag", "Al", "Am", "As", "At", "Au",
            "B", "Ba", "Be", "Bi", "Bk", "Br", "C", "Ca", "Cd", "Ce", "Cf", "Cl", "Co", "Cr", "Cs", "Cu", "Dy", "Er",
            "Es", "Eu", "F", "Fe", "Ga", "Gd", "Ge", "H", "Hf", "Hg", "Ho", "I", "In", "Ir", "K", "La", "Li", "Lu",
            "Lr", "Md", "Mg", "Mn", "Mo", "N", "Na", "Nb", "Nd", "Ni", "No", "Np", "Os", "P", "Pa", "Pb", "Pd", "Pm",
            "Po", "Pr", "Pt", "Pu", "Ra", "Rb", "Re", "Rh", "Ru", "S", "Sb", "Sc", "Se", "Si", "Sm", "Sn", "Sr", "Ta",
            "Tb", "Tc", "Te", "Th", "Ti", "Tl", "Tm", "U", "V", "W", "Y", "Yb", "Zn", "Zr", "O" };

    final float ma[] = { 4.002602f, 223f, 39.948f, 20.1797f, 222f, 247f, 288f, 131.293f, 227.0278f, 107.8682f, 26.98f,
            243.0614f, 74.9216f, 209.987f, 196.966f, 10.811f, 137.327f, 9.012f, 208.980f, 247.07f, 79.904f, 12.011f,
            40.078f, 112.411f, 140.115f, 251.0796f, 35.4527f, 58.933f, 51.996f, 132.905f, 63.546f, 162.50f, 167.26f,
            252.083f, 151.965f, 18.998f, 55.847f, 69.723f, 157.25f, 72.61f, 1.00794f, 178.49f, 200.59f, 164.930f,
            126.905f, 114.82f, 192.22f, 39.0983f, 138.906f, 6.941f, 174.967f, 260.1053f, 258.099f, 24.305f, 54.938f,
            95.94f, 14.007f, 22.90f, 92.906f, 144.24f, 58.69f, 259.1009f, 237.048f, 190.2f, 30.974f, 231.036f, 207.2f,
            106.42f, 146.915f, 208.9824f, 140.908f, 195.08f, 244.064f, 226.03f, 85.47f, 186.207f, 102.91f, 101.07f,
            32.066f, 121.75f, 44.96f, 78.96f, 28.09f, 150.36f, 118.71f, 87.62f, 180.95f, 158.93f, 98.91f, 127.6f,
            232.04f, 47.88f, 204.38f, 168.93f, 238.029f, 50.94f, 183.85f, 88.91f, 173.04f, 65.39f, 91.224f, 15.994f };

    calc_masmol(int ed, String[] s, float coeff[]) {

        float massat[] = new float[ed + 1];

        for(int a = 0; a <= ed; a++)
            for (int i = 0; i <= symb.length - 1; i++)
                if (s[a].equals(symb[i])) {
                    massat[a] = ma[i];
                    break;
                }

        for (int a = 0; a <= ed; a++)
            if (massat[a] > 0) masmol = masmol + massat[a] * coeff[a];
            else {
                masmol = 0;
                break;
            }
    }

    float mt() {
        return masmol;
    }

}

class analysis {

    String chemForm;

    float molmas = 0f;

    analysis(String cForm) {

        chemForm = cForm; //Chemical formula

        String s[] = new String[20]; // Symbols of the elements in the chemical formula

        float massat = 0;

        float coeff[] = new float[20]; // Coefficients ——————————–

        int len = cForm.length(); //Number of characters in the formula

        char c;

        String ch, coefficient;

        int a = 0, i = 0, end = 0;

        cForm = cForm + " ";

        do {
            ch = "";
            coefficient = "1";
            coeff[a] = 0;

            c = cForm.charAt(i);

            if (Character.isUpperCase(c)) {
                ch = String.valueOf(c);
                s[a] = ch;
                i++;
            }

            c = cForm.charAt(i);

            if(Character.isLowerCase(c)) {
                ch = String.valueOf(c);
                s[a] = s[a] + ch; // The symbol of the element is obtained
                i++;
            }

            c = cForm.charAt(i);

            if(Character.isDigit(c)) {
                coefficient = String.valueOf(c);
                i++;
            }

            c = cForm.charAt(i);

            if (Character.isDigit(c)) {
                coefficient = coefficient + String.valueOf(c);
                i++;
            }

            c = cForm.charAt(i);

            if (c == '.') {
                coefficient = coefficient + ".";
                i++;
            }

            c = cForm.charAt(i);

            if (Character.isDigit(c)) {
                coefficient = coefficient + String.valueOf(c);
                i++;
            }

            c = cForm.charAt(i);

            if (Character.isDigit(c)) {
                coefficient = coefficient + String.valueOf(c);
                i++;
            }

            c = cForm.charAt(i);

            // The next character could be a comma

            if (c == ',')
                i++;

            coeff[a] = Float.valueOf(coefficient).floatValue();

            if (coeff[a] == 0)
                coeff[a] = 1;

            a++;

        } while (i <= len - 1);

        end = a - 1;
        calc_masmol ms = new calc_masmol(end, s, coeff);
        molmas = ms.mt();
    }

    float result() {
        return molmas;
    }
}
*/
    }

}
