package com.quimify.api.organic;

import com.quimify.api.utils.Connection;
import com.quimify.api.error.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

// This class makes calls to the PubChem API.

@Component
@Scope("prototype") // New instance for each request, avoiding shared state
class PubChemComponent {

    @Autowired
    ErrorService errorService; // API errors logic

    private String compoundId; // Compound's ID in Pub Chem DB
    private String encodedSmiles; // Simplified Molecular Input Line Entry Specification

    // Constants:

    private static final String url = "https://pubchem.ncbi.nlm.nih.gov/";
    private static final String restUrl = url + "rest/pug/compound/";

    private static final String compoundIdUrl = restUrl + "smiles/%s/cids/TXT";
    private static final String smiles2DUrl = restUrl + "smiles/%s/PNG";
    private static final String compoundId2DUrl = url + "image/imagefly.cgi?width=500&height=500&cid=%s";
    private static final String molecularMassUrl = restUrl + "cid/%s/property/molecularweight/TXT";

    // Queries:

    protected void resolveCompound(String smiles) {
        // Adapted for URLs and PubChem:
        encodedSmiles = smiles.replaceAll("[/\\\\]", ""); // Isomeric (uses dashes) -> canonical
        encodedSmiles = Connection.encodeForUrl(encodedSmiles); // Escapes special characters

        try {
            compoundId = new Connection(String.format(compoundIdUrl, encodedSmiles)).getText();
        } catch (IOException ioException) {
            errorService.log("Exception getting cId for: " + smiles, ioException.toString(), getClass());
            compoundId = null;
        }
    }

    protected String getUrl2D() {
        if (invalidCompoundId())
            return String.format(smiles2DUrl, encodedSmiles); // 300 x 300 px

        return String.format(compoundId2DUrl, compoundId); // 500 x 500 px
    }

    protected Optional<Float> getMolecularMass() {
        if(invalidCompoundId())
            return Optional.empty();

        Optional<Float> molecularMass;

        try {
            String text = new Connection(String.format(molecularMassUrl, compoundId)).getText();
            molecularMass = Optional.of(Float.valueOf(text));
        } catch (Exception exception) {
            errorService.log("Exception getting mass for: " + compoundId, exception.toString(), getClass());
            molecularMass = Optional.empty();
        }

        return molecularMass;
    }

    // Private:

    private boolean invalidCompoundId() {
        return compoundId == null || compoundId.equals("0");
    }

}
