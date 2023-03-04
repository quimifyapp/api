SELECT query, location, COUNT(*) AS frequency FROM not_found_query
GROUP BY query, location
ORDER BY frequency DESC;
