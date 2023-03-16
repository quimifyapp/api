SELECT normalized_tag, COUNT(normalized_tag)
FROM inorganic_search_tag 
WHERE normalized_tag NOT IN ("arsenico", "auno3", "cuclo2", "cuio2", "hgclo2", "co", "hf", "no", "cn", "ni", "cs")
GROUP BY normalized_tag
HAVING COUNT(normalized_tag) > 1
