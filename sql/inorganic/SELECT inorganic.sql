SELECT * FROM inorganic 
# WHERE formula LIKE BINARY "%(O2)" AND systematic_name IS NULL

# WHERE systematic_name LIKE "%teleruro%"
# WHERE traditional_name LIKE "%ina"
# WHERE systematic_name LIKE "%-%"
# WHERE formula REGEXP '.*C[0-9]*[\-]?H.*'
# WHERE id = 781
# WHERE formula = "HClO"
# WHERE formula = "Cu(HCrO4)2"
ORDER BY id DESC;
