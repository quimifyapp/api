INSERT INTO settings(version,
use_google, google_daily_limit,
use_free_bing,
use_paid_bing, paid_bing_daily_limit,
google_url,
bing_url,
free_bing_key, 
paid_bing_key,
user_agent)
VALUES(5, # Versión
true, 100, # Google
true, # Free Bing
false, 0, # Paid Bing
, # Google URL
, # Bing URL
, # Free Bing key
, # Paid Bing key
"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.147 Safari/537.36") # User agent
