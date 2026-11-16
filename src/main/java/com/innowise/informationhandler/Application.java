package com.innowise.informationhandler;

import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.exception.TextReadException;
import com.innowise.informationhandler.parser.*;
import com.innowise.informationhandler.reader.TextFileReader;
import com.innowise.informationhandler.service.TextProcessService;
import com.innowise.informationhandler.renderer.TextRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class Application {

    private static final Logger LOGGER =
            LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        LOGGER.info("Application started");
        TextParser lexeme = new LexemeParser();
        TextParser sentence = new SentenceParser(lexeme);
        TextParser paragraph = new ParagraphParser(sentence);
        TextParser document = new DocumentParser(paragraph);
        TextFileReader reader = new TextFileReader();
        TextProcessService processService = new TextProcessService(reader, document);
        TextRenderer renderer = new TextRenderer();
        Path path = Path.of("data", "adventure.txt");

        try{
            TextComponent root = processService.processFile(path);
            String restoredText = renderer.render(root);
            System.out.print(restoredText);
            LOGGER.info("Text processing completed");
        }
        catch (TextReadException e){
            LOGGER.error("Cannot process file: {}", path, e);
            System.exit(1);
        }
    }
}
