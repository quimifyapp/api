SET SQL_SAFE_UPDATES = 0;

UPDATE inorganico SET
nombre = REPLACE(nombre, " (", "("), 
alternativo = REPLACE(alternativo, " (", "(");

SET SQL_SAFE_UPDATES = 1;