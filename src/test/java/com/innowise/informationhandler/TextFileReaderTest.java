package com.innowise.informationhandler;

import com.innowise.informationhandler.exception.TextReadException;
import com.innowise.informationhandler.reader.TextFileReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TextFileReaderTest {
    @TempDir
    Path directory;

    private final TextFileReader reader = new TextFileReader();

    @Test
    void readsUtf8AndPreservesLineEndings() throws Exception {
        String text = "Привет 😀!\r\n\nBye.";
        Path path = directory.resolve("text.txt");
        Files.writeString(path, text, StandardCharsets.UTF_8);
        assertEquals(text, reader.read(path));
    }

    @Test
    void acceptsEmptyFile() throws Exception {
        Path path = Files.createFile(directory.resolve("empty.txt"));
        assertEquals("", reader.read(path));
    }

    @Test
    void preservesCauseAndPathForMissingFile() {
        Path missing = directory.resolve("missing.txt");
        TextReadException exception = assertThrows(TextReadException.class, () -> reader.read(missing));
        assertInstanceOf(IOException.class, exception.getCause());
        assertTrue(exception.getMessage().contains(missing.toString()));
    }

    @Test
    void rejectsMalformedUtf8AndNullPath() throws Exception {
        Path path = directory.resolve("malformed.txt");
        Files.write(path, new byte[]{(byte) 0xC3, 0x28});
        TextReadException exception = assertThrows(TextReadException.class, () -> reader.read(path));
        assertInstanceOf(IOException.class, exception.getCause());
        assertThrows(NullPointerException.class, () -> reader.read(null));
    }
}
