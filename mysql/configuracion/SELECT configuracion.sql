SELECT version,
actualizacion_disponible AS "Actualización disponible",
actualizacion_obligatoria AS "Actualialización necesaria",
actualizacion_detalles AS "Actualialización detalles",
mensaje_presente AS "Mensaje presente",
mensaje_titulo AS "Mensaje titulo",
mensaje_detalles AS "Mensaje detalles",
mensaje_enlace_presente AS "Mensaje enlace presente",
mensaje_enlace_nombre AS "Mensaje enlace nombre",
mensaje_enlace AS "Mensaje enlace",
google_on AS "Google ON",
bing_gratis_on AS "Bing gratis ON",
bing_pago_on AS "Bing de pago ON",
google_limite AS "Google límite",
bing_pago_limite AS "Bing de pago límite",
google_url AS "Google URL",
bing_url AS "Bing URL",
bing_gratis_key AS "Bing gratis key",
bing_pago_key AS "Bing de pago key",
user_agent AS "User-Agent"
FROM configuracion