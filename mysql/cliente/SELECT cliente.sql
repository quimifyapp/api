SELECT version,
actualizacion_disponible AS "Actualización disponible",
actualizacion_obligatoria AS "Actualialización necesaria",
actualizacion_detalles AS "Actualialización detalles",
mensaje_presente AS "Mensaje presente",
mensaje_titulo AS "Mensaje titulo",
mensaje_detalles AS "Mensaje detalles",
mensaje_enlace_presente AS "Mensaje enlace presente",
mensaje_enlace_nombre AS "Mensaje enlace nombre",
mensaje_enlace AS "Mensaje enlace"
FROM cliente 
ORDER BY version DESC