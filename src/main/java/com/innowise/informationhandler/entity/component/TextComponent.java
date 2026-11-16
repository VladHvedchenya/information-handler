package com.innowise.informationhandler.entity.component;

import com.innowise.informationhandler.entity.TextComponentType;

import java.util.List;

public interface TextComponent {
    TextComponentType getType();

    List<TextComponent> getChildren();
}
