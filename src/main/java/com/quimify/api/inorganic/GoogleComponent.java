package com.quimify.api.inorganic;

import com.quimify.api.download.Download;
import com.quimify.api.error.ErrorService;
import com.quimify.api.metrics.MetricsService;
import com.quimify.api.settings.SettingsService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

// This class runs Google searches through their API.

@Component
class GoogleComponent {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SettingsService settingsService; // Settings logic

    @Autowired
    MetricsService metricsService; // Daily metrics logic

    @Autowired
    ErrorService errorService; // API errors logic

    // Protected:

    protected Optional<WebSearchResult> search(String input) {
        if(!canSearch())
            return Optional.empty();

        Optional<WebSearchResult> webSearchResult;

        try {
            Download connection = new Download(settingsService.getGoogleURL(), input);
            connection.setProperty("Accept", "application/json");
            JSONObject response = new JSONObject(connection.getText());

            if (response.getJSONObject("searchInformation").getInt("totalResults") > 0) {
                JSONObject result = response.getJSONArray("items").getJSONObject(0);
                String title = result.getString("title");
                String address = result.getString("formattedUrl");

                webSearchResult = Optional.of(new WebSearchResult(title, address));
            }
            else {
                logger.warn("Couldn't find \"" + input + "\" on Google.");
                webSearchResult = Optional.of(WebSearchResult.notFound);
            }
        } catch (Exception exception) {
            if (exception.toString().contains("Server returned HTTP response code: 429"))
                logger.warn("Google returned HTTP code 429.");
            else errorService.log("IOException Google: " + input, exception.toString(), this.getClass());

            webSearchResult = Optional.empty();
        }

        metricsService.googleSearch(webSearchResult.isPresent() && webSearchResult.get().isFound());

        return webSearchResult;
    }

    // Private:

    private boolean canSearch() {
        if(!settingsService.getGoogleON())
            return false;

        int queries = metricsService.getGoogleQueries();

        if (queries == settingsService.getGoogleLimit() - 1)
            logger.warn("Daily Google queries have been exceeded.");

        return queries < settingsService.getGoogleLimit();
    }

}
