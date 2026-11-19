package com.innowise.informationhandler.entity;

import com.innowise.informationhandler.entity.component.TextComponent;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record CommonWordResult(
        int maxSentenceCount,
        Map<String, List<TextComponent>> sentencesByWord
) {
    public CommonWordResult {
        Map<String, List<TextComponent>> copy = new HashMap<>();
        sentencesByWord.forEach((word, sentences) -> copy.put(word, List.copyOf(sentences)));
        sentencesByWord = Collections.unmodifiableMap(copy);
    }
}
