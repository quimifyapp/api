SELECT formula, COUNT(formula) # Hay 5 aceptables
FROM inorganic
GROUP BY formula
HAVING COUNT(formula) > 1;

SELECT stock_name, COUNT(stock_name) # Hay 0 aceptables
FROM inorganic
GROUP BY stock_name
HAVING COUNT(stock_name) > 1;

SELECT systematic_name, COUNT(systematic_name) # Hay 0 aceptables
FROM inorganic
GROUP BY systematic_name
HAVING COUNT(systematic_name) > 1;

SELECT traditional_name, COUNT(traditional_name) # Hay 0 aceptables
FROM inorganic
GROUP BY traditional_name
HAVING COUNT(traditional_name) > 1;

SELECT common_name, COUNT(common_name) # Hay 0 aceptables
FROM inorganic
GROUP BY common_name
HAVING COUNT(common_name) > 1;