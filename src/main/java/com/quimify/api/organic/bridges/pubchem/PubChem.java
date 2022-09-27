package com.quimify.api.organic.bridges.pubchem;

import com.quimify.api.descarga.Descarga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PubChem {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	String smiles;

	private static final String DIR = "https://pubchem.ncbi.nlm.nih.gov/";
	private static final String REST = DIR + "rest/pug/compound/";
	private static final String PNG_2D = DIR + "image/imagefly.cgi?width=500&height=500&cid=";

	private static final String SMILES = "smiles/";
	private static final String PNG = "/PNG";

	// --------------------------------------------------------------------------------

	// Constructor:

	public PubChem(String smiles) {
		this.smiles = smiles;
	}

	public PubChemResult procesar() {
		PubChemResult resultado = new PubChemResult();

		smiles = Descarga.formatearHTTP(smiles);

		String url = REST + SMILES + smiles + "/cids/TXT";
		try {
			String cid = new Descarga(url).getTexto();

			if(!cid.equals("0")) {
				resultado.setUrl_2d(PNG_2D + cid); // Este es de buena calidad (500 x 500 px) :)

				String base = REST + "cid/" + cid + "/property/";
				try {
					resultado.setMasa(new Descarga(base + "molecularweight/TXT").getTexto());
				}
				catch(IOException exception) {
					logger.error("Excepción al descargar \"" + base + "molecularweight/TXT" + "\": " + exception);
				}

				try {
					resultado.setNombre_ingles(new Descarga(base + "iupacname/TXT").getTexto());
				}
				catch(IOException exception) {
					logger.error("Excepción al descargar \"" + base + "iupacname/TXT" + "\": " + exception);
				}
			}
		}
		catch(IOException exception) {
			logger.error("Excepción al descargar \"" + url + "\": " + exception);
		}

		if(resultado.getUrl_2d() == null)
			resultado.setUrl_2d(REST + SMILES + smiles + PNG); // Este es de mala calidad (300 x 300) px :(

		return resultado;
	}

}
