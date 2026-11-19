package com.innowise.informationhandler;

import com.innowise.informationhandler.entity.CommonWordResult;
import com.innowise.informationhandler.entity.LetterCountResult;
import com.innowise.informationhandler.entity.TextComponentType;
import com.innowise.informationhandler.entity.TextStatistics;
import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.entity.component.TextComposite;
import com.innowise.informationhandler.entity.component.TextSymbol;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EntityContractsTest {
    @Test
    void symbolsCompareCodePointsAndWorkInHashCollections() {
        TextSymbol first = new TextSymbol('A');
        TextSymbol second = new TextSymbol('A');
        TextSymbol third = new TextSymbol('A');
        assertValueEquality(first, second, third);
        assertNotEquals(first, new TextSymbol('a'));
        assertNotEquals(first, new TextSymbol('B'));
        assertNotEquals(first, new TextComposite(TextComponentType.LEXEME));

        Set<TextSymbol> symbols = new HashSet<>();
        symbols.add(first);
        symbols.add(second);
        assertEquals(1, symbols.size());
        assertTrue(symbols.contains(third));
        assertTrue(new TextSymbol(0x1F600).toString().contains("128512"));
    }

    @Test
    void compositesCompareTypeAndOrderedChildrenRecursively() {
        TextComponent first = TextFixtures.parse("Hi.\n\nBye!");
        TextComponent second = TextFixtures.parse("Hi.\n\nBye!");
        TextComponent third = TextFixtures.parse("Hi.\n\nBye!");
        assertValueEquality(first, second, third);

        assertNotEquals(first, TextFixtures.parse("Bye!\n\nHi."));
        assertNotEquals(first, TextFixtures.parse("Hi. Bye!"));
        assertNotEquals(first, TextFixtures.parse("Hi.\n\nBye?"));
        assertNotEquals(new TextComposite(TextComponentType.TEXT), new TextComposite(TextComponentType.PARAGRAPH));

        Map<TextComponent, String> descriptions = new HashMap<>();
        descriptions.put(first, "same text");
        assertEquals("same text", descriptions.get(second));
    }

    @Test
    void toStringDescribesStructureWithoutDependingOnRenderer() {
        TextComposite lexeme = new TextComposite(TextComponentType.LEXEME);
        lexeme.addChild(new TextSymbol('A'));
        String description = lexeme.toString();

        assertTrue(description.contains("TextComposite"));
        assertTrue(description.contains("LEXEME"));
        assertTrue(description.contains("children"));
        assertTrue(description.contains("TextSymbol"));
        assertTrue(description.contains("65"));
    }

    @Test
    void cycleChecksUseIdentityRatherThanStructuralEquality() {
        TextComposite first = new TextComposite(TextComponentType.TEXT);
        TextComposite second = new TextComposite(TextComponentType.TEXT);
        assertEquals(first, second);

        first.addChild(second);
        assertSame(second, first.getChildren().getFirst());
        assertThrows(IllegalArgumentException.class, () -> first.addChild(first));
        assertThrows(IllegalArgumentException.class, () -> second.addChild(first));
        assertTrue(second.getChildren().isEmpty());
    }

    @Test
    void recordsUseTheirValuesForEqualityHashCodeAndToString() {
        assertValueEquality(new TextStatistics(2, 3), new TextStatistics(2, 3), new TextStatistics(2, 3));
        assertNotEquals(new TextStatistics(2, 3), new TextStatistics(2, 4));
        assertTrue(new TextStatistics(2, 3).toString().contains("letterCount=2"));

        TextComponent first = TextFixtures.sentences("Hi.").getFirst();
        TextComponent second = TextFixtures.sentences("Hi.").getFirst();
        TextComponent third = TextFixtures.sentences("Hi.").getFirst();
        assertValueEquality(new LetterCountResult(first, 1), new LetterCountResult(second, 1),
                new LetterCountResult(third, 1));
        assertNotEquals(new LetterCountResult(first, 1), new LetterCountResult(first, 2));
        assertTrue(new LetterCountResult(first, 1).toString().contains("count=1"));

        CommonWordResult result = new CommonWordResult(1, Map.of("hi", List.of(first)));
        assertValueEquality(result,
                new CommonWordResult(1, Map.of("hi", List.of(second))),
                new CommonWordResult(1, Map.of("hi", List.of(third))));
        assertNotEquals(result, new CommonWordResult(2, Map.of("hi", List.of(first))));
        assertNotEquals(result, new CommonWordResult(1, Map.of("bye", List.of(first))));
        assertTrue(result.toString().contains("maxSentenceCount=1"));
        assertTrue(result.toString().contains("sentencesByWord"));
    }

    @Test
    void commonWordResultProtectsItsMapAndListsFromExternalChanges() {
        TextComponent sentence = TextFixtures.sentences("Hi.").getFirst();
        List<TextComponent> sentences = new ArrayList<>(List.of(sentence));
        Map<String, List<TextComponent>> source = new HashMap<>();
        source.put("hi", sentences);
        CommonWordResult result = new CommonWordResult(1, source);

        sentences.clear();
        source.clear();
        assertEquals(Set.of("hi"), result.sentencesByWord().keySet());
        assertEquals(1, result.sentencesByWord().get("hi").size());
        assertSame(sentence, result.sentencesByWord().get("hi").getFirst());
        assertThrows(UnsupportedOperationException.class, () -> result.sentencesByWord().clear());
        assertThrows(UnsupportedOperationException.class, () -> result.sentencesByWord().get("hi").clear());
    }

    private static void assertValueEquality(Object first, Object second, Object third) {
        assertEquals(first, first);
        assertEquals(first, second);
        assertEquals(second, first);
        assertEquals(second, third);
        assertEquals(first, third);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(second.hashCode(), third.hashCode());
        assertNotEquals(first, null);
        assertNotEquals(first, new Object());
    }
}
