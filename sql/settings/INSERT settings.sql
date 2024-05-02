INSERT INTO settings(version, use_bing, bing_daily_limit, use_google, google_daily_limit, bing_url, google_url, classifier_ai_url, user_agent)

VALUES(
, # Version
true, 33, # Free Bing
true, 100, # Google
"https://api.bing.microsoft.com/v7.0/search?count=1&q=site:www.formulacionquimica.com+", # Free Bing URL
"https://www.googleapis.com/customsearch/v1/siterestrict?key=%s&cx=773fc1cf8a92a4979&num=1&q=", # Google URL
"http://localhost:8070/?input=", # Classifier URL
"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.147 Safari/537.36" # User agent
);
