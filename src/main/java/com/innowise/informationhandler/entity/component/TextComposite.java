package com.innowise.informationhandler.entity.component;

import com.innowise.informationhandler.entity.TextComponentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TextComposite implements TextComponent {
    private final TextComponentType type;
    private final List<TextComponent> children;

    public TextComposite(TextComponentType type){
        this.type = Objects.requireNonNull(type);

        if(type == TextComponentType.SYMBOL){
            throw new IllegalArgumentException("Composite must not have SYMBOL type");
        }

        this.children = new ArrayList<>();
    }

    @Override
    public TextComponentType getType() {
        return this.type;
    }

    @Override
    public List<TextComponent> getChildren() {
        return List.copyOf(children);
    }

    public void addChild(TextComponent child){
        Objects.requireNonNull(child);

        if (!containsComponent(child, this)) {
            children.add(child);
        }
        else {
            throw new IllegalArgumentException("Adding this component would create a cycle.");
        }
    }

    private boolean containsComponent(TextComponent root, TextComponent target){
        if (root == target)
            return true;

        for (TextComponent child : root.getChildren()) {
            if (containsComponent(child, target)) {
                return true;
            }
        }

        return false;
    }
}
