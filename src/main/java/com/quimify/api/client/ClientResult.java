package com.quimify.api.client;

// This POJO class represents responses of client information to the client.

class ClientResult {

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

    ClientResult(ClientModel clientModel) {
        this.updateAvailable = clientModel.getUpdateAvailable();
        this.updateNeeded = clientModel.getUpdateNeeded();
        this.updateDetails = clientModel.getUpdateDetails();
        this.messagePresent = clientModel.getMessagePresent();
        this.messageTitle = clientModel.getMessageTitle();
        this.messageDetails = clientModel.getMessageDetails();
        this.messageLinkPresent = clientModel.getMessageLinkPresent();
        this.messageLinkLabel = clientModel.getMessageLinkLabel();
        this.messageLink = clientModel.getMessageLink();
    }

    private ClientResult(boolean updateAvailable, boolean messagePresent) {
        this.updateAvailable = updateAvailable;
        this.messagePresent = messagePresent;
    }

    static ClientResult notFound() {
        return new ClientResult(false, false);
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