package com.quimify.api.inorganic;

import com.quimify.api.error.ErrorService;
import com.quimify.api.metrics.MetricsService;
import com.quimify.api.settings.SettingsService;
import com.quimify.api.utils.Connection;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

// This class runs web searches through Google's and Bing's API.

@Component
@Scope("prototype")
class WebSearchComponent {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SettingsService settingsService; // Settings logic

    @Autowired
    MetricsService metricsService; // Daily metrics logic

    @Autowired
    ErrorService errorService; // API errors logic

    private String input;

    private boolean found;
    private String title;
    private String address;

    // Protected:

    protected void search(String input) {
        this.input = input;

        if(canGoogleSearch())
            googleSearch();
        else if(canFreeBingSearch())
            bingSearch(settingsService.getFreeBingKey(), "free Bing");
        else if(canPaidBingSearch())
            bingSearch(settingsService.getPaidBingKey(), "paid Bing");
        else found = false;
    }

    // Private:

    private boolean canGoogleSearch() {
        if(!settingsService.getGoogleON())
            return false;

        int queries = metricsService.getGoogleQueries();

        if (queries == settingsService.getGoogleLimit() - 1)
            logger.warn("Daily Google queries have been exceeded.");

        return queries < settingsService.getGoogleLimit();
    }

    protected void googleSearch() {
        try {
            Connection connection = new Connection(settingsService.getGoogleURL(), input);
            connection.setProperty("Accept", "application/json");
            JSONObject response = new JSONObject(connection.getText());

            if (response.getJSONObject("searchInformation").getInt("totalResults") > 0) {
                JSONObject result = response.getJSONArray("items").getJSONObject(0);

                found = true;
                title = result.getString("title");
                address = result.getString("formattedUrl");
            }
            else {
                logger.warn("Couldn't find \"" + input + "\" on Google.");
                found = false;
            }
        } catch (Exception exception) {
            if (exception.toString().contains("Server returned HTTP response code: 429"))
                logger.warn("Google returned HTTP code 429.");
            else errorService.log("IOException Google: " + input, exception.toString(), this.getClass());

            found = false;
        }

        metricsService.googleSearch(found);
    }

    private boolean canFreeBingSearch() {
        return settingsService.getFreeBingON();
    }

    private boolean canPaidBingSearch() {
        if(!settingsService.getPaidBingON())
            return false;

        int queries = metricsService.getPaidBingQueries();

        if (queries == settingsService.getPaidBingLimit() - 1)
            logger.warn("Daily paid Bing queries have been exceeded.");

        return queries < settingsService.getPaidBingLimit();
    }

    private void bingSearch(String apiKey, String apiName) {
        try {
            Connection connection = new Connection(settingsService.getBingURL(), input);
            connection.setProperty("Ocp-Apim-Subscription-Key", apiKey);
            JSONObject response = new JSONObject(connection.getText());

            if (response.has("webPages")) {
                JSONObject result = response.getJSONObject("webPages").getJSONArray("value").getJSONObject(0);

                found = true;
                title = result.getString("name");
                address = result.getString("url");
            }
            else {
                logger.warn("Couldn't find \"" + input + "\" on " + apiName);
                found = false;
            }
        } catch (IOException exception) {
            if (exception.toString().contains("HTTP response code: 403"))
                logger.warn(apiName + " returned HTTP code 403.");
            else errorService.log("IOException " + apiName + ": " + input, exception.toString(), this.getClass());

            found = false;
        } catch (Exception exception) {
            errorService.log("Exception " + apiName + ": " + input, exception.toString(), this.getClass());
            found = false;
        }

        metricsService.bingSearch(found);
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