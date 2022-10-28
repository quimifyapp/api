SELECT texto_normalizado, COUNT(texto_normalizado)
FROM etiqueta
GROUP BY texto_normalizado
HAVING COUNT(texto_normalizado) > 1