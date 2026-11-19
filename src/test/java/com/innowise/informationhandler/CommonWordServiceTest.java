package com.innowise.informationhandler;

import com.innowise.informationhandler.entity.CommonWordResult;
import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.renderer.TextRenderer;
import com.innowise.informationhandler.service.CommonWordService;
import com.innowise.informationhandler.service.WordExtractor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CommonWordServiceTest {
    private final WordExtractor extractor = new WordExtractor();
    private final CommonWordService service = new CommonWordService(new TextRenderer(), extractor);

    @Test
    void extractsUniqueWordsWithPunctuationAndInternalHyphensOrApostrophes() {
        assertEquals(Set.of("cats", "don't", "run", "more-or-less", "rock’n’roll", "кот"),
                extractor.extractUniqueWords("Cats, cats! Don't run; more-or-less. rock’n’roll Кот, КОТ! 13"));
        assertEquals(Set.of(), extractor.extractUniqueWords("!? 13"));
        assertEquals(Set.of("new"), extractor.extractUniqueWords("NEW"));
        assertThrows(NullPointerException.class, () -> extractor.extractUniqueWords(null));
    }

    @Test
    void normalizesWordsIndependentlyOfDefaultLocale() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals(Set.of("inside"), extractor.extractUniqueWords("INSIDE"));
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    void countsEachWordOncePerSentenceAndReturnsAllTies() {
        List<TextComponent> sentences = TextFixtures.sentences("Cats cats sleep. Dogs sleep. Cats play.");
        Map<String, List<TextComponent>> index = service.buildIndex(sentences);
        CommonWordResult result = service.findMostCommonWords(index);

        assertEquals(2, result.maxSentenceCount());
        assertEquals(Set.of("cats", "sleep"), result.sentencesByWord().keySet());
        assertEquals(List.of(sentences.get(0), sentences.get(2)), result.sentencesByWord().get("cats"));
        assertEquals(List.of(sentences.get(0), sentences.get(1)), result.sentencesByWord().get("sleep"));
        assertEquals(4, index.size());
        assertEquals(2, index.get("cats").size());
    }

    @Test
    void identicalSentenceTextsRemainSeparateOccurrences() {
        List<TextComponent> sentences = TextFixtures.sentences("Cats sleep. Cats sleep.");
        CommonWordResult result = service.findMostCommonWords(service.buildIndex(sentences));
        assertEquals(2, result.maxSentenceCount());
        assertEquals(2, result.sentencesByWord().get("cats").size());
        assertEquals(sentences.get(0), sentences.get(1));
        assertNotSame(sentences.get(0), sentences.get(1));
        assertSame(sentences.get(0), result.sentencesByWord().get("cats").get(0));
        assertSame(sentences.get(1), result.sentencesByWord().get("cats").get(1));
    }

    @Test
    void handlesEmptyIndexAndWordsThatOccurOnlyOnce() {
        CommonWordResult empty = service.findMostCommonWords(service.buildIndex(List.of()));
        assertEquals(0, empty.maxSentenceCount());
        assertTrue(empty.sentencesByWord().isEmpty());

        CommonWordResult unique = service.findMostCommonWords(service.buildIndex(TextFixtures.sentences("Cat. Dog.")));
        assertEquals(1, unique.maxSentenceCount());
        assertEquals(Set.of("cat", "dog"), unique.sentencesByWord().keySet());
        assertTrue(service.buildIndex(TextFixtures.sentences("13! ...")).isEmpty());
    }
}
