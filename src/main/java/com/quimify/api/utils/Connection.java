package com.quimify.api.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// This class makes HTTP(s) GET requests and returns the body text from their responses.

public class Connection {

    private final HttpURLConnection httpURLConnection;

    // Constructors:

    public Connection(String url) throws IOException {
        this.httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        this.httpURLConnection.setRequestMethod("GET");
    }

    public Connection(String url, String parameter) throws IOException {
        this.httpURLConnection = (HttpURLConnection) new URL(url + encodeForUrl(parameter)).openConnection();
        this.httpURLConnection.setRequestMethod("GET");
    }

    // Queries:

    public void setProperty(String key, String value) {
        httpURLConnection.setRequestProperty(key, value);
    }

    public String getText() throws IOException {
        BufferedReader download = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        StringBuilder text = new StringBuilder();

        String line;
        while((line = download.readLine()) != null)
            text.append(line);

        download.close();
        httpURLConnection.disconnect();

        return text.toString();
    }

    // Static:

    public static String encodeForUrl(String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }

}
