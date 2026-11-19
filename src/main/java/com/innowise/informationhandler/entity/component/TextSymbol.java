package com.innowise.informationhandler.entity.component;

import com.innowise.informationhandler.entity.TextComponentType;

import java.util.List;

public final class TextSymbol implements TextComponent {
    private final int codePoint;

    public TextSymbol(int codePoint) {
        if (!Character.isValidCodePoint(codePoint)) {
            throw new IllegalArgumentException(
                    "Invalid Unicode code point: " + codePoint
            );
        }
        this.codePoint = codePoint;
    }

    @Override
    public TextComponentType getType() {
        return TextComponentType.SYMBOL;
    }

    @Override
    public List<TextComponent> getChildren() {
        return List.of();
    }

    public int getCodePoint(){
        return codePoint;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof TextSymbol symbol)) {
            return false;
        }
        return codePoint == symbol.codePoint;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(codePoint);
    }

    @Override
    public String toString() {
        return "TextSymbol{codePoint=" + codePoint + '}';
    }
}
