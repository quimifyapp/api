package com.quimify.api.organic;

import com.quimify.api.download.Download;
import com.quimify.api.error.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

// This class makes calls to the PubChem API.

@Component
class PubChemComponent {

    @Autowired
    ErrorService errorService; // API errors logic

    private String compoundId; // Compound's ID in Pub Chem DB
    private String smiles; // Simplified Molecular Input Line Entry Specification

    // Constants:

    private static final String url = "https://pubchem.ncbi.nlm.nih.gov/";
    private static final String restUrl = url + "rest/pug/compound/";

    private static final String compoundIdUrl = restUrl + "smiles/%s/cids/TXT";
    private static final String smiles2DUrl = restUrl + "smiles/%s/PNG";
    private static final String compoundId2DUrl = url + "image/imagefly.cgi?width=500&height=500&cid=%s";
    private static final String molecularMassUrl = restUrl + "cid/%s/property/molecularweight/TXT";

    // Queries:

    protected void resolveCompound(String smiles) {
        smiles = formatSmilesForUrl(smiles);
        this.smiles = smiles;

        try {
            compoundId = new Download(String.format(compoundIdUrl, smiles)).getText();
        } catch (IOException ioException) {
            errorService.log("Exception getting cId for: " + smiles, ioException.toString(), this.getClass());
            compoundId = null;
        }
    }

    protected String getUrl2D() {
        if (invalidCompoundId())
            return String.format(smiles2DUrl, smiles); // 300 x 300 px

        return String.format(compoundId2DUrl, compoundId); // 500 x 500 px
    }

    protected Optional<Float> getMolecularMass() {
        if(invalidCompoundId())
            return Optional.empty();

        Optional<Float> molecularMass;

        try {
            String text = new Download(String.format(molecularMassUrl, compoundId)).getText();
            molecularMass = Optional.of(Float.valueOf(text));
        } catch (Exception exception) {
            errorService.log("Exception getting mass for: " + smiles, exception.toString(), this.getClass());
            molecularMass = Optional.empty();
        }

        return molecularMass;
    }

    // Private:

    private String formatSmilesForUrl(String smiles) {
        smiles = smiles.replaceAll("[/\\\\]", ""); // Isomeric (uses dashes) -> canonical
        return Download.formatForUrl(smiles);
    }

    private boolean invalidCompoundId() {
        return compoundId == null || compoundId.equals("0");
    }

}
