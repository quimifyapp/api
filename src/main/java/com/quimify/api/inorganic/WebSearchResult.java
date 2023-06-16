package com.quimify.api.inorganic;

public class WebSearchResult {

    private final String title;
    private final String address;

    // Constructor:

    public WebSearchResult(String title, String address) {
        this.title = title;
        this.address = address;
    }

    // Getters:

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

}
