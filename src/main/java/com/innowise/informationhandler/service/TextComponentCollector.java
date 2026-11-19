package com.innowise.informationhandler.service;

import com.innowise.informationhandler.entity.TextComponentType;
import com.innowise.informationhandler.entity.component.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TextComponentCollector {
    public List<TextComponent> collect(TextComponent root, TextComponentType targetType){
        Objects.requireNonNull(root);
        Objects.requireNonNull(targetType);
        List<TextComponent> result = new ArrayList<>();
        collect(root, targetType, result);
        return result;
    }

    private void collect(TextComponent root, TextComponentType targetType, List<TextComponent> result){
        if(root.getType().equals(targetType)){
            result.add(root);
        }

        for(var child : root.getChildren()){
            collect(child, targetType, result);
        }
    }
}
