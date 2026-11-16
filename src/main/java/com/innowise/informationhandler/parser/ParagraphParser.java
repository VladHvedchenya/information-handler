package com.innowise.informationhandler.parser;

import com.innowise.informationhandler.entity.TextComponentType;
import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.entity.component.TextComposite;
import com.innowise.informationhandler.entity.component.TextSymbol;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParagraphParser implements TextParser {
    private final TextParser sentenceParser;
    private static final String SENTENCE_REGEX = "(?sU)(?<sentence>\\S.*?(?:[.!?]+(?=\\s|\\z)|\\z))|\\s";

    private static final Pattern SENTENCE_PATTERN = Pattern.compile(SENTENCE_REGEX);


    public ParagraphParser(TextParser sentenceParser) {
        this.sentenceParser = Objects.requireNonNull(sentenceParser);
    }

    @Override
    public TextComponent parse(String text) {
        Objects.requireNonNull(text);
        TextComposite paragraph = new TextComposite(TextComponentType.PARAGRAPH);
        Matcher matcher = SENTENCE_PATTERN.matcher(text);

        while(matcher.find()){
            String sentence = matcher.group("sentence");

            if(sentence != null){
                paragraph.addChild(sentenceParser.parse(sentence));
            }
            else {
                paragraph.addChild(new TextSymbol(matcher.group().codePointAt(0)));
            }
        }

        return paragraph;
    }
}
