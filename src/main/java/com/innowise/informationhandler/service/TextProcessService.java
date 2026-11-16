package com.innowise.informationhandler.service;

import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.exception.TextReadException;
import com.innowise.informationhandler.parser.TextParser;
import com.innowise.informationhandler.reader.TextFileReader;

import java.nio.file.Path;
import java.util.Objects;

public class TextProcessService {
    private final TextFileReader textFileReader;
    private final TextParser textParser;

    public TextProcessService(TextFileReader reader, TextParser textParser){
        this.textFileReader = Objects.requireNonNull(reader);
        this.textParser = Objects.requireNonNull(textParser);
    }

    public TextComponent processFile(Path path) throws TextReadException {
        String text = textFileReader.read(path);
        return textParser.parse(text);
    }
}
