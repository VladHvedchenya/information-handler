package com.innowise.informationhandler.parser;

import com.innowise.informationhandler.entity.TextComponentType;
import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.entity.component.TextComposite;
import com.innowise.informationhandler.entity.component.TextSymbol;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentenceParser implements TextParser{
    private final TextParser lexemeParser;

    private static final String LEXEME_REGEX = "(?U)(?<lexeme>\\S+)|\\s";

    private static final Pattern LEXEME_PATTERN = Pattern.compile(LEXEME_REGEX);

    public SentenceParser(TextParser lexemeParser){
        this.lexemeParser = Objects.requireNonNull(lexemeParser);
    }

    @Override
    public TextComponent parse(String text) {
        Objects.requireNonNull(text);
        TextComposite sentence = new TextComposite(TextComponentType.SENTENCE);
        Matcher matcher = LEXEME_PATTERN.matcher(text);

        while(matcher.find()){
            String lexeme= matcher.group("lexeme");

            if(lexeme != null){
                sentence.addChild(lexemeParser.parse(lexeme));
            }
            else {
                sentence.addChild(new TextSymbol(matcher.group().codePointAt(0)));
            }
        }

        return sentence;
    }
}
