INSERT INTO client(platform,version,update_available,update_needed,update_details,message_present,message_title,message_details,message_link_present,message_link_label,message_link,banner_ad_present,banner_ad_unit_id,interstitial_ad_present,interstitial_ad_period,interstitial_ad_offset,interstitial_ad_unit_id,rewarded_ad_present,rewarded_ad_unit_id)

VALUES(
, # Platform ("android" or "ios")
, # Version
false, # Update available
# If true:
	null, # Update needed
	null, # Update details
false, # Message present
# If true:
	null, # Message title
	null, # Message details
	null, # Message link present
    # If true:
		null, # Message link label
		null, # Message link
false, # Banner ad present
# If true:
	null, # Banner ad unit id
false, # Interstitial ad present
# If true:
	null, # Interstitial ad period
	null, # Interstitial ad offset
	null, # Interstitial ad unit id
false, # Rewarded ad present
# If true:
	null # Rewarded ad unit id
);
