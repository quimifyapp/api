SELECT search_tag.id, normalized_text, inorganic_id, formula, name, search_count
FROM search_tag
INNER JOIN inorganic
ON search_tag.inorganic_id = inorganic.id

# WHERE normalized_text = "acidohiposelenioso"
# WHERE inorganic_id = 283
 ORDER BY id DESC