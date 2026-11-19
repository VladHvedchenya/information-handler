package com.innowise.informationhandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {
    @TempDir
    Path directory;

    @Test
    void runsAllTasksForDefaultArguments() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream previous = System.out;
        try (PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            assertEquals(0, Application.run(new String[0]));
        } finally {
            System.setOut(previous);
        }
        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("At dusk, Mara reached the abandoned lighthouse."));
        assertTrue(text.contains("Задание 1. Максимальное количество предложений: 8"));
        assertTrue(text.contains("Слово: the"));
        assertTrue(text.contains("Задание 2."));
        assertTrue(text.contains("Задание 3."));
    }

    @Test
    void acceptsEmptyFileAndExplicitLetter() throws Exception {
        Path path = Files.createFile(directory.resolve("empty.txt"));
        assertEquals(0, Application.run(new String[]{path.toString(), "Я"}));
    }

    @Test
    void returnsReadErrorForMissingFile() {
        assertEquals(1, Application.run(new String[]{directory.resolve("missing.txt").toString(), "a"}));
    }

    @Test
    void rejectsInvalidArgumentsBeforeReadingFile() {
        String missingPath = directory.resolve("missing.txt").toString();
        for (String letter : new String[]{"ab", "", "1", "😀"}) {
            assertEquals(2, Application.run(new String[]{missingPath, letter}));
        }
        assertEquals(2, Application.run(new String[]{missingPath, "a", "extra"}));
        assertEquals(2, Application.run(new String[]{"\0"}));
    }
}
