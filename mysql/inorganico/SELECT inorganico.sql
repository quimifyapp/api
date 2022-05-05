SELECT id, busquedas, formula, nombre, alternativo, premium, 
masa, densidad, fusion, ebullicion, etiquetas
FROM inorganico
# WHERE formula = "HCl"
 WHERE formula = "Rb2TeO2"
order by busquedas desc