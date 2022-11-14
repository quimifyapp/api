SELECT inorganic_search_tag.id, normalized_tag, inorganic_id, formula, search_count
FROM inorganic_search_tag
INNER JOIN inorganic
ON inorganic_search_tag.inorganic_id = inorganic.id

# WHERE normalized_tag = "pentano"
# WHERE inorganic_id = 283
ORDER BY id DESC