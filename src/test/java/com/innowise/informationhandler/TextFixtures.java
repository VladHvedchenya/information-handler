package com.innowise.informationhandler;

import com.innowise.informationhandler.entity.TextComponentType;
import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.parser.DocumentParser;
import com.innowise.informationhandler.parser.LexemeParser;
import com.innowise.informationhandler.parser.ParagraphParser;
import com.innowise.informationhandler.parser.SentenceParser;
import com.innowise.informationhandler.renderer.TextRenderer;
import com.innowise.informationhandler.service.TextComponentCollector;

import java.util.List;

final class TextFixtures {
    private TextFixtures() {
    }

    static TextComponent parse(String text) {
        return new DocumentParser(new ParagraphParser(new SentenceParser(new LexemeParser()))).parse(text);
    }

    static List<TextComponent> sentences(TextComponent root) {
        return new TextComponentCollector().collect(root, TextComponentType.SENTENCE);
    }

    static List<TextComponent> sentences(String text) {
        return sentences(parse(text));
    }

    static String render(TextComponent component) {
        return new TextRenderer().render(component);
    }
}
