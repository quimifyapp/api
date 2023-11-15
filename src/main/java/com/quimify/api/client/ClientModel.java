package com.quimify.api.client;

import org.hibernate.annotations.Check;

import javax.persistence.*;

// This class represents client versions and information.

@Entity
@Table(name = "client")
@IdClass(ClientId.class)
@Check(constraints =
        "(update_available != true OR " +
                "(update_needed IS NOT NULL AND " +
                "update_details IS NOT NULL)) AND " +
        "(message_present != true OR " +
                "(message_title IS NOT NULL AND " +
                "message_details IS NOT NULL AND " +
                "message_link_present IS NOT NULL)) AND " +
        "(message_link_present != true OR " +
                "(message_link_label IS NOT NULL AND " +
                "message_link IS NOT NULL))"
)
class ClientModel {

    @Id
    private String platform;

    @Id
    private Integer version;

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

    // If messageLinkPresent = true:
    @Column()
    private String messageLinkLabel;
    @Column()
    private String messageLink;

    // Getters and setters:

    String getPlatform() {
        return platform;
    }

    void setPlatform(String platform) {
        this.platform = platform;
    }

    Integer getVersion() {
        return version;
    }

    void setVersion(Integer version) {
        this.version = version;
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
