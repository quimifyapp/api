package com.quimify.api.accessdata;

import javax.persistence.*;

// This class represents access data needed to load the client.

@Entity
@Table(name = "access_data")
class AccessDataModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Integer clientVersion;

	@Column(nullable = false)
	private Boolean updateAvailable;
	// If updateAvailable = true:
	@Column()
	private Boolean updateNeeded;
	@Column()
	private String updateDetails;

	@Column(nullable = false)
	private Boolean messagePresent;
	// If messagePresent = true:
	@Column()
	private String messageTitle;
	@Column()
	private String messageDetails;

	@Column() // If messagePresent = true
	private Boolean messageLinkPresent;
	// If messageLinkPresent = true:
	@Column()
	private String messageLinkLabel;
	@Column()
	private String messageLink;

	// Getters and setters:

	Integer getClientVersion() {
		return clientVersion;
	}

	void setClientVersion(Integer version) {
		this.clientVersion = version;
	}

	Boolean getUpdateAvailable() {
		return updateAvailable;
	}

	void setUpdateAvailable(Boolean updateAvailable) {
		this.updateAvailable = updateAvailable;
	}

	Boolean getUpdateNeeded() {
		return updateNeeded;
	}

	void setUpdateNeeded(Boolean updateNeeded) {
		this.updateNeeded = updateNeeded;
	}

	String getUpdateDetails() {
		return updateDetails;
	}

	void setUpdateDetails(String updateDetails) {
		this.updateDetails = updateDetails;
	}

	Boolean getMessagePresent() {
		return messagePresent;
	}

	void setMessagePresent(Boolean messagePresent) {
		this.messagePresent = messagePresent;
	}

	String getMessageTitle() {
		return messageTitle;
	}

	void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}

	String getMessageDetails() {
		return messageDetails;
	}

	void setMessageDetails(String messageDetails) {
		this.messageDetails = messageDetails;
	}

	Boolean getMessageLinkPresent() {
		return messageLinkPresent;
	}

	void setMessageLinkPresent(Boolean messageLinkPresent) {
		this.messageLinkPresent = messageLinkPresent;
	}

	String getMessageLinkLabel() {
		return messageLinkLabel;
	}

	void setMessageLinkLabel(String messageLinkLabel) {
		this.messageLinkLabel = messageLinkLabel;
	}

	String getMessageLink() {
		return messageLink;
	}

	void setMessageLink(String messageLink) {
		this.messageLink = messageLink;
	}

}
