package com.quimify.api.inorganic;

import com.quimify.api.error.ErrorService;
import com.quimify.api.metrics.MetricsService;
import com.quimify.api.settings.SettingsService;
import com.quimify.api.utils.Connection;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

// This class runs web searches through Google's and Bing's API.

@Component
class WebSearchComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SettingsService settingsService;

    @Autowired
    MetricsService metricsService;

    @Autowired
    ErrorService errorService;

    // Internal:

    Optional<String> search(String input) {
        Optional<String> url = Optional.empty();

        if (canFreeBingSearch())
            url = freeBingSearch(input);

        if (url.isEmpty() && canGoogleSearch())
            url = googleSearch(input);

        if (url.isEmpty())
            logger.warn("Couldn't find inorganic \"" + input + "\" on the web.");

        return url;
    }

    // Private:

    private boolean canFreeBingSearch() {
        if (!settingsService.getUseFreeBing())
            return false;

        int queries = metricsService.getFreeBingQueries();
        int dailyLimit = settingsService.getFreeBingDailyLimit();

        if (queries == dailyLimit - 1)
            logger.warn("Daily free Bing queries have just been exceeded.");

        return queries < dailyLimit;
    }

    private Optional<String> freeBingSearch(String input) {
        Optional<String> address = Optional.empty();

        try {
            Connection connection = new Connection(settingsService.getFreeBingUrl(), input);
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", settingsService.getFreeBingKey());
            JSONObject response = new JSONObject(connection.getText());

            if (response.has("webPages")) {
                JSONArray results = response.getJSONObject("webPages").getJSONArray("value");
                JSONObject firstResult = results.getJSONObject(0);

                address = Optional.ofNullable(firstResult.getString("url"));
            }
            else logger.warn("Couldn't find \"" + input + "\" on free Bing");

            metricsService.freeBingQueried(address.isPresent());
        } catch (Exception exception) {
            if (exception.toString().contains("HTTP response code: 403"))
                logger.warn("Got HTTP code 403 from free Bing (probably limit exceeded).");
            else errorService.log("Exception free Bing: " + input, exception.toString(), getClass());
        }

        return address;
    }

    private boolean canGoogleSearch() {
        if (!settingsService.getUseGoogle())
            return false;

        int queries = metricsService.getGoogleQueries();
        int dailyLimit = settingsService.getGoogleDailyLimit();

        if (queries == dailyLimit - 1)
            logger.warn("Daily Google queries have just been exceeded.");

        return queries < dailyLimit;
    }

    private Optional<String> googleSearch(String input) {
        Optional<String> address = Optional.empty();

        try {
            String url = String.format(settingsService.getGoogleUrl(), settingsService.getGoogleKey());

            Connection connection = new Connection(url, input);
            connection.setRequestProperty("Accept", "application/json");
            JSONObject response = new JSONObject(connection.getText());

            if (response.getJSONObject("searchInformation").getInt("totalResults") > 0) {
                JSONArray results = response.getJSONArray("items");
                JSONObject firstResult = results.getJSONObject(0);

                address = Optional.ofNullable(firstResult.getString("formattedUrl"));
            }
            else logger.warn("Couldn't find \"" + input + "\" on Google.");

            metricsService.googleQueried(address.isPresent());
        } catch (Exception exception) {
            if (exception.toString().contains("Server returned HTTP response code: 429"))
                logger.warn("Got HTTP code 429 from Google (probably limit exceeded).");
            else errorService.log("Exception Google: " + input, exception.toString(), getClass());
        }

        return address;
    }

}
