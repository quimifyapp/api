SELECT formula, COUNT(formula) # Hay 53 aceptables
FROM inorganico
GROUP BY formula
HAVING COUNT(formula) > 1;

SELECT nombre, COUNT(nombre) # Hay 0 aceptables
FROM inorganico
GROUP BY nombre
HAVING COUNT(nombre) > 1;

SELECT alternativo, COUNT(alternativo) # Hay 0 aceptables
FROM inorganico
GROUP BY alternativo
HAVING COUNT(alternativo) > 1;