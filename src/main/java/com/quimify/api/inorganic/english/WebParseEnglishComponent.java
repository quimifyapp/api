package com.quimify.api.inorganic.english;

import com.quimify.api.inorganic.WebParseComponent;

import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class WebParseEnglishComponent extends WebParseComponent<InorganicEnglishModel> {

    @Override
    protected Optional<InorganicEnglishModel> tryParse(String url) {
        return Optional.empty(); // TODO implement
    }

}
