SELECT nombre, formula, alternativo, COUNT(formula), COUNT(nombre), COUNT(alternativo)
FROM inorganico
GROUP BY nombre
HAVING COUNT(nombre) > 1 or COUNT(formula) > 1 or COUNT(alternativo) > 1