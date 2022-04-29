SELECT id, busquedas, formula, nombre, alternativo, premium, 
masa, densidad, fusion, ebullicion, etiquetas
FROM inorganico
# WHERE formula = "CH4"
order by busquedas desc