package com.innowise.informationhandler.service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordExtractor {
    private static final String WORD_REGEX =
            "\\p{L}+(?:[-'’]\\p{L}+)*";

    private static final Pattern WORD_PATTERN =
            Pattern.compile(WORD_REGEX);

    public Set<String> extractUniqueWords(String text){
        Objects.requireNonNull(text);
        HashSet<String> words = new HashSet<>();
        Matcher matcher = WORD_PATTERN.matcher(text);

        while(matcher.find()){
            String word = matcher.group().toLowerCase(Locale.ROOT);
            words.add(word);
        }

        return words;
    }
}
