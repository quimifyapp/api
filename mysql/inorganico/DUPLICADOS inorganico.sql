SELECT formula, COUNT(formula)
FROM inorganico
GROUP BY formula
HAVING COUNT(formula) > 1;

SELECT nombre, COUNT(nombre)
FROM inorganico
GROUP BY nombre
HAVING COUNT(nombre) > 1;

SELECT alternativo, COUNT(alternativo)
FROM inorganico
GROUP BY alternativo
HAVING COUNT(alternativo) > 1;