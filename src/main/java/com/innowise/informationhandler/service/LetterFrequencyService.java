package com.innowise.informationhandler.service;

import com.innowise.informationhandler.entity.LetterCountResult;
import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.entity.component.TextSymbol;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class LetterFrequencyService {

    public List<LetterCountResult> sort(List<TextComponent> sentences, int letterCodePoint) {
        Objects.requireNonNull(sentences);

        if (!Character.isValidCodePoint(letterCodePoint) || !Character.isLetter(letterCodePoint)) {
            throw new IllegalArgumentException("Expected one letter");
        }

        int normalizedLetter = Character.toLowerCase(letterCodePoint);
        List<LetterCountResult> results = new ArrayList<>();

        for (TextComponent sentence : sentences) {
            long count = countLetter(Objects.requireNonNull(sentence), normalizedLetter);
            results.add(new LetterCountResult(sentence, count));
        }

        results.sort(Comparator.comparingLong(LetterCountResult::count));
        return results;
    }

    private long countLetter(TextComponent component, int normalizedLetter) {
        if (component instanceof TextSymbol symbol) {
            return Character.toLowerCase(symbol.getCodePoint()) == normalizedLetter ? 1 : 0;
        }

        long count = 0;

        for (TextComponent child : component.getChildren()) {
            count += countLetter(child, normalizedLetter);
        }

        return count;
    }
}
