package com.innowise.informationhandler.reader;

import com.innowise.informationhandler.exception.TextReadException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class TextFileReader {
    public String read(Path path) throws TextReadException {
        Objects.requireNonNull(path);

        try{
            return Files.readString(path, StandardCharsets.UTF_8);
        }
        catch (IOException e){
            throw new TextReadException("Cannot read text file: " + path, e);
        }
    }
}
