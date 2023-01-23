package com.quimify.api.inorganic;

// This class represents the result of a web search.

class WebSearchResult {

    private final boolean found;
    private final String title;
    private final String address;

    // Constants:

    protected static final WebSearchResult notFound = new WebSearchResult();

    // Constructors:

    protected WebSearchResult(String title, String address) {
        this.found = true;
        this.title = title;
        this.address = address;
    }

    private WebSearchResult() {
        this.found = false;
        this.title = null;
        this.address = null;
    }

    // Getters:

    protected boolean isFound() {
        return found;
    }

    protected String getTitle() {
        return title;
    }

    protected String getAddress() {
        return address;
    }

}
