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
true, 110, # Paid Bing
"https://www.googleapis.com/customsearch/v1/siterestrict?key=AIzaSyAC0vTdsgSmyp5VP_9ZtUNumBDjxDgBosM&cx=f475470fccb0e037e&num=1&q=", # Google URL
"https://api.bing.microsoft.com/v7.0/search?count=1&q=site:www.formulacionquimica.com+", # Bing URL
"3aba1c345f31492cb5bc8dfaf9f47097", # Free Bing key
"612b8d0be02d47b1b5389aa65a9165f6", # Paid Bing key
"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.147 Safari/537.36") # User agent
