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

import java.io.IOException;
import java.util.Optional;

// This class runs Bing searches through their API.

@Component
public class BingComponent {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SettingsService settingsService; // Settings logic

    @Autowired
    MetricsService metricsService; // Daily metrics logic

    @Autowired
    ErrorService errorService; // API errors logic

    // Protected:

    protected Optional<WebSearchResult> freeSearch(String input) {
        return canFreeSearch()
                ? search(input, settingsService.getFreeBingKey(), "free Bing")
                : Optional.empty();
    }

    protected Optional<WebSearchResult> paidSearch(String input) {
        metricsService.countPaidBingSearch();

        return canPaidSearch()
                ? search(input, settingsService.getPaidBingKey(), "paid Bing")
                : Optional.empty();
    }

    // Private:

    private boolean canFreeSearch() {
        return settingsService.getFreeBingON();
    }

    private boolean canPaidSearch() {
        if(!settingsService.getPaidBingON())
            return false;

        int queries = metricsService.getPaidBingQueries();

        if (queries == settingsService.getPaidBingLimit() - 1)
            logger.warn("Daily paid Bing queries have been exceeded.");

        return queries < settingsService.getPaidBingLimit();
    }

    private Optional<WebSearchResult> search(String input, String apiKey, String apiName) {
        Optional<WebSearchResult> webSearchResult;

        try {
            Download connection = new Download(settingsService.getBingURL(), input);
            connection.setProperty("Ocp-Apim-Subscription-Key", apiKey);
            JSONObject response = new JSONObject(connection.getText());

            if (response.has("webPages")) {
                JSONObject result = response.getJSONObject("webPages").getJSONArray("value").getJSONObject(0);
                String title = result.getString("name");
                String address = result.getString("url");

                webSearchResult = Optional.of(new WebSearchResult(title, address));
            }
            else {
                logger.warn("Couldn't find \"" + input + "\" on " + apiName);
                webSearchResult = Optional.of(WebSearchResult.notFound);
            }
        } catch (IOException exception) {
            if (exception.toString().contains("HTTP response code: 403"))
                logger.warn(apiName + " returned HTTP code 403.");
            else errorService.saveError("IOException " + apiName + ": " + input, exception.toString(), this.getClass());

            webSearchResult = Optional.empty();
        } catch (Exception exception) {
            errorService.saveError("Exception " + apiName + ": " + input, exception.toString(), this.getClass());
            webSearchResult = Optional.empty();
        }

        metricsService.countBingSearch(webSearchResult.isPresent() && webSearchResult.get().isFound(), false);

        return webSearchResult;
    }

}
