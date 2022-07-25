SELECT SUM(accesos),
SUM(teclado_encontrados),
SUM(teclado_no_encontrados),
SUM(teclado_sugerencias),
SUM(teclado_encontrados),
SUM(teclado_no_encontrados),
SUM(teclado_sugerencias),
SUM(teclado_sugerencias_ok),
SUM(teclado_premiums),
SUM(teclado_google),
SUM(teclado_bing_gratis),
SUM(teclado_bing_pago),
SUM(teclado_compleciones_ok)
FROM metricas;

SELECT SUM(accesos),
SUM(camara_encontrados),
SUM(camara_no_encontrados),
SUM(camara_sugerencias),
SUM(camara_encontrados),
SUM(camara_no_encontrados),
SUM(camara_sugerencias),
SUM(camara_sugerencias_ok),
SUM(camara_premiums),
SUM(camara_google),
SUM(camara_bing_gratis),
SUM(camara_bing_pago)
FROM metricas;

SELECT SUM(accesos),
SUM(galeria_encontrados), 
SUM(galeria_no_encontrados), 
SUM(galeria_sugerencias),
SUM(galeria_encontrados),
SUM(galeria_no_encontrados),
SUM(galeria_sugerencias),
SUM(galeria_sugerencias_ok),
SUM(galeria_premiums),
SUM(galeria_google),
SUM(galeria_bing_gratis),
SUM(galeria_bing_pago)
FROM metricas;