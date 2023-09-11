package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity.AmenityType;

@Slf4j
@NoArgsConstructor
public class BasicStringGenerator implements StringGenerator {
  private static final String FOLDER = "names" + System.getProperty("file.separator");
  private static final String SUFFIX_MIDDLE = "--middle";
  private static final String[] SUFFIXES = new String[]{"--start", SUFFIX_MIDDLE, "--end"};
  private static final String FILE_EXTENSION = ".txt";
  private static final String NONDESCRIPT = "Nondescript ";
  private static final String HYPHEN = "-";
  private static final String PLACEHOLDER_PARENT_NAME = "%P";
  private static final String PLACEHOLDER_OWNER_NAME = "%O";
  private static final String FIRST_NAME = "FIRST_NAME";
  private static final String LAST_NAME = "LAST_NAME";
  private Random random;

  public void setRandom(Random parentRandom) {
    random = parentRandom;
  }

  @Override
  public String locationNameFrom(Class<?> clazz) {
    return locationNameFrom(null, null, null, null, clazz);
  }

  @Override
  public String locationNameFrom(AbstractAmenity.AmenityType type, Class<?> clazz) {
    return locationNameFrom(type, null, null, null, clazz);
  }

  @Override
  public String locationNameFrom(String parentName, Class<?> clazz) {
    return locationNameFrom(null, null, parentName, null, clazz);
  }

  @Override
  public String locationNameFrom(
    AbstractAmenity.AmenityType type, AbstractLocation.Size parentSize, String parentName, List<Npc> inhabitants, Class<?> clazz) {
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

  @Override
  public String npcFirstNameFrom(Class<?> clazz) {
    return npcNameFrom(true, false, clazz);
  }

  @Override
  public String npcLastNameFrom(Class<?> clazz) {
    return npcNameFrom(false, true, clazz);
  }

  @Override
  public String npcNameFrom(boolean firstName, boolean lastName, Class<?> clazz) {
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

  private void loopThroughFilesWithSuffixes(List<String> words, String pathName) {
    if (words.isEmpty()) {
      for (String suffix : SUFFIXES) {
        var result = readWordsFromFile(pathName + suffix);
        if (!result.isEmpty() && (!suffix.equals(SUFFIX_MIDDLE) || random.nextInt(3) == 0)) {
          words.add(getRandomWord(result));
        }
      }
    }
  }

  private void loopThroughFilesWithoutSuffix(List<String> words, String pathName) {
    if (words.isEmpty()) {
      var result = readWordsFromFile(pathName);
      if (!result.isEmpty()) {
        words.add(getRandomWord(result).trim());
      }
    }
  }

  private void setFallbackStringIfListEmpty(List<String> words, String className) {
    if (words.isEmpty()) {
      log.warn("No input files found for class '{}'", className);
      words.add(className + " " + RandomStringUtils.randomNumeric(3));
    }
  }

  private List<String> readWordsFromFile(String fileName) {
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

  private String getRandomWord(List<String> words) {
    int randomIndex = random.nextInt(words.size());
    return words.get(randomIndex);
  }

  private void processingFileNamePlaceholders(
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

  private void processWordPlaceholders(
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
}
