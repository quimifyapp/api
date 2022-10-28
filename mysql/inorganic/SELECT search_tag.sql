SELECT search_tag.id, normalized_text, inorganic_id, search_count, formula, name
FROM search_tag
INNER JOIN inorganic
ON search_tag.inorganic_id = inorganic.id

# WHERE texto_normalizado LIKE "%selen%"
# WHERE inorganic_id = 65
 ORDER BY id DESC