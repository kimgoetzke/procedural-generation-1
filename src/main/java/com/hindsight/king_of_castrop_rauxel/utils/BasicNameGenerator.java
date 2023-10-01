package com.hindsight.king_of_castrop_rauxel.utils;

import static com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity.PoiType;
import static com.hindsight.king_of_castrop_rauxel.location.AbstractLocation.*;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

@Slf4j
@NoArgsConstructor
public class BasicNameGenerator implements NameGenerator {
  private static final String FOLDER = "names" + System.getProperty("file.separator");
  private static final String SUFFIX_MIDDLE = "--middle";
  private static final String[] SUFFIXES = new String[] {"--start", SUFFIX_MIDDLE, "--end"};
  private static final String FILE_EXTENSION = ".txt";
  private static final String NONDESCRIPT = "Nondescript ";
  private static final String HYPHEN = "-";
  private static final String PLACEHOLDER_PARENT_NAME = "%P";
  private static final String PLACEHOLDER_OWNER_NAME = "%O";
  private static final String FIRST_NAME = "FIRST_NAME";
  private static final String LAST_NAME = "LAST_NAME";
  public static final String FALLBACK_INHABITANT = "INHABITANT--fallback";
  private final FileProcessor fileProcessor = new FileProcessor(FOLDER, FILE_EXTENSION);
  private Random random;

  public void setRandom(Random parentRandom) {
    random = parentRandom;
  }

  @Override
  public String locationNameFrom(Class<?> clazz) {
    return locationNameFrom(null, null, null, null, clazz);
  }

  @Override
  public String locationNameFrom(
      AbstractAmenity amenity, Size parentSize, String parentName, Npc inhabitant, Class<?> clazz) {
    var type = amenity == null ? null : amenity.getType();
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
    processWordPlaceholders(words, parentName, inhabitant, amenity);
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
        var result = fileProcessor.readWordsFromFile(pathName + suffix);
        if (!result.isEmpty() && (!suffix.equals(SUFFIX_MIDDLE) || random.nextInt(3) == 0)) {
          words.add(fileProcessor.getRandomWord(result, random));
        }
      }
    }
  }

  private void loopThroughFilesWithoutSuffix(List<String> words, String pathName) {
    if (words.isEmpty()) {
      var result = fileProcessor.readWordsFromFile(pathName);
      if (!result.isEmpty()) {
        words.add(fileProcessor.getRandomWord(result, random));
      }
    }
  }

  private void setFallbackStringIfListEmpty(List<String> words, String className) {
    if (words.isEmpty()) {
      log.warn("No input files found for class '{}'", className);
      words.add(className + " " + RandomStringUtils.randomNumeric(3));
    }
  }

  private void processingFileNamePlaceholders(List<String> words, String pathName, PoiType type) {
    if (words.get(0).startsWith(HYPHEN)) {
      var result = fileProcessor.readWordsFromFile(pathName + words.get(0));
      if (result.isEmpty()) {
        log.warn("Failed to replace '{}' at path '{}'", words.get(0), pathName);
        var fallbackName = type != null ? type.name() : pathName;
        words.set(0, NONDESCRIPT + fallbackName + " " + RandomStringUtils.randomNumeric(3));
      } else {
        var randomWord = fileProcessor.getRandomWord(result, random);
        log.info("Replacing '{}' with word '{}'", words.get(0), randomWord);
        words.set(0, randomWord);
      }
    }
  }

  private void processWordPlaceholders(
      List<String> words, String parentName, Npc inhabitant, AbstractAmenity amenity) {
    for (String word : words) {
      injectParentName(words, word, parentName);
      injectInhabitantName(words, word, inhabitant, amenity);
    }
  }

  private void injectParentName(List<String> words, String word, String parentName) {
    if (word.contains(PLACEHOLDER_PARENT_NAME) && parentName != null) {
      log.info("Injecting parent class name '{}' into '{}'", parentName, word);
      words.set(words.indexOf(word), word.replace(PLACEHOLDER_PARENT_NAME, parentName));
    }
  }

  private void injectInhabitantName(
      List<String> words, String word, Npc inhabitant, AbstractAmenity amenity) {
    if (!word.contains(PLACEHOLDER_OWNER_NAME)) {
      return;
    }
    if (inhabitant == null) {
      var fallbackName = getFallbackName(FALLBACK_INHABITANT);
      words.set(words.indexOf(word), word.replaceFirst(PLACEHOLDER_OWNER_NAME, fallbackName));
      log.warn("Inhabitant was null when generating {}, using {} instead", word, fallbackName);
      return;
    } else if (inhabitant.getHome() != amenity) {
      throw new IllegalStateException(
          "Inhabitant '" + inhabitant.getName() + "' already has a different home");
    }
    log.info("Injecting inhabitant first name '{}' into '{}'", inhabitant.getFirstName(), word);
    words.set(
        words.indexOf(word), word.replaceFirst(PLACEHOLDER_OWNER_NAME, inhabitant.getFirstName()));
  }

  private String getFallbackName(String fileName) {
    var result = fileProcessor.readWordsFromFile(fileName);
    if (result.isEmpty()) {
      throw new IllegalStateException("Failed to find fallback name in '%s'".formatted(fileName));
    }
    return fileProcessor.getRandomWord(result, random);
  }
}