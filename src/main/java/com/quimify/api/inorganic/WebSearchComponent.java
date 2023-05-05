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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SettingsService settingsService; // Settings logic

    @Autowired
    MetricsService metricsService; // Daily metrics logic

    @Autowired
    ErrorService errorService; // API errors logic

    private String title;
    private String address;

    // Internal:

    boolean search(String input) {
        this.title = null;
        this.address = null;

        boolean searchDone = false;

        if (canGoogleSearch())
            searchDone = googleSearch(input);

        if (!searchDone && canFreeBingSearch())
            searchDone = bingSearch(input, settingsService.getFreeBingKey(), "free Bing");

        if (!searchDone && canPaidBingSearch()) {
            searchDone = bingSearch(input, settingsService.getPaidBingKey(), "paid Bing");

            if (searchDone)
                metricsService.paidBingQuery();
        }

        return title != null && address != null;
    }

    // Private:

    private boolean canGoogleSearch() {
        if (!settingsService.getUseGoogle())
            return false;

        int queries = metricsService.getGoogleQueries();

        if (queries == settingsService.getGoogleDailyLimit() - 1)
            logger.warn("Daily Google queries have been exceeded.");

        return queries < settingsService.getGoogleDailyLimit();
    }

    boolean googleSearch(String input) {
        boolean searched;

        try {
            Connection connection = new Connection(settingsService.getGoogleUrl(), input);
            connection.setProperty("Accept", "application/json");
            JSONObject response = new JSONObject(connection.getText());

            if (response.getJSONObject("searchInformation").getInt("totalResults") > 0) {
                JSONObject result = response.getJSONArray("items").getJSONObject(0);

                title = result.getString("title");
                address = result.getString("formattedUrl");

                metricsService.googleSearchFound();
            }
            else {
                logger.warn("Couldn't find \"" + input + "\" on Google.");
                metricsService.googleSearchNotFound();
            }

            searched = true;
        } catch (Exception exception) {
            if (exception.toString().contains("Server returned HTTP response code: 429"))
                logger.warn("Got HTTP code 403 from Google");
            else errorService.log("IOException Google: " + input, exception.toString(), getClass());

            searched = false;
        }

        return searched;
    }

    private boolean canFreeBingSearch() {
        return settingsService.getUseFreeBing();
    }

    private boolean canPaidBingSearch() {
        if (!settingsService.getUsePaidBing())
            return false;

        int queries = metricsService.getPaidBingQueries();

        if (queries == settingsService.getPaidBingDailyLimit() - 1)
            logger.warn("Daily paid Bing queries have been exceeded.");

        return queries < settingsService.getPaidBingDailyLimit();
    }

    private boolean bingSearch(String input, String apiKey, String apiName) {
        boolean searched;

        try {
            Connection connection = new Connection(settingsService.getBingUrl(), input);
            connection.setProperty("Ocp-Apim-Subscription-Key", apiKey);
            JSONObject response = new JSONObject(connection.getText());

            if (response.has("webPages")) {
                JSONObject result = response.getJSONObject("webPages").getJSONArray("value").getJSONObject(0);

                title = result.getString("name");
                address = result.getString("url");

                metricsService.bingSearchFound();
            }
            else {
                logger.warn("Couldn't find \"" + input + "\" on " + apiName);
                metricsService.bingSearchNotFound();
            }

            searched = true;
        } catch (IOException exception) {
            if (exception.toString().contains("HTTP response code: 403"))
                logger.warn("Got HTTP code 403 from " + apiName);
            else errorService.log("IOException " + apiName + ": " + input, exception.toString(), getClass());

            searched = false;
        } catch (Exception exception) {
            errorService.log("Exception " + apiName + ": " + input, exception.toString(), getClass());

            searched = false;
        }

        return searched;
    }

    // Getters:

    String getTitle() {
        return title;
    }

    String getAddress() {
        return address;
    }

}
