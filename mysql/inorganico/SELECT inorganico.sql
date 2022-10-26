SELECT (SUM(busquedas)) / 1000 FROM inorganico;
SELECT id, busquedas, formula, nombre, alternativo, premium, masa, densidad, fusion, ebullicion
FROM inorganico 

# WHERE formula REGEXP '.*C[0-9]*[\-]?H.*'
# WHERE id = 1002
# WHERE inorganico.id = 2995
# WHERE formula = "Es2O3"
# WHERE alternativo LIKE "ácido%"
# WHERE formula = "N2O5"
# WHERE nombre = "copernicio"
# WHERE nombre LIKE "ácido piro%"
ORDER BY id DESC