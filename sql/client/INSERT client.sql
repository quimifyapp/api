INSERT INTO client(platform,version,update_available,update_needed,update_details,message_present,message_title,message_details,message_link_present,message_link_label,message_link)

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
		null  # Message link
);
