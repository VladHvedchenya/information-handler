package com.innowise.informationhandler.service;

import com.innowise.informationhandler.entity.CommonWordResult;
import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.renderer.TextRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CommonWordService {
    private final TextRenderer renderer;
    private final WordExtractor extractor;

    public CommonWordService(TextRenderer renderer, WordExtractor extractor) {
        this.renderer = Objects.requireNonNull(renderer);
        this.extractor = Objects.requireNonNull(extractor);
    }

    public Map<String, List<TextComponent>> buildIndex(List<TextComponent> sentences) {
        Objects.requireNonNull(sentences);
        Map<String, List<TextComponent>> index = new HashMap<>();

        for (TextComponent sentence : sentences) {
            String sentenceText = renderer.render(sentence);
            Set<String> words = extractor.extractUniqueWords(sentenceText);

            for (String word : words) {
                index.computeIfAbsent(word, key -> new ArrayList<>()).add(sentence);
            }
        }

        return index;
    }

    public CommonWordResult findMostCommonWords(Map<String, List<TextComponent>> index) {
        Objects.requireNonNull(index);
        int maxSentenceCount = 0;

        for (List<TextComponent> sentences : index.values()) {
            maxSentenceCount = Math.max(maxSentenceCount, sentences.size());
        }

        Map<String, List<TextComponent>> matches = new HashMap<>();

        for (Map.Entry<String, List<TextComponent>> entry : index.entrySet()) {
            if (entry.getValue().size() == maxSentenceCount) {
                matches.put(entry.getKey(), entry.getValue());
            }
        }
        return new CommonWordResult(maxSentenceCount, matches);
    }
}
