SELECT (SUM(search_count)) / 1000 FROM inorganic;
SELECT * FROM inorganic
# WHERE name LIKE "%selenioso%" OR alternative_name LIKE "%selenioso%"
# WHERE formula REGEXP '.*C[0-9]*[\-]?H.*'
# WHERE id = 1002
# WHERE inorganico.id = 2995
# WHERE formula = "HClO"
# WHERE alternativo LIKE "ácido%"
# WHERE formula = "F2O"
# WHERE nombre = "copernicio"
# WHERE nombre LIKE "ácido piro%"
ORDER BY id DESC