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
    private String updateDetailsSpanish;
    @Column()
    private String updateDetailsEnglish;

    @Column(nullable = false)
    private Boolean messagePresent;

    // If messagePresent = true:
    @Column()
    private String messageTitleSpanish;
    @Column()
    private String messageDetailsSpanish;
    @Column()
    private String messageTitleEnglish;
    @Column()
    private String messageDetailsEnglish;

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

    @Column(nullable = false)
    private Boolean rewardedAdPresent;

    // If rewardedAdPresent = true:
    private String rewardedAdUnitId;


    // Getters and setters:

    Boolean getUpdateAvailable() {
        return updateAvailable;
    }

    Boolean getUpdateNeeded() {
        return updateNeeded;
    }

    // Specific getters for update details
    String getUpdateDetailsSpanish() {
        return updateDetailsSpanish;
    }

    String getUpdateDetailsEnglish() {
        return updateDetailsEnglish;
    }

    Boolean getMessagePresent() {
        return messagePresent;
    }

    // Specific getters for message titles
    String getMessageTitleSpanish() {
        return messageTitleSpanish;
    }

    String getMessageTitleEnglish() {
        return messageTitleEnglish;
    }

    // Specific getters for message details
    String getMessageDetailsSpanish() {
        return messageDetailsSpanish;
    }

    String getMessageDetailsEnglish() {
        return messageDetailsEnglish;
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

    public Boolean getRewardedAdPresent() {
        return rewardedAdPresent;
    }

    public String getRewardedAdUnitId() {
        return rewardedAdUnitId;
    }

}
