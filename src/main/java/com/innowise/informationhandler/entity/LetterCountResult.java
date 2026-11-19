package com.innowise.informationhandler.entity;

import com.innowise.informationhandler.entity.component.TextComponent;

public record LetterCountResult(TextComponent sentence, long count) {
}
