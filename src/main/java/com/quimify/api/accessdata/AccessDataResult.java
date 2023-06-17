package com.quimify.api.accessdata;

// This POJO class represents responses of access data to the client.

class AccessDataResult {

	private boolean updateAvailable;
	private Boolean updateNeeded;
	private String updateDetails;

	private boolean messagePresent;
	private String messageTitle;
	private String messageDetails;
	private Boolean messageLinkPresent;
	private String messageLinkLabel;
	private String messageLink;

	// Constructors:

	AccessDataResult(boolean updateAvailable, Boolean updateNeeded, String updateDetails,
					 boolean messagePresent, String messageTitle, String messageDetails,
					 Boolean messageLinkPresent, String messageLinkLabel, String messageLink) {
		this.updateAvailable = updateAvailable;
		this.updateNeeded = updateNeeded;
		this.updateDetails = updateDetails;
		this.messagePresent = messagePresent;
		this.messageTitle = messageTitle;
		this.messageDetails = messageDetails;
		this.messageLinkPresent = messageLinkPresent;
		this.messageLinkLabel = messageLinkLabel;
		this.messageLink = messageLink;
	}

	// Getters and setters (must be public to enable JSON serialization):

	public boolean getUpdateAvailable() {
		return updateAvailable;
	}

	public void setUpdateAvailable(boolean updateAvailable) {
		this.updateAvailable = updateAvailable;
	}

	public Boolean getUpdateNeeded() {
		return updateNeeded;
	}

	public void setUpdateNeeded(Boolean updateNeeded) {
		this.updateNeeded = updateNeeded;
	}

	public String getUpdateDetails() {
		return updateDetails;
	}

	public void setUpdateDetails(String updateDetails) {
		this.updateDetails = updateDetails;
	}

	public boolean getMessagePresent() {
		return messagePresent;
	}

	public void setMessagePresent(boolean messagePresent) {
		this.messagePresent = messagePresent;
	}

	public String getMessageTitle() {
		return messageTitle;
	}

	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}

	public String getMessageDetails() {
		return messageDetails;
	}

	public void setMessageDetails(String messageDetails) {
		this.messageDetails = messageDetails;
	}

	public Boolean getMessageLinkPresent() {
		return messageLinkPresent;
	}

	public void setMessageLinkPresent(Boolean messageLinkPresent) {
		this.messageLinkPresent = messageLinkPresent;
	}

	public String getMessageLinkLabel() {
		return messageLinkLabel;
	}

	public void setMessageLinkLabel(String messageLinkLabel) {
		this.messageLinkLabel = messageLinkLabel;
	}

	public String getMessageLink() {
		return messageLink;
	}

	public void setMessageLink(String messageLink) {
		this.messageLink = messageLink;
	}

}
