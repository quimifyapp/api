SELECT normalized_text, inorganic_id, formula, stock_name, systematic_name, traditional_name, common_name, searches FROM inorganic_search_tag
INNER JOIN inorganic ON inorganic_id = inorganic.id

# WHERE normalized_text LIKE "%telururo%"
# WHERE inorganic_id = 1
ORDER BY id DESC;
