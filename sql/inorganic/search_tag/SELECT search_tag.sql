SELECT inorganic_search_tag.id, normalized_text, inorganic_id, formula, search_count
FROM inorganic_search_tag
INNER JOIN inorganic
ON inorganic_search_tag.inorganic_id = inorganic.id

# WHERE normalized_text LIKE "%telururo%"
# WHERE inorganic_id = 1
ORDER BY id DESC