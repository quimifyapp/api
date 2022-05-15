SELECT id, busquedas, formula, nombre, alternativo, premium, 
masa, densidad, fusion, ebullicion, etiquetas
FROM inorganico
# WHERE formula = "HCl"
# WHERE nombre = "metanol"
# WHERE nombre LIKE "m%"
ORDER BY busquedas DESC