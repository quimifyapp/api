package com.quimify.api.organico.intermediarios.pubchem;



import com.quimify.api.descarga.Descarga;

import java.io.IOException;

public class PubChem {

	private static final String DIR = "https://pubchem.ncbi.nlm.nih.gov/";
	private static final String REST = DIR + "rest/pug/compound/";
	private static final String PNG_2D = DIR + "image/imagefly.cgi?width=500&height=500&cid=";

	private static final String SMILES = "smiles/";
	private static final String PNG = "/PNG";

	public static PubChemResultado procesarSmiles(String smiles) {
		PubChemResultado resultado = new PubChemResultado();

		smiles = Descarga.formatearHTTP(smiles);

		try {
			String cid = new Descarga(REST + SMILES + smiles + "/cids/TXT").getTexto();

			if(!cid.equals("0")) {
				resultado.setUrl_2d(PNG_2D + cid); // Este es de buena calidad (500 x 500 px) :)

				String base = REST + "cid/" + cid + "/property/";

				try {
					resultado.setMasa(new Descarga(base + "molecularweight/TXT").getTexto());
				}
				catch(IOException exception) {
					// Error...
				}

				try {
					resultado.setNombre_ingles(new Descarga(base + "iupacname/TXT").getTexto());
				}
				catch(IOException exception) {
					// Error...
				}
			}
		}
		catch(IOException exception) {
			// Error...
		}

		if(resultado.getUrl_2d() == null)
			resultado.setUrl_2d(REST + SMILES + smiles + PNG); // Este es de mala calidad (300 x 300) px :(

		return resultado;
	}

}
