package com.innowise.informationhandler.parser;

import com.innowise.informationhandler.entity.TextComponentType;
import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.entity.component.TextComposite;
import com.innowise.informationhandler.entity.component.TextSymbol;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexemeParser implements TextParser{
    private static final String SYMBOL_REGEX = "(?s).";
    private static final Pattern SYMBOL_PATTERN = Pattern.compile(SYMBOL_REGEX);

    @Override
    public TextComponent parse(String text) {
        Objects.requireNonNull(text);
        TextComposite lexeme = new TextComposite(TextComponentType.LEXEME);
        Matcher matcher = SYMBOL_PATTERN.matcher(text);

        while(matcher.find()){
            TextSymbol symbol = new TextSymbol(matcher.group().codePointAt(0));
            lexeme.addChild(symbol);
        }

        return lexeme;
    }
}
