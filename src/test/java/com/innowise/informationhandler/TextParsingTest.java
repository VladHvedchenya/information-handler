package com.innowise.informationhandler;

import com.innowise.informationhandler.entity.TextComponentType;
import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.entity.component.TextComposite;
import com.innowise.informationhandler.entity.component.TextSymbol;
import com.innowise.informationhandler.reader.TextFileReader;
import com.innowise.informationhandler.service.TextComponentCollector;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextParsingTest {
    @Test
    void restoresTextAndPreservesParagraphBoundaries() {
        String[] inputs = {
                "", "Hi.\nBye.", "Hi.\n\nBye.", "\n\nHi.", "Hi.\n\n",
                "Hi.\n\n\nBye.", "Hi.\r\n \t\r\nBye.", "\n\n",
                "  Привет 😀!\t\n\nJava работает.  "
        };
        int[] paragraphCounts = {0, 1, 2, 1, 1, 2, 2, 0, 2};
        TextComponentCollector collector = new TextComponentCollector();

        for (int i = 0; i < inputs.length; i++) {
            TextComponent root = TextFixtures.parse(inputs[i]);
            assertEquals(inputs[i], TextFixtures.render(root), "Round-trip case " + i);
            assertEquals(paragraphCounts[i], collector.collect(root, TextComponentType.PARAGRAPH).size());
        }
    }

    @Test
    void collectsSentencesInOrderWithoutAccumulatingPreviousResults() {
        TextComponent root = TextFixtures.parse("Hi.\nWe love Java! Bye? Last fragment");
        List<TextComponent> sentences = TextFixtures.sentences(root);
        assertEquals(List.of("Hi.", "We love Java!", "Bye?", "Last fragment"),
                sentences.stream().map(TextFixtures::render).toList());
        assertEquals(4, TextFixtures.sentences(root).size());
    }

    @Test
    void parsesAdventureFixture() throws Exception {
        String original = new TextFileReader().read(Path.of("data", "adventure.txt"));
        TextComponent root = TextFixtures.parse(original);
        assertEquals(original, TextFixtures.render(root));
        assertEquals(10, TextFixtures.sentences(root).size());
        assertEquals(3, new TextComponentCollector().collect(root, TextComponentType.PARAGRAPH).size());
    }

    @Test
    void rejectsCyclesAndProtectsChildrenList() {
        TextComposite root = new TextComposite(TextComponentType.TEXT);
        TextComposite child = new TextComposite(TextComponentType.PARAGRAPH);
        root.addChild(child);

        assertThrows(IllegalArgumentException.class, () -> root.addChild(root));
        assertThrows(IllegalArgumentException.class, () -> child.addChild(root));
        assertThrows(UnsupportedOperationException.class, () -> root.getChildren().clear());
        assertEquals(List.of(child), root.getChildren());
        assertTrue(child.getChildren().isEmpty());
    }

    @Test
    void collectsNestedMatchesAndCountsUnicodeAsOneLeaf() {
        TextComposite outer = new TextComposite(TextComponentType.TEXT);
        outer.addChild(new TextComposite(TextComponentType.TEXT));
        assertEquals(2, new TextComponentCollector().collect(outer, TextComponentType.TEXT).size());
        assertEquals("😀", TextFixtures.render(new TextSymbol(0x1F600)));
    }

    @Test
    void separatesSinglePunctuationMarkBeforeNextSentence() {
        String[] inputs = {". Hi.", "? Go!", "! End?", "!! Go!"};
        List<List<String>> expected = List.of(
                List.of(".", "Hi."),
                List.of("?", "Go!"),
                List.of("!", "End?"),
                List.of("!!", "Go!")
        );
        for (int i = 0; i < inputs.length; i++) {
            TextComponent root = TextFixtures.parse(inputs[i]);
            assertEquals(expected.get(i), TextFixtures.sentences(root).stream().map(TextFixtures::render).toList());
            assertEquals(inputs[i], TextFixtures.render(root));
        }
        assertTrue(TextFixtures.sentences(" \t\n").isEmpty());
    }
}
