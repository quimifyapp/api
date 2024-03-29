package com.quimify.api.client;


import org.hibernate.annotations.Check;

import javax.persistence.*;

// This class represents client versions and information.

@Entity
@Table(name = "client")
@IdClass(ClientId.class)
@Check(constraints = "platform IN ('android', 'ios')")
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

    @Column(nullable = false)
    private Boolean bannerAdPresent;

    // If bannerAdPresent = true:
    private String bannerAdUnitId;

    @Column(nullable = false)
    private Boolean interstitialAdPresent;

    // If interstitialAdPresent = true:
    private Integer interstitialAdPeriod;
    private Integer interstitialAdOffset;
    private String interstitialAdUnitId;

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

    public Boolean getBannerAdPresent() {
        return bannerAdPresent;
    }

    void setBannerAdPresent(Boolean bannerAdPresent) {
        this.bannerAdPresent = bannerAdPresent;
    }

    String getBannerAdUnitId() {
        return bannerAdUnitId;
    }

    void setBannerAdUnitId(String bannerAdUnitId) {
        this.bannerAdUnitId = bannerAdUnitId;
    }

    Boolean getInterstitialAdPresent() {
        return interstitialAdPresent;
    }

    void setInterstitialAdPresent(Boolean interstitialAdPresent) {
        this.interstitialAdPresent = interstitialAdPresent;
    }

    Integer getInterstitialAdPeriod() {
        return interstitialAdPeriod;
    }

    void setInterstitialAdPeriod(Integer interstitialAdPeriod) {
        this.interstitialAdPeriod = interstitialAdPeriod;
    }

    Integer getInterstitialAdOffset() {
        return interstitialAdOffset;
    }

    void setInterstitialAdOffset(Integer interstitialAdOffset) {
        this.interstitialAdOffset = interstitialAdOffset;
    }

    String getInterstitialAdUnitId() {
        return interstitialAdUnitId;
    }

    void setInterstitialAdUnitId(String interstitialAdUnitId) {
        this.interstitialAdUnitId = interstitialAdUnitId;
    }

}
