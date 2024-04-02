package com.quimify.api.organic;

import com.quimify.api.utils.Connection;
import com.quimify.api.error.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

// This Spring Bean makes calls to the PubChem API to resolve organic compound properties.

@Component
class PubChemComponent {

    @Autowired
    ErrorService errorService;

    // Constants:

    private static final String baseUrl = "https://pubchem.ncbi.nlm.nih.gov/";
    private static final String restUrl = baseUrl + "rest/pug/compound/";

    private static final String smilesToCompoundIdUrl = restUrl + "smiles/%s/cids/TXT";
    private static final String compoundIdToMolecularMassUrl = restUrl + "cid/%s/property/molecularweight/TXT";
    private static final String smilesTo2DUrl = restUrl + "smiles/%s/PNG";
    private static final String compoundIdTo2DUrl = baseUrl + "image/imagefly.cgi?width=500&height=500&cid=%s";
    private static final String compoundIdTo3DUrl = baseUrl + "compound/%s#section=3D-Conformer&fullscreen=true";

    // Queries:

    void setProperties(OrganicResult organicResult, String smiles) {
        String adaptedSmiles = adaptSmiles(smiles);
        String compoundId = getCompoundId(smiles, adaptedSmiles);

        Float molecularMass = getMolecularMass(compoundId);
        String url2D = getUrl2D(compoundId, adaptedSmiles);
        String url3D = getUrl3D(compoundId);

        organicResult.setMolecularMass(molecularMass);
        organicResult.setUrl2D(url2D);
        organicResult.setUrl3D(url3D);
    }

    // Private:

    private String getCompoundId(String smiles, String encodedSmiles) {
        try {
            return new Connection(String.format(smilesToCompoundIdUrl, encodedSmiles)).getText();
        } catch (IOException ioException) {
            errorService.log("Exception getting cId for: " + smiles, ioException.toString(), getClass());
            return null;
        }
    }

    private Float getMolecularMass(String compoundId) {
        if (isInvalid(compoundId))
            return null;

        try {
            String molecularMass = new Connection(String.format(compoundIdToMolecularMassUrl, compoundId)).getText();
            return Float.valueOf(molecularMass);
        } catch (Exception exception) {
            errorService.log("Exception getting mass for: " + compoundId, exception.toString(), getClass());
            return null;
        }
    }

    private String getUrl2D(String compoundId, String adaptedSmiles) {
        if (isInvalid(compoundId))
            return String.format(smilesTo2DUrl, adaptedSmiles); // 300 x 300 px

        return String.format(compoundIdTo2DUrl, compoundId); // 500 x 500 px
    }

    private String getUrl3D(String compoundId) {
        if (isInvalid(compoundId))
            return null;

        return String.format(compoundIdTo3DUrl, compoundId);
    }

    private String adaptSmiles(String smiles) {
        String pubChemAdapted = smiles.replaceAll("[/\\\\]", ""); // Isomeric (uses dashes) -> canonical
        return Connection.encode(pubChemAdapted); // For URLs
    }

    private boolean isInvalid(String compoundId) {
        return compoundId == null || compoundId.equals("0");
    }

}
