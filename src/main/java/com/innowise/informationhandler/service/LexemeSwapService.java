package com.innowise.informationhandler.service;

import com.innowise.informationhandler.entity.TextComponentType;
import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.entity.component.TextComposite;
import com.innowise.informationhandler.entity.component.TextSymbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LexemeSwapService {

    public TextComponent swap(TextComponent root) {
        Objects.requireNonNull(root);

        if (root instanceof TextSymbol) {
            return root;
        }

        List<TextComponent> children = new ArrayList<>(root.getChildren());

        if (root.getType() == TextComponentType.SENTENCE) {
            swapLexemes(children);
        }

        TextComposite copy = new TextComposite(root.getType());

        for (TextComponent child : children) {
            copy.addChild(swap(child));
        }
        return copy;
    }

    private void swapLexemes(List<TextComponent> children) {
        int first = -1;
        int last = -1;

        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getType() == TextComponentType.LEXEME) {
                if (first == -1) {
                    first = i;
                }

                last = i;
            }
        }

        if (first != -1 && first != last) {
            Collections.swap(children, first, last);
        }
    }
}
