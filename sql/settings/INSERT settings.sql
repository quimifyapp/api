INSERT INTO settings(version,
use_google, google_daily_limit,
use_free_bing,
use_paid_bing, paid_bing_daily_limit,
google_url,
bing_url,
classifier_url,
user_agent
)
VALUES(5, # API versión
true, 100, # Google
true, # Free Bing
true, 110, # Paid Bing
"https://www.googleapis.com/customsearch/v1/siterestrict?key=%s&cx=f475470fccb0e037e&num=1&q=", 
"https://api.bing.microsoft.com/v7.0/search?count=1&q=site:www.formulacionquimica.com+", 
"http://127.0.0.1:8070",
"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.147 Safari/537.36"
)
