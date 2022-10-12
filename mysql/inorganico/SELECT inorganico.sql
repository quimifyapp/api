SELECT (SUM(busquedas)) / 1000 FROM inorganico;
SELECT id, busquedas, formula, nombre, alternativo, premium, masa, densidad, fusion, ebullicion
FROM inorganico
WHERE premium = 1
# WHERE formula = "H2O2"
# WHERE alternativo LIKE "ácido%"
# WHERE formula = "HCl"
# WHERE nombre = "metanol"
# WHERE nombre LIKE "m%"
ORDER BY id DESC