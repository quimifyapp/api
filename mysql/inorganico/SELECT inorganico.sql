SELECT (SUM(busquedas)) / 1000 FROM inorganico;
SELECT id, busquedas, formula, nombre, alternativo, premium, masa, densidad, fusion, ebullicion
FROM inorganico 

# WHERE id = 1002
# WHERE inorganico.id = 2995
# WHERE formula = "Es2O3"
# WHERE alternativo LIKE "ácido%"
# WHERE formula = "HCl"
# WHERE nombre = "metanol"
# WHERE nombre LIKE "m%"
ORDER BY id DESC