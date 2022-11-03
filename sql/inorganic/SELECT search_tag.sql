SELECT search_tag.id, normalized_text, inorganic_id, search_count, formula, name
FROM search_tag
INNER JOIN inorganic
ON search_tag.inorganic_id = inorganic.id

# WHERE normalized_text = "acidohiposelenioso"
# WHERE inorganic_id = 283
 ORDER BY id DESC