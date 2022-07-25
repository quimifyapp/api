SELECT dia, accesos,
teclado_encontrados, teclado_no_encontrados, teclado_sugerencias, teclado_sugerencias_ok, teclado_premiums,
teclado_google, teclado_bing_gratis, teclado_bing_pago, teclado_compleciones_ok
FROM metricas ORDER BY dia DESC;

SELECT dia, accesos,
camara_encontrados, camara_no_encontrados, camara_sugerencias, camara_sugerencias_ok, camara_premiums,
camara_google, camara_bing_gratis, camara_bing_pago
FROM metricas ORDER BY dia DESC;

SELECT dia, accesos,
galeria_encontrados, galeria_no_encontrados, galeria_sugerencias, galeria_sugerencias_ok, galeria_premiums,
galeria_google, galeria_bing_gratis, galeria_bing_pago
FROM metricas ORDER BY dia DESC;