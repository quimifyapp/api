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

    private Boolean bannerAdPresent;
    private String bannerAdUnitId;

    private Boolean interstitialAdPresent;
    private Integer interstitialAdPeriod; // Minimum attempts before next one
    private Integer interstitialAdOffset; // Minimum attempts before 1st one
    private String interstitialAdUnitId;

    private Boolean rewardedAdPresent;
    private String rewardedAdUnitId;

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
        this.bannerAdPresent = clientModel.getBannerAdPresent();
        this.bannerAdUnitId = clientModel.getBannerAdUnitId();
        this.interstitialAdPresent = clientModel.getInterstitialAdPresent();
        this.interstitialAdPeriod = clientModel.getInterstitialAdPeriod();
        this.interstitialAdOffset = clientModel.getInterstitialAdOffset();
        this.interstitialAdUnitId = clientModel.getInterstitialAdUnitId();
        this.rewardedAdPresent = clientModel.getRewardedAdPresent();
        this.rewardedAdUnitId = clientModel.getRewardedAdUnitId();
    }

    private ClientResult(boolean updateAvailable, boolean messagePresent) {
        this.updateAvailable = updateAvailable;
        this.messagePresent = messagePresent;
    }

    static ClientResult notFound() {
        return new ClientResult(false, false);
    }

    // Getters and setters (must be defined and public to enable JSON serialization):

    @SuppressWarnings("unused")
    public boolean getUpdateAvailable() {
        return updateAvailable;
    }

    @SuppressWarnings("unused")
    public void setUpdateAvailable(boolean updateAvailable) {
        this.updateAvailable = updateAvailable;
    }

    @SuppressWarnings("unused")
    public Boolean getUpdateNeeded() {
        return updateNeeded;
    }

    @SuppressWarnings("unused")
    public void setUpdateNeeded(Boolean updateNeeded) {
        this.updateNeeded = updateNeeded;
    }

    @SuppressWarnings("unused")
    public String getUpdateDetails() {
        return updateDetails;
    }

    @SuppressWarnings("unused")
    public void setUpdateDetails(String updateDetails) {
        this.updateDetails = updateDetails;
    }

    @SuppressWarnings("unused")
    public boolean getMessagePresent() {
        return messagePresent;
    }

    @SuppressWarnings("unused")
    public void setMessagePresent(boolean messagePresent) {
        this.messagePresent = messagePresent;
    }

    @SuppressWarnings("unused")
    public String getMessageTitle() {
        return messageTitle;
    }

    @SuppressWarnings("unused")
    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    @SuppressWarnings("unused")
    public String getMessageDetails() {
        return messageDetails;
    }

    @SuppressWarnings("unused")
    public void setMessageDetails(String messageDetails) {
        this.messageDetails = messageDetails;
    }

    @SuppressWarnings("unused")
    public Boolean getMessageLinkPresent() {
        return messageLinkPresent;
    }

    @SuppressWarnings("unused")
    public void setMessageLinkPresent(Boolean messageLinkPresent) {
        this.messageLinkPresent = messageLinkPresent;
    }

    @SuppressWarnings("unused")
    public String getMessageLinkLabel() {
        return messageLinkLabel;
    }

    @SuppressWarnings("unused")
    public void setMessageLinkLabel(String messageLinkLabel) {
        this.messageLinkLabel = messageLinkLabel;
    }

    @SuppressWarnings("unused")
    public String getMessageLink() {
        return messageLink;
    }

    @SuppressWarnings("unused")
    public void setMessageLink(String messageLink) {
        this.messageLink = messageLink;
    }

    @SuppressWarnings("unused")
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    @SuppressWarnings("unused")
    public boolean isMessagePresent() {
        return messagePresent;
    }

    @SuppressWarnings("unused")
    public Boolean getBannerAdPresent() {
        return bannerAdPresent;
    }

    @SuppressWarnings("unused")
    public void setBannerAdPresent(Boolean bannerAdPresent) {
        this.bannerAdPresent = bannerAdPresent;
    }

    @SuppressWarnings("unused")
    public String getBannerAdUnitId() {
        return bannerAdUnitId;
    }

    @SuppressWarnings("unused")
    public void setBannerAdUnitId(String bannerAdUnitId) {
        this.bannerAdUnitId = bannerAdUnitId;
    }

    @SuppressWarnings("unused")
    public Boolean getInterstitialAdPresent() {
        return interstitialAdPresent;
    }

    @SuppressWarnings("unused")
    public void setInterstitialAdPresent(Boolean interstitialAdPresent) {
        this.interstitialAdPresent = interstitialAdPresent;
    }

    @SuppressWarnings("unused")
    public Integer getInterstitialAdPeriod() {
        return interstitialAdPeriod;
    }

    @SuppressWarnings("unused")
    public void setInterstitialAdPeriod(Integer interstitialAdPeriod) {
        this.interstitialAdPeriod = interstitialAdPeriod;
    }

    @SuppressWarnings("unused")
    public Integer getInterstitialAdOffset() {
        return interstitialAdOffset;
    }

    @SuppressWarnings("unused")
    public void setInterstitialAdOffset(Integer interstitialAdOffset) {
        this.interstitialAdOffset = interstitialAdOffset;
    }

    @SuppressWarnings("unused")
    public String getInterstitialAdUnitId() {
        return interstitialAdUnitId;
    }

    @SuppressWarnings("unused")
    public void setInterstitialAdUnitId(String interstitialAdUnitId) {
        this.interstitialAdUnitId = interstitialAdUnitId;
    }

    @SuppressWarnings("unused")
    public Boolean getRewardedAdPresent() {
        return rewardedAdPresent;
    }

    @SuppressWarnings("unused")
    public void setRewardedAdPresent(Boolean rewardedAdPresent) {
        this.rewardedAdPresent = rewardedAdPresent;
    }

    @SuppressWarnings("unused")
    public String getRewardedAdUnitId() {
        return rewardedAdUnitId;
    }

    @SuppressWarnings("unused")
    public void setRewardedAdUnitId(String rewardedAdUnitId) {
        this.rewardedAdUnitId = rewardedAdUnitId;
    }
}
