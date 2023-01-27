package com.quimify.api.download;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// This class makes HTTP GET requests and returns the text from their responses.

public class Download {

    private final HttpURLConnection httpURLConnection;

    // Static:

    public static String formatForUrl(String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }

    // Constructors:

    public Download(String url) throws IOException {
        httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        httpURLConnection.setRequestMethod("GET");
    }

    public Download(String url, String parameter) throws IOException {
        httpURLConnection = (HttpURLConnection) new URL(url + formatForUrl(parameter)).openConnection();
        httpURLConnection.setRequestMethod("GET");
    }

    // Queries:

    public void setProperty(String key, String value) {
        httpURLConnection.setRequestProperty(key, value);
    }

    public String getText() throws IOException {
        BufferedReader download = new BufferedReader(new InputStreamReader(this.httpURLConnection.getInputStream()));
        StringBuilder text = new StringBuilder();

        String line;
        while((line = download.readLine()) != null)
            text.append(line);

        download.close();
        this.httpURLConnection.disconnect();

        return text.toString();
    }

}
