package com.quimify.api.client;

// Esta clase representa las entregas al cliente de la configuración.

class ClientResult {

	private Boolean updateAvailable;
	private Boolean updateNeeded;
	private String updateDetails;

	private Boolean messagePresent;
	private String messageTitle;
	private String messageDetails;
	private Boolean messageLinkPresent;
	private String messageLinkLabel;
	private String messageLink;

	// --------------------------------------------------------------------------------

	// Constructor:

	protected ClientResult(Boolean updateAvailable, Boolean updateNeeded,
						String updateDetails, Boolean messagePresent, String messageTitle,
						String messageDetails, Boolean messageLinkPresent, String messageLinkLabel,
						String messageLink) {
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

	// Getters y setters:


	protected Boolean getUpdateAvailable() {
		return updateAvailable;
	}

	protected void setUpdateAvailable(Boolean updateAvailable) {
		this.updateAvailable = updateAvailable;
	}

	protected Boolean getUpdateNeeded() {
		return updateNeeded;
	}

	protected void setUpdateNeeded(Boolean updateNeeded) {
		this.updateNeeded = updateNeeded;
	}

	protected String getUpdateDetails() {
		return updateDetails;
	}

	protected void setUpdateDetails(String updateDetails) {
		this.updateDetails = updateDetails;
	}

	protected Boolean getMessagePresent() {
		return messagePresent;
	}

	protected void setMessagePresent(Boolean messagePresent) {
		this.messagePresent = messagePresent;
	}

	protected String getMessageTitle() {
		return messageTitle;
	}

	protected void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}

	protected String getMessageDetails() {
		return messageDetails;
	}

	protected void setMessageDetails(String messageDetails) {
		this.messageDetails = messageDetails;
	}

	protected Boolean getMessageLinkPresent() {
		return messageLinkPresent;
	}

	protected void setMessageLinkPresent(Boolean messageLinkPresent) {
		this.messageLinkPresent = messageLinkPresent;
	}

	protected String getMessageLinkLabel() {
		return messageLinkLabel;
	}

	protected void setMessageLinkLabel(String messageLinkLabel) {
		this.messageLinkLabel = messageLinkLabel;
	}

	protected String getMessageLink() {
		return messageLink;
	}

	protected void setMessageLink(String messageLink) {
		this.messageLink = messageLink;
	}

}
