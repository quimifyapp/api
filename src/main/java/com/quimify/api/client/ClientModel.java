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

    Boolean getUpdateAvailable() {
        return updateAvailable;
    }

    Boolean getUpdateNeeded() {
        return updateNeeded;
    }

    String getUpdateDetails() {
        return updateDetails;
    }

    Boolean getMessagePresent() {
        return messagePresent;
    }

    String getMessageTitle() {
        return messageTitle;
    }

    String getMessageDetails() {
        return messageDetails;
    }

    Boolean getMessageLinkPresent() {
        return messageLinkPresent;
    }

    String getMessageLinkLabel() {
        return messageLinkLabel;
    }

    String getMessageLink() {
        return messageLink;
    }

    Boolean getBannerAdPresent() {
        return bannerAdPresent;
    }

    String getBannerAdUnitId() {
        return bannerAdUnitId;
    }

    Boolean getInterstitialAdPresent() {
        return interstitialAdPresent;
    }

    Integer getInterstitialAdPeriod() {
        return interstitialAdPeriod;
    }

    Integer getInterstitialAdOffset() {
        return interstitialAdOffset;
    }

    String getInterstitialAdUnitId() {
        return interstitialAdUnitId;
    }

}
