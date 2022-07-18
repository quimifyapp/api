SELECT etiqueta.id, texto_normalizado, inorganico_id, busquedas, formula, nombre 
FROM etiqueta
INNER JOIN inorganico
ON etiqueta.inorganico_id = inorganico.id

# WHERE inorganico_id = 3
ORDER BY busquedas DESC