package com.quimify.api.organic;

import com.quimify.api.organic.bridges.opsin.Opsin;
import com.quimify.api.organic.bridges.opsin.OpsinResult;
import com.quimify.api.organic.bridges.pubchem.PubChem;
import com.quimify.api.organic.bridges.pubchem.PubChemResult;
import com.quimify.api.organic.components.FunctionalGroup;
import com.quimify.api.organic.components.Substituent;
import com.quimify.api.organic.compounds.Molecule;
import com.quimify.api.organic.compounds.open_chain.Ether;
import com.quimify.api.organic.compounds.open_chain.OpenChain;
import com.quimify.api.organic.compounds.open_chain.Simple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class OrganicFactory {

    private static final Logger logger = LoggerFactory.getLogger(OrganicFactory.class);

    public static final OrganicResult organicNotFound = new OrganicResult(false); // Constante auxiliar
    public static final int carbonInputCode = -1;

    // PUBLIC ------------------------------------------------------------------------

    // TODO: LAS MAPSAS

    public static OrganicResult getFromName(String name) {
        OrganicResult organicResult;

        Optional<OpsinResult> opsinResult = Opsin.procesarNombreES(name);
        if(opsinResult.isPresent()) {
            organicResult = new OrganicResult(true);

            organicResult.setNombre(name); // El input del usuario (puede estar mal escrito)

            complementViaPubChem(organicResult, opsinResult.get().getSmiles()); // Características

            // Structure:
            try {
                Molecule molecule = new Molecule(opsinResult.get().getCml(), opsinResult.get().getSmiles());

                Optional<String> formula = molecule.getStructure();
                if(formula.isPresent())
                    organicResult.setFormula(formula.get());
                else logger.warn("No se pudo generar la fórmula para \"" + name + "\".");
            }
            catch(IllegalArgumentException exception) {
                logger.warn("Excepción al generar la fórmula de \"" + name + "\": " + exception); // It happens often
            }
            catch (Exception exception) {
                logger.error("Excepción al generar la fórmula de \"" + name + "\": " + exception);
            }
        }
        else organicResult = organicNotFound;

        return organicResult;
    }

    // TODO: handle exceptions
    public static OrganicResult getFromStructure(int[] inputSequence) {
        OrganicResult organicResult = new OrganicResult(true);

        OpenChain openChain = getOpenChainFromSequence(inputSequence);

        openChain.correctSubstituents(); // It´s necessary

        // Name:
        String name = openChain.getName();
        organicResult.setNombre(name);

        // Properties:
        Optional<OpsinResult> opsinResult = Opsin.procesarNombreES(name);
        opsinResult.ifPresent(result -> complementViaPubChem(organicResult, result.getSmiles()));

        // Structure:
        organicResult.setFormula(openChain.getStructure());

        return organicResult;
    }

    // PRIVATE -----------------------------------------------------------------------

    private static OpenChain getOpenChainFromSequence(int[] inputSequence) {
        OpenChain openChain = new Simple();

        for(int i = 0; i < inputSequence.length; i++) {
            if(inputSequence[i] != carbonInputCode) {
                FunctionalGroup groupElection = openChain.getOrderedBondableGroups().get(inputSequence[i]);

                if (groupElection != FunctionalGroup.radical) {
                    if (groupElection != FunctionalGroup.ether)
                        openChain.bond(groupElection);
                    else { // Ether
                        assert openChain instanceof Simple; // Yes, it is...
                        openChain = new Ether((Simple) openChain);
                    }
                }
                else { // Radical
                    boolean isIso = inputSequence[++i] == 1;
                    int carbonCount = inputSequence[++i];
                    openChain.bond(new Substituent(carbonCount, isIso));
                }
            }
            else openChain.bondCarbon();
        }

        return openChain;
    }

    private static void complementViaPubChem(OrganicResult organicResult, String smiles) {
        PubChemResult pubChemResult = new PubChem(smiles).procesar();

        if(pubChemResult.getMasa() != null)
            organicResult.setMasa(Float.valueOf(pubChemResult.getMasa()));

        organicResult.setUrl_2d(pubChemResult.getUrl_2d());
    }

}
