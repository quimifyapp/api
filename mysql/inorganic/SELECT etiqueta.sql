SELECT etiqueta.id, texto_normalizado, inorganico_id, busquedas, formula, nombre 
FROM etiqueta
INNER JOIN inorganico
ON etiqueta.inorganico_id = inorganico.id

# WHERE texto_normalizado LIKE "%selen%"
# WHERE inorganico_id = 1
 ORDER BY id DESC