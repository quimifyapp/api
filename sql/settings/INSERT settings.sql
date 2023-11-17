INSERT INTO settings(version, use_free_bing, free_bing_daily_limit,use_google, google_daily_limit,free_bing_url,google_url,classifier_url,user_agent)

VALUES(
, # Version
true, 33, # Free Bing
true, 100, # Google
"https://api.bing.microsoft.com/v7.0/search?count=1&q=site:www.formulacionquimica.com+", # Free Bing URL
"https://www.googleapis.com/customsearch/v1/siterestrict?key=%s&cx=f475470fccb0e037e&num=1&q=", # Google URL
"http://localhost:8000", # Classifier URL
"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.147 Safari/537.36" # User agent
);
