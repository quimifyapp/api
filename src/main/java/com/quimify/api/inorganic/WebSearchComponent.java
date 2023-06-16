package com.quimify.api.inorganic;

import com.quimify.api.error.ErrorService;
import com.quimify.api.metrics.MetricsService;
import com.quimify.api.settings.SettingsService;
import com.quimify.api.utils.Connection;
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

    Optional<WebSearchResult> search(String input) {
        Optional<WebSearchResult> result = Optional.empty();

        if (canFreeBingSearch())
            result = freeBingSearch(input);

        if (result.isEmpty() && canGoogleSearch())
            result = googleSearch(input);

        if (result.isEmpty())
            logger.warn("Couldn't find inorganic \"" + input + "\" on the web.");

        return result;
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

    private Optional<WebSearchResult> freeBingSearch(String input) {
        Optional<WebSearchResult> result = Optional.empty();

        try {
            Connection connection = new Connection(settingsService.getFreeBingUrl(), input);
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", settingsService.getFreeBingKey());
            JSONObject response = new JSONObject(connection.getText());

            if (response.has("webPages")) {
                JSONObject firstResult = response.getJSONObject("webPages").getJSONArray("value").getJSONObject(0);

                String title = firstResult.getString("name");
                String address = firstResult.getString("url");

                result = Optional.of(new WebSearchResult(title, address));
            }
            else logger.warn("Couldn't find \"" + input + "\" on free Bing");

            metricsService.freeBingSearched(result.isPresent());
        } catch (Exception exception) {
            if (exception.toString().contains("HTTP response code: 403"))
                logger.warn("Got HTTP code 403 from free Bing (probably limit exceeded).");
            else errorService.log("Exception free Bing: " + input, exception.toString(), getClass());
        }

        return result;
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

    private Optional<WebSearchResult> googleSearch(String input) {
        Optional<WebSearchResult> result = Optional.empty();

        try {
            String url = String.format(settingsService.getGoogleUrl(), settingsService.getGoogleKey());

            Connection connection = new Connection(url, input);
            connection.setRequestProperty("Accept", "application/json");
            JSONObject response = new JSONObject(connection.getText());

            if (response.getJSONObject("searchInformation").getInt("totalResults") > 0) {
                JSONObject firstResult = response.getJSONArray("items").getJSONObject(0);

                String title = firstResult.getString("title");
                String address = firstResult.getString("formattedUrl");

                result = Optional.of(new WebSearchResult(title, address));
            }
            else logger.warn("Couldn't find \"" + input + "\" on Google.");

            metricsService.googleSearched(result.isPresent());
        } catch (Exception exception) {
            if (exception.toString().contains("Server returned HTTP response code: 429"))
                logger.warn("Got HTTP code 429 from Google (probably limit exceeded).");
            else errorService.log("Exception Google: " + input, exception.toString(), getClass());
        }

        return result;
    }

}
