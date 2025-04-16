package com.quimify.api.inorganic;

import org.springframework.stereotype.Component;

import java.util.Optional;

// This class parses inorganic compounds from FQ.com web pages.

@Component
public abstract class WebParseComponent<InorganicModel extends com.quimify.api.inorganic.InorganicModel> {

    // Internal:

    protected abstract Optional<InorganicModel> tryParse(String url);

}
