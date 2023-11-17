SET @id = 5721;

SELECT * FROM inorganic_search_tag WHERE inorganic_id = @id;
DELETE FROM inorganic_search_tag WHERE inorganic_id = @id;

SELECT * FROM inorganic WHERE id = @id;
DELETE FROM inorganic WHERE id = @id;
