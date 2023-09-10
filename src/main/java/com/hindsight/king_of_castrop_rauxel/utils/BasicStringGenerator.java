package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
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
  public static final String FIRST_NAME = "FIRST_NAME";
  public static final String LAST_NAME = "LAST_NAME";

  public static String locationNameFrom(Class<?> clazz) {
    return locationNameFrom(null, null, null, null, clazz);
  }

  public static String locationNameFrom(AmenityType type, Class<?> clazz) {
    return locationNameFrom(type, null, null, null, clazz);
  }

  public static String locationNameFrom(String parentName, Class<?> clazz) {
    return locationNameFrom(null, null, parentName, null, clazz);
  }

  public static String locationNameFrom(
      AmenityType type, Size parentSize, String parentName, List<Npc> inhabitants, Class<?> clazz) {
    var withType = type == null ? "" : HYPHEN + type.name().toUpperCase();
    var withSize = parentSize == null ? "" : HYPHEN + parentSize.name().toUpperCase();
    var className = clazz.getSimpleName().toUpperCase();
    var pathNameWithTypeAndSize = "%s%s%s".formatted(className, withType, withSize);
    var pathNameWithTypeOnly = "%s%s".formatted(className, withType);
    log.debug(
        "Attempting to generate string for class '{}' with '{}' or '{}'",
        className,
        pathNameWithTypeAndSize != null ? pathNameWithTypeAndSize : "null",
        pathNameWithTypeOnly != null ? pathNameWithTypeOnly : "null");
    List<String> words = new ArrayList<>();

    loopThroughFilesWithSuffixes(words, pathNameWithTypeAndSize);
    loopThroughFilesWithSuffixes(words, pathNameWithTypeOnly);
    loopThroughFilesWithoutSuffix(words, pathNameWithTypeAndSize);
    loopThroughFilesWithoutSuffix(words, pathNameWithTypeOnly);
    setFallbackStringIfListEmpty(words, className);

    processingFileNamePlaceholders(words, pathNameWithTypeAndSize, type);
    processWordPlaceholders(words, parentName, inhabitants);
    return String.join("", words);
  }

  public static String firstNameFrom(Class<?> clazz) {
    return npcNameFrom(true, false, clazz);
  }

  public static String lastNameFrom(Class<?> clazz) {
    return npcNameFrom(false, true, clazz);
  }

  public static String npcNameFrom(boolean firstName, boolean lastName, Class<?> clazz) {
    var className = clazz.getSimpleName().toUpperCase();
    log.debug(
        "Attempting to generate {} {} {} for class {}",
        firstName && lastName ? "first and last name" : "",
        firstName && !lastName ? "first name" : "",
        !firstName && lastName ? "last name" : "",
        className);
    List<String> words = new ArrayList<>();

    if (firstName) {
      loopThroughFilesWithoutSuffix(words, className + HYPHEN + FIRST_NAME);
    }
    if (lastName) {
      loopThroughFilesWithoutSuffix(words, className + HYPHEN + LAST_NAME);
    }
    setFallbackStringIfListEmpty(words, className);

    return String.join(" ", words).trim();
  }

  private static void loopThroughFilesWithSuffixes(List<String> words, String pathName) {
    if (words.isEmpty()) {
      for (String suffix : SUFFIXES) {
        var result = readWordsFromFile(pathName + suffix);
        if (!result.isEmpty() && (!suffix.equals(SUFFIX_MIDDLE) || random.nextInt(3) == 0)) {
          words.add(getRandomWord(result));
        }
      }
    }
  }

  private static void loopThroughFilesWithoutSuffix(List<String> words, String pathName) {
    if (words.isEmpty()) {
      var result = readWordsFromFile(pathName);
      if (!result.isEmpty()) {
        words.add(getRandomWord(result).trim());
      }
    }
  }

  private static void setFallbackStringIfListEmpty(List<String> words, String className) {
    if (words.isEmpty()) {
      log.warn("No input files found for class '{}'", className);
      words.add(className + " " + RandomStringUtils.randomNumeric(3));
    }
  }

  private static List<String> readWordsFromFile(String fileName) {
    InputStream inputStream =
        BasicStringGenerator.class
            .getClassLoader()
            .getResourceAsStream(FOLDER + fileName + FILE_EXTENSION);
    if (inputStream != null) {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        return reader.lines().map(String::trim).toList();
      } catch (IOException e) {
        log.warn("File '{}' not found", fileName);
      }
    }
    return new ArrayList<>();
  }

  private static String getRandomWord(List<String> words) {
    int randomIndex = random.nextInt(words.size());
    return words.get(randomIndex);
  }

  private static void processingFileNamePlaceholders(
      List<String> words, String pathName, AmenityType type) {
    if (words.get(0).startsWith(HYPHEN)) {
      var result = readWordsFromFile(pathName + words.get(0));
      if (result.isEmpty()) {
        log.warn("Failed to replace '{}' at path '{}'", words.get(0), pathName);
        var fallbackName = type != null ? type.name() : pathName;
        words.set(0, NONDESCRIPT + fallbackName + " " + RandomStringUtils.randomNumeric(3));
      } else {
        var randomWord = getRandomWord(result);
        log.info("Replacing '{}' with word '{}'", words.get(0), randomWord);
        words.set(0, randomWord);
      }
    }
  }

  private static void processWordPlaceholders(
      List<String> words, String parentName, List<Npc> inhabitants) {
    for (String word : words) {
      if (word.contains(PLACEHOLDER_PARENT_NAME) && parentName != null) {
        log.info("Replacing '{}' with class name '{}'", word, parentName);
        words.set(words.indexOf(word), word.replace(PLACEHOLDER_PARENT_NAME, parentName));
      }
      if (word.contains(PLACEHOLDER_OWNER_NAME) && parentName != null) {
        var randomInhabitant = inhabitants.get(random.nextInt(inhabitants.size())).getFirstName();
        log.info("Replacing '{}' with NPC name '{}'", word, randomInhabitant);
        words.set(words.indexOf(word), word.replace(PLACEHOLDER_OWNER_NAME, randomInhabitant));
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
