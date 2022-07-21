SELECT SUM(accesos),
SUM(teclado_encontrados), SUM(camara_encontrados), SUM(galeria_encontrados), 
SUM(teclado_no_encontrados), SUM(camara_no_encontrados), SUM(galeria_no_encontrados), 
SUM(teclado_sugerencias), SUM(camara_sugerencias), SUM(galeria_sugerencias),
SUM(teclado_encontrados), SUM(camara_encontrados), SUM(galeria_encontrados),
SUM(teclado_no_encontrados), SUM(camara_no_encontrados), SUM(galeria_no_encontrados),
SUM(teclado_sugerencias), SUM(camara_sugerencias), SUM(galeria_sugerencias),
SUM(teclado_sugerencias_ok), SUM(camara_sugerencias_ok), SUM(galeria_sugerencias_ok),
SUM(teclado_premiums), SUM(camara_premiums), SUM(galeria_premiums),
SUM(teclado_google), SUM(camara_google), SUM(galeria_google),
SUM(teclado_bing_gratis), SUM(camara_bing_gratis), SUM(galeria_bing_gratis),
SUM(teclado_bing_pago), SUM(camara_bing_pago), SUM(galeria_bing_pago),
SUM(teclado_autocompleciones_ok)
FROM metricas;