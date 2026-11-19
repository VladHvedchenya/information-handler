package com.innowise.informationhandler;

import com.innowise.informationhandler.entity.LetterCountResult;
import com.innowise.informationhandler.entity.TextComponentType;
import com.innowise.informationhandler.entity.TextStatistics;
import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.entity.component.TextComposite;
import com.innowise.informationhandler.entity.component.TextSymbol;
import com.innowise.informationhandler.parser.LexemeParser;
import com.innowise.informationhandler.parser.SentenceParser;
import com.innowise.informationhandler.service.LetterFrequencyService;
import com.innowise.informationhandler.service.LexemeSwapService;
import com.innowise.informationhandler.service.TextStatisticsService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextOperationsTest {
    private final LetterFrequencyService letterService = new LetterFrequencyService();
    private final LexemeSwapService swapService = new LexemeSwapService();
    private final TextStatisticsService statisticsService = new TextStatisticsService();

    @Test
    void sortsByLetterIgnoringCaseAndPreservingTiesAndInputOrder() {
        List<TextComponent> sentences = TextFixtures.sentences("Aa! b. a? aa.");
        List<TextComponent> originalOrder = new ArrayList<>(sentences);
        List<LetterCountResult> results = letterService.sort(sentences, 'A');

        assertEquals(List.of(0L, 1L, 2L, 2L), results.stream().map(LetterCountResult::count).toList());
        assertEquals(List.of("b.", "a?", "Aa!", "aa."),
                results.stream().map(result -> TextFixtures.render(result.sentence())).toList());
        assertEquals(originalOrder, sentences);
    }

    @Test
    void handlesCyrillicSupplementaryLettersAndInvalidTargets() {
        assertEquals(2, letterService.sort(TextFixtures.sentences("Аа!"), 'А').getFirst().count());
        String deseretLetters = new String(Character.toChars(0x10400))
                + new String(Character.toChars(0x10428)) + ".";
        assertEquals(2, letterService.sort(TextFixtures.sentences(deseretLetters), 0x10400).getFirst().count());
        assertTrue(letterService.sort(List.of(), 'a').isEmpty());
        assertThrows(IllegalArgumentException.class, () -> letterService.sort(List.of(), '#'));
        assertThrows(IllegalArgumentException.class, () -> letterService.sort(List.of(), -1));
    }

    @Test
    void swapsLexemesWithoutChangingSeparatorsLengthOrOriginalTree() {
        String text = "  We  love\tJava.\n\nAlone!\nA 😀 verylong.";
        TextComponent root = TextFixtures.parse(text);
        TextComponent swapped = swapService.swap(root);

        assertEquals("  Java.  love\tWe\n\nAlone!\nverylong. 😀 A", TextFixtures.render(swapped));
        assertEquals(text, TextFixtures.render(root));
        List<TextComponent> before = TextFixtures.sentences(root);
        List<TextComponent> after = TextFixtures.sentences(swapped);
        assertEquals(before.size(), after.size());
        for (int i = 0; i < before.size(); i++) {
            String original = TextFixtures.render(before.get(i));
            String changed = TextFixtures.render(after.get(i));
            assertEquals(original.length(), changed.length());
            assertEquals(original.codePointCount(0, original.length()), changed.codePointCount(0, changed.length()));
            assertNotSame(before.get(i), after.get(i));
        }
        assertEquals(statisticsService.calculate(root), statisticsService.calculate(swapped));

        ((TextComposite) after.getFirst()).addChild(new TextSymbol('!'));
        assertEquals(text, TextFixtures.render(root));
    }

    @Test
    void acceptsZeroOrOneLexemeAndEmptyDocument() {
        TextComponent whitespace = new SentenceParser(new LexemeParser()).parse(" \t");
        assertEquals(" \t", TextFixtures.render(swapService.swap(whitespace)));
        assertEquals("Alone!", TextFixtures.render(swapService.swap(TextFixtures.parse("Alone!"))));
        assertEquals("", TextFixtures.render(swapService.swap(TextFixtures.parse(""))));
        assertEquals("", TextFixtures.render(swapService.swap(new TextComposite(TextComponentType.SENTENCE))));
    }

    @Test
    void countsLettersAndAllCodePointsIncludingSeparators() {
        assertEquals(new TextStatistics(0, 0), statisticsService.calculate(TextFixtures.parse("")));
        assertEquals(new TextStatistics(3, 8), statisticsService.calculate(TextFixtures.parse("Hi, Я! 😀")));
        assertEquals(new TextStatistics(5, 9), statisticsService.calculate(TextFixtures.parse("Hi.\n\nBye.")));
    }

    @Test
    void swappingTwiceRestoresOriginalStructure() {
        String text = "We  love\tJava.\n\nAlone!\nA 😀 verylong.";
        TextComponent root = TextFixtures.parse(text);
        TextComponent twiceSwapped = swapService.swap(swapService.swap(root));

        assertNotSame(root, twiceSwapped);
        assertEquals(root, twiceSwapped);
        assertEquals(root.hashCode(), twiceSwapped.hashCode());
        assertEquals(text, TextFixtures.render(twiceSwapped));
    }
}
