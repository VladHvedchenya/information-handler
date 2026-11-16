package com.innowise.informationhandler.parser;

import com.innowise.informationhandler.entity.TextComponentType;
import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.entity.component.TextComposite;
import com.innowise.informationhandler.entity.component.TextSymbol;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentParser implements TextParser{
    private final TextParser paragraphParser;
    private static final String PARAGRAPH_SEPARATOR_REGEX = "\\r?\\n[\\t ]*\\r?\\n(?:[\\t ]*\\r?\\n)*";

    private static final Pattern PARAGRAPH_SEPARATOR_PATTERN = Pattern.compile(PARAGRAPH_SEPARATOR_REGEX);

    public DocumentParser(TextParser paragraphParser){
        this.paragraphParser = Objects.requireNonNull(paragraphParser);
    }

    @Override
    public TextComponent parse(String text) {
        Objects.requireNonNull(text);
        TextComposite textComposite = new TextComposite(TextComponentType.TEXT);
        Matcher matcher = PARAGRAPH_SEPARATOR_PATTERN.matcher(text);
        int startPosition = 0;

        while(matcher.find()){
            String foundMatch = matcher.group();
            String realText = text.substring(startPosition, matcher.start());

            if(!realText.isEmpty()){
                textComposite.addChild(paragraphParser.parse(realText));
            }

            foundMatch.codePoints().forEach(codePoint -> textComposite.addChild(new TextSymbol(codePoint)));
            startPosition = matcher.end();
        }

        if (startPosition < text.length()){
            textComposite.addChild(paragraphParser.parse(text.substring(startPosition)));
        }

        return textComposite;
    }
}