SELECT SUM(accesos) AS "Accesos",
SUM(teclado_encontrados + camara_encontrados + galeria_encontrados + teclado_no_encontrados + camara_no_encontrados + galeria_no_encontrados + teclado_sugerencias + camara_sugerencias + galeria_sugerencias) AS "Peticiones",
SUM(teclado_encontrados + camara_encontrados + galeria_encontrados) AS "Encontrados",
SUM(teclado_no_encontrados + camara_no_encontrados + galeria_no_encontrados) AS "No encontrados",
SUM(teclado_sugerencias + camara_sugerencias + galeria_sugerencias) AS "Sugerencias",
SUM(teclado_sugerencias_ok + camara_sugerencias_ok + galeria_sugerencias_ok) AS "Sugerencias OK",
SUM(teclado_premiums + camara_premiums + galeria_premiums) AS "Encontrados premium",
SUM(teclado_google + camara_google + galeria_google) AS "Google",
SUM(teclado_bing_gratis + camara_bing_gratis + galeria_bing_gratis) AS "Bing gratis",
SUM(teclado_bing_pago + camara_bing_pago + galeria_bing_pago) AS "Bing de pago",
SUM(teclado_autocompleciones_ok) AS "Autocompleciones OK"
FROM metricas;