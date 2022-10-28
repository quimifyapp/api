SELECT search_tag.id, normalized_text, inorganic_id, search_count, formula, name
FROM search_tag
INNER JOIN inorganic
ON search_tag.inorganic_id = inorganic.id

# WHERE texto_normalizado LIKE "%selen%"
# WHERE inorganico_id = 1
 ORDER BY id DESC