INSERT INTO client(platform,version,update_available,update_needed,update_details_spanish, update_details_english,message_present,message_title_spanish,message_title_english,message_details_spanish,message_details_english,message_link_present,message_link_label,message_link,banner_ad_present,banner_ad_unit_id,interstitial_ad_present,interstitial_ad_period,interstitial_ad_offset,interstitial_ad_unit_id,rewarded_ad_present,rewarded_ad_unit_id)

VALUES(
"ios", # Platform ("android" or "ios")
21, # Version
false, # Update available
# If true:
	null, # Update needed
	null, # Update details spanish
	null, # Update details english
true, # Message present
# If true:
	"Hola test", # Message title spanish
    "Hello test", # Message title english
	"SPANISH This a test for localization development", # Message details spanish
    "ENGLISH This a test for localization development", # Message details english
	false, # Message link present
    # If true:
		null, # Message link label
		null, # Message link
true, # Banner ad present
# If true:
	"ca-app-pub-8575538787782344/5022579587", # Banner ad unit id
true, # Interstitial ad present
# If true:
	2, # Interstitial ad period
	1, # Interstitial ad offset
	"ca-app-pub-8575538787782344/6169545266", # Interstitial ad unit id
true, # Rewarded ad present
# If true:
	"ca-app-pub-8575538787782344/4969356309" # Rewarded ad unit id
);
