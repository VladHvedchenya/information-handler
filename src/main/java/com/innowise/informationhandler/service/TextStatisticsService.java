package com.innowise.informationhandler.service;

import com.innowise.informationhandler.entity.TextStatistics;
import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.entity.component.TextSymbol;

import java.util.Objects;

public class TextStatisticsService {

    public TextStatistics calculate(TextComponent component) {
        Objects.requireNonNull(component);

        if (component instanceof TextSymbol symbol) {
            long letters = Character.isLetter(symbol.getCodePoint()) ? 1 : 0;
            return new TextStatistics(letters, 1);
        }

        long letters = 0;
        long symbols = 0;

        for (TextComponent child : component.getChildren()) {
            TextStatistics statistics = calculate(child);
            letters += statistics.letterCount();
            symbols += statistics.symbolCount();
        }
        return new TextStatistics(letters, symbols);
    }
}
