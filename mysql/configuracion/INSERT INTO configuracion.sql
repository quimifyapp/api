INSERT INTO configuracion(version,
google_on, google_limite,
bing_gratis_on,
bing_pago_on,  bing_pago_limite,
google_url,
bing_url,
bing_gratis_key, bing_pago_key,
user_agent)
VALUES(1, # Versión
true, 100, # Google
true, # Bing gratis
false, 0, # Bing de pago
"https://www.googleapis.com/customsearch/v1/siterestrict?key=AIzaSyAC0vTdsgSmyp5VP_9ZtUNumBDjxDgBosM&cx=f475470fccb0e037e&num=1&q=", 
"https://api.bing.microsoft.com/v7.0/search?count=1&q=site:www.formulacionquimica.com+", 
"3aba1c345f31492cb5bc8dfaf9f47097", "612b8d0be02d47b1b5389aa65a9165f6",
"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.147 Safari/537.36")