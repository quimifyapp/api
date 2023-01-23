package com.quimify.api.organic;

// This class makes calls to the PubChem API.

import com.quimify.utils.Download;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
class PubChemComponent {

    private static final String DIR = "https://pubchem.ncbi.nlm.nih.gov/";
    private static final String PNG_2D = DIR + "image/imagefly.cgi?width=500&height=500&cid=";
    private static final String REST = DIR + "rest/pug/compound/";

    public String getUrl2D(String smiles) throws IOException {
        String url2D;

        smiles = smiles.replaceAll("[/\\\\]", ""); // Isomeric (uses dashes) -> canonical
        smiles = Download.formatForUrl(smiles);

        String compoundId = new Download(REST + "smiles/" + smiles + "/cids/TXT").getText();

        if (!compoundId.equals("0"))
            url2D = PNG_2D + compoundId; // Good quality (500 x 500 px) :)
        else url2D = REST + "smiles/" + smiles + "/PNG"; // Bad quality (300 x 300) px :(

        return url2D;
    }

}
