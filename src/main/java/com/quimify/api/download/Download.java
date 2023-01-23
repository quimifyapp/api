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

    private HttpURLConnection httpURLConnection;

    // Constructors:

    public Download(String url, String parameter) throws IOException {
        this.setConnection(url + formatForUrl(parameter));
    }

    public Download(String url) throws IOException {
        this.setConnection(url);
    }

    private void setConnection(String url) throws IOException {
        this.httpURLConnection = (HttpURLConnection)(new URL(url)).openConnection();
        this.httpURLConnection.setRequestMethod("GET");
    }

    // Static:

    public static String formatForUrl(String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }

    // Public:

    public void setProperty(String key, String value) {
        this.httpURLConnection.setRequestProperty(key, value);
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
