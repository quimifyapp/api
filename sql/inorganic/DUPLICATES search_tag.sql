SELECT normalized_text, COUNT(normalized_text)
FROM search_tag
GROUP BY normalized_text
HAVING COUNT(normalized_text) > 1