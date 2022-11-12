SELECT formula, COUNT(formula) # Hay 41 aceptables
FROM inorganic
GROUP BY formula
HAVING COUNT(formula) > 1;

SELECT name, COUNT(name) # Hay 0 aceptables
FROM inorganic
GROUP BY name
HAVING COUNT(name) > 1;

SELECT alternative_name, COUNT( alternative_name) # Hay 0 aceptables
FROM inorganic
GROUP BY alternative_name
HAVING COUNT( alternative_name) > 1;