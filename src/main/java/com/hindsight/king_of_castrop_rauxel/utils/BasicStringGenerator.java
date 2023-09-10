package com.hindsight.king_of_castrop_rauxel.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.hindsight.king_of_castrop_rauxel.settings.LocationComponent.*;

@Slf4j
@UtilityClass
public class BasicStringGenerator {
  private static final String FOLDER = "names" + System.getProperty("file.separator");
  private static final String SUFFIX_MIDDLE = "--middle";
  private static final String[] SUFFIXES = new String[] {"--start", SUFFIX_MIDDLE, "--end"};
  private static final String FILE_EXTENSION = ".txt";
  public static final String NONDESCRIPT = "Nondescript ";
  private final Random random = new Random();
  public static final String HYPHEN = "-";
  public static final String PLACEHOLDER_PARENT_NAME = "%P";
  public static final String PLACEHOLDER_OWNER_NAME = "%O";

  public static String generate(Class<?> clazz) {
    return generate(null, null, clazz);
  }

  public static String generate(AmenityType type, Class<?> clazz) {
    return generate(type, null, clazz);
  }

  public static String generate(String parentName, Class<?> clazz) {
    return generate(null, parentName, clazz);
  }

  public static String generate(AmenityType type, String parentName, Class<?> clazz) {
    var typeName = type == null ? "" : HYPHEN + type.name();
    var pathName = "%s%s".formatted(getClassName(clazz), typeName);
    log.debug("Generating string for path '{}'", pathName);
    List<String> words = new ArrayList<>();

    // Loop through files with suffixes
    for (String suffix : SUFFIXES) {
      var result = readWordsFromFile(pathName + suffix);
      if (!result.isEmpty() && (!suffix.equals(SUFFIX_MIDDLE) || random.nextInt(3) == 0)) {
        words.add(getRandomWord(result));
      }
    }

    // Loop through file without suffix
    if (words.isEmpty()) {
      var result = readWordsFromFile(pathName);
      if (result.isEmpty()) {
        log.warn("No input files found for class '{}'", pathName);
        words.add(NONDESCRIPT + pathName.toLowerCase());
      } else {
        words.add(getRandomWord(result).trim());
      }
    }

    // Process and return words
    processingNewWordPlaceholders(words, pathName);
    processParentNamePlaceholders(words, parentName);
    return String.join("", words);
  }

  private String getClassName(Class<?> clazz) {
    return clazz.getSimpleName();
  }

  private static List<String> readWordsFromFile(String filename) {
    InputStream inputStream =
        BasicStringGenerator.class
            .getClassLoader()
            .getResourceAsStream(FOLDER + filename + FILE_EXTENSION);
    if (inputStream != null) {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        return reader.lines().map(String::trim).toList();
      } catch (IOException e) {
        log.warn("File '{}' not found", filename);
      }
    }
    return new ArrayList<>();
  }

  private static String getRandomWord(List<String> words) {
    int randomIndex = random.nextInt(words.size());
    return words.get(randomIndex);
  }

  private static void processingNewWordPlaceholders(List<String> words, String pathName) {
    if (words.get(0).startsWith(HYPHEN)) {
      var result = readWordsFromFile(pathName + words.get(0));
      if (result.isEmpty()) {
        log.warn("No input files found for path '{}'", pathName + words.get(0));
        words.set(0, NONDESCRIPT + pathName.toLowerCase());
      } else {
        var randomWord = getRandomWord(result).trim();
        log.info("Replacing '{}' with random word '{}'", words.get(0), randomWord);
        words.set(0, randomWord);
      }
    }
  }

  private static void processParentNamePlaceholders(List<String> words, String parentName) {
    for (String word : words) {
      if (word.contains(PLACEHOLDER_OWNER_NAME) && parentName != null) {
        log.info("Replacing '{}' with class name '{}'", word, parentName);
        words.set(words.indexOf(word), word.replace(PLACEHOLDER_OWNER_NAME, parentName));
      }
    }
  }

  public static String generatePlaceholder(String className) {
    log.info("Using fallback generator is being used '{}'", className);
    int length = 5;
    boolean useLetters = true;
    boolean useNumbers = false;
    return "%s %s"
        .formatted(
            className, RandomStringUtils.random(length, useLetters, useNumbers).toUpperCase());
  }
}
