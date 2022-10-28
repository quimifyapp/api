package com.quimify.api.client;

import javax.persistence.*;

// Esta clase representa la configuración de cada cliente según su versión.

@Entity // Es un modelo real
@Table(name = "client") // En la tabla 'cliente' de la DB
public class ClientModel {

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

	@Column()
	private Boolean messageLinkPresent;
	// If messagePresent = true and messageLinkPresent = true:
	@Column()
	private String messageLinkLabel;
	@Column()
	private String messageLink;

	// --------------------------------------------------------------------------------

	// Getters y setters:

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Boolean getUpdateAvailable() {
		return updateAvailable;
	}

	public void setUpdateAvailable(Boolean updateAvailable) {
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

	public Boolean getMessagePresent() {
		return messagePresent;
	}

	public void setMessagePresent(Boolean messagePresent) {
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
