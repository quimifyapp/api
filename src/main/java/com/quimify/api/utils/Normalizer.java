package com.quimify.api.utils;

import org.springframework.lang.Nullable;

// This class normalizes text.

public class Normalizer {

    // I.E.: "Óxido de hierro (III)" -> "oxidodehierroiii"
    public static String get(@Nullable String text) {
        if (text == null)
            return null;

        text = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD); // Without accents or diacritics
        text = text.replaceAll("[^\\p{ASCII}]", ""); // Only ASCII
        text = text.replaceAll("[^A-Za-z0-9]", ""); // Only alphanumeric

        return text.toLowerCase();
    }

}
