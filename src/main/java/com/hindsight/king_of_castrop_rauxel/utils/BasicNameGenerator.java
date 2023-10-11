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

  public static final String FOLDER = "names" + System.getProperty("file.separator");
  private static final String SUFFIX_MIDDLE = "--middle";
  private static final String[] SUFFIXES = new String[] {"--start", SUFFIX_MIDDLE, "--end"};
  private static final String NONDESCRIPT = "Nondescript ";
  private static final String HYPHEN = "-";
  private static final String FIRST_NAME = "FIRST_NAME";
  private static final String LAST_NAME = "LAST_NAME";
  private final TxtReader txtReader = new TxtReader(FOLDER);
  private final PlaceholderProcessor placeholderProcessor = new PlaceholderProcessor();
  private Random random;

  public void setRandom(Random parentRandom) {
    random = parentRandom;
    placeholderProcessor.setRandom(parentRandom);
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

    processFileNamePlaceholders(words, pathNameWithTypeAndSize, type);
    placeholderProcessor.process(words, parentName, inhabitant, amenity);
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
        var result = txtReader.read(pathName + suffix);
        if (!result.isEmpty() && (!suffix.equals(SUFFIX_MIDDLE) || random.nextInt(3) == 0)) {
          words.add(txtReader.getRandom(result, random));
        }
      }
    }
  }

  private void loopThroughFilesWithoutSuffix(List<String> words, String pathName) {
    if (words.isEmpty()) {
      var result = txtReader.read(pathName);
      if (!result.isEmpty()) {
        words.add(txtReader.getRandom(result, random));
      }
    }
  }

  private void setFallbackStringIfListEmpty(List<String> words, String className) {
    if (words.isEmpty()) {
      log.warn("No input files found for class '{}'", className);
      words.add(className + " " + RandomStringUtils.randomNumeric(3));
    }
  }

  private void processFileNamePlaceholders(List<String> words, String pathName, PoiType type) {
    if (words.get(0).startsWith(HYPHEN)) {
      var result = txtReader.read(pathName + words.get(0));
      if (result.isEmpty()) {
        log.warn("Failed to replace '{}' at path '{}'", words.get(0), pathName);
        var fallbackName = type != null ? type.name() : pathName;
        words.set(0, NONDESCRIPT + fallbackName + " " + RandomStringUtils.randomNumeric(3));
      } else {
        var randomWord = txtReader.getRandom(result, random);
        log.info("Replacing '{}' with word '{}'", words.get(0), randomWord);
        words.set(0, randomWord);
      }
    }
  }
}
