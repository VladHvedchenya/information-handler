package com.innowise.informationhandler;

import com.innowise.informationhandler.entity.CommonWordResult;
import com.innowise.informationhandler.entity.LetterCountResult;
import com.innowise.informationhandler.entity.TextComponentType;
import com.innowise.informationhandler.entity.TextStatistics;
import com.innowise.informationhandler.entity.component.TextComponent;
import com.innowise.informationhandler.exception.TextReadException;
import com.innowise.informationhandler.parser.DocumentParser;
import com.innowise.informationhandler.parser.LexemeParser;
import com.innowise.informationhandler.parser.ParagraphParser;
import com.innowise.informationhandler.parser.SentenceParser;
import com.innowise.informationhandler.parser.TextParser;
import com.innowise.informationhandler.reader.TextFileReader;
import com.innowise.informationhandler.service.CommonWordService;
import com.innowise.informationhandler.service.LetterFrequencyService;
import com.innowise.informationhandler.service.LexemeSwapService;
import com.innowise.informationhandler.service.TextComponentCollector;
import com.innowise.informationhandler.service.TextProcessService;
import com.innowise.informationhandler.service.TextStatisticsService;
import com.innowise.informationhandler.renderer.TextRenderer;
import com.innowise.informationhandler.service.WordExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Application {

    private static final Logger LOGGER =
            LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        int exitCode = run(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    static int run(String[] args) {
        Path path;
        int letter;
        try {
            if (args.length > 2) {
                throw new IllegalArgumentException("Expected a file path and one letter");
            }
            path = args.length > 0 ? Path.of(args[0]) : Path.of("data", "adventure.txt");
            letter = args.length > 1 ? parseLetter(args[1]) : 'a';
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid arguments: {}", e.getMessage());
            System.err.println("Usage: Application [file-path] [letter]");
            return 2;
        }

        LOGGER.info("Application started");
        TextParser lexeme = new LexemeParser();
        TextParser sentence = new SentenceParser(lexeme);
        TextParser paragraph = new ParagraphParser(sentence);
        TextParser document = new DocumentParser(paragraph);
        TextFileReader reader = new TextFileReader();
        TextProcessService processService = new TextProcessService(reader, document);
        TextRenderer renderer = new TextRenderer();
        TextComponentCollector collector = new TextComponentCollector();
        WordExtractor extractor = new WordExtractor();
        CommonWordService wordService = new CommonWordService(renderer, extractor);
        LetterFrequencyService letterService = new LetterFrequencyService();
        LexemeSwapService swapService = new LexemeSwapService();
        TextStatisticsService statisticsService = new TextStatisticsService();

        try {
            TextComponent root = processService.processFile(path);
            List<TextComponent> sentences = collector.collect(root, TextComponentType.SENTENCE);
            Map<String, List<TextComponent>> words = wordService.buildIndex(sentences);
            CommonWordResult commonWords = wordService.findMostCommonWords(words);
            List<LetterCountResult> sortedSentences = letterService.sort(sentences, letter);
            TextComponent swappedText = swapService.swap(root);
            TextStatistics statistics = statisticsService.calculate(root);

            System.out.println("Исходный текст:");
            System.out.println(renderer.render(root));
            System.out.printf("Букв: %d, символов: %d%n", statistics.letterCount(), statistics.symbolCount());
            printCommonWords(commonWords, renderer);
            printSortedSentences(sortedSentences, letter, renderer);
            System.out.println("\nЗадание 3. Первая и последняя лексемы поменяны местами:");
            System.out.println(renderer.render(swappedText));
            LOGGER.info("Text processing completed");
            return 0;
        } catch (TextReadException e) {
            LOGGER.error("Cannot process file: {}", path, e);
            return 1;
        }
    }

    private static int parseLetter(String text) {
        if (text.codePointCount(0, text.length()) != 1) {
            throw new IllegalArgumentException("Expected exactly one letter");
        }
        int codePoint = text.codePointAt(0);
        if (!Character.isLetter(codePoint)) {
            throw new IllegalArgumentException("Expected a letter, not a digit or punctuation mark");
        }
        return codePoint;
    }

    private static void printCommonWords(CommonWordResult result, TextRenderer renderer) {
        System.out.println("\nЗадание 1. Максимальное количество предложений: " + result.maxSentenceCount());
        if (result.sentencesByWord().isEmpty()) {
            System.out.println("В тексте не найдено слов.");
        }
        for (Map.Entry<String, List<TextComponent>> entry : result.sentencesByWord().entrySet()) {
            System.out.println("Слово: " + entry.getKey());
            for (TextComponent sentence : entry.getValue()) {
                System.out.println("  " + renderer.render(sentence));
            }
        }
    }

    private static void printSortedSentences(
            List<LetterCountResult> results,
            int letter,
            TextRenderer renderer
    ) {
        String letterText = new String(Character.toChars(letter));
        System.out.println("\nЗадание 2. Предложения по количеству буквы '" + letterText + "':");
        for (LetterCountResult result : results) {
            System.out.printf("[%d] %s%n", result.count(), renderer.render(result.sentence()));
        }
    }
}
