SELECT dia AS "Día", 
accesos AS "Accesos",
teclado_encontrados + camara_encontrados + galeria_encontrados + teclado_no_encontrados + camara_no_encontrados + galeria_no_encontrados + teclado_sugerencias + camara_sugerencias + galeria_sugerencias AS "Peticiones",
teclado_encontrados + camara_encontrados + galeria_encontrados AS "Encontrados",
teclado_no_encontrados + camara_no_encontrados + galeria_no_encontrados AS "No encontrados",
teclado_sugerencias + camara_sugerencias + galeria_sugerencias AS "Sugerencias",
teclado_sugerencias_ok + camara_sugerencias_ok + galeria_sugerencias_ok AS "Sugerencias OK",
teclado_premiums + camara_premiums + galeria_premiums AS "Encontrados premium",
teclado_google + camara_google + galeria_google AS "Google",
teclado_bing_gratis + camara_bing_gratis + galeria_bing_gratis AS "Bing gratis",
teclado_bing_pago + camara_bing_pago + galeria_bing_pago AS "Bing de pago",
teclado_autocompleciones_ok AS "Autocompleciones OK"
FROM metricas;