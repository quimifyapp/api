package com.quimify.api.client;

import javax.persistence.*;

// Esta clase representa la configuración de cada cliente según su versión.

@Entity // Es un modelo real
@Table(name = "client") // En la tabla 'client' de la DB
class ClientModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Integer version; // Se corresponde con la versión del cliente

	// Bienvenida:

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

	// --------------------------------------------------------------------------------

	// Getters y setters:

	protected Integer getVersion() {
		return version;
	}

	protected void setVersion(Integer version) {
		this.version = version;
	}

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
