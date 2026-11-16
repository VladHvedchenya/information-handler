package com.innowise.informationhandler.renderer;

import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.entity.component.TextSymbol;

import java.util.Objects;

public class TextRenderer {
    public String render(TextComponent textComponent){
        Objects.requireNonNull(textComponent);
        StringBuilder sb = new StringBuilder();
        append(sb, textComponent);
        return sb.toString();
    }

    private void append(StringBuilder sb, TextComponent textComponent){
        if(textComponent instanceof TextSymbol symbol){
            sb.appendCodePoint(symbol.getCodePoint());
        }
        else{
            textComponent.getChildren().forEach(child -> append(sb, child));
        }
    }
}
