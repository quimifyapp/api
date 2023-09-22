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

    // Constants:

    private static final int connectTimeoutMilliseconds = 5 * 1000;
    private static final int readTimeoutMilliseconds = 2 * 1000;

    // Constructors:

    public Connection(String url) throws IOException {
        this.httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        this.httpURLConnection.setRequestMethod("GET");
        this.httpURLConnection.setConnectTimeout(connectTimeoutMilliseconds);
        this.httpURLConnection.setReadTimeout(readTimeoutMilliseconds);
    }

    public Connection(String url, String parameter) throws IOException {
        this(url + encode(parameter));
    }

    // Queries:

    public void setRequestProperty(String key, String value) {
        httpURLConnection.setRequestProperty(key, value);
    }

    public String getText() throws IOException {
        BufferedReader download = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        StringBuilder text = new StringBuilder();

        String line;
        while ((line = download.readLine()) != null)
            text.append(line);

        download.close();

        httpURLConnection.disconnect();

        return text.toString();
    }

    // Static:

    public static String encode(String input) {
        return URLEncoder.encode(input, StandardCharsets.UTF_8);
    }

}
