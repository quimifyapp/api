package com.quimify.api.inorganic;

class WebSearchResult {

    private final String title;
    private final String address;

    // Constructor:

    WebSearchResult(String title, String address) {
        this.title = title;
        this.address = address;
    }

    // Getters:

    String getTitle() {
        return title;
    }

    String getAddress() {
        return address;
    }

}
