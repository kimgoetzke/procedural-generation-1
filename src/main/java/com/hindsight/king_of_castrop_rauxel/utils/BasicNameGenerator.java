package com.hindsight.king_of_castrop_rauxel.utils;

import static com.hindsight.king_of_castrop_rauxel.location.PointOfInterest.Type;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity;
import com.hindsight.king_of_castrop_rauxel.encounter.DungeonDetails;
import com.hindsight.king_of_castrop_rauxel.location.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

@Slf4j
public class BasicNameGenerator implements NameGenerator {

  private static final String SUFFIX_MIDDLE = "--middle";
  private static final String[] SUFFIXES = new String[] {"--start", SUFFIX_MIDDLE, "--end"};
  private static final String BASIC_ENEMY_SUFFIX = "--type-prefix";
  private static final String NONDESCRIPT = "Nondescript ";
  private static final String HYPHEN = "-";
  private static final String FIRST_NAME = "first_name";
  private static final String LAST_NAME = "last_name";
  private final TxtReader txtReader;
  private final PlaceholderProcessor processor;
  private Random random;

  public BasicNameGenerator(FolderReader folderReader) {
    this.txtReader = new TxtReader(folderReader.getNamesFolder());
    processor = new PlaceholderProcessor();
  }

  public void initialise(Random parentRandom) {
    this.random = parentRandom;
  }

  @Override
  public String locationNameFrom(Class<?> clazz) {
    return locationNameFrom(null, null, null, null, clazz);
  }

  @Override
  public String locationNameFrom(
      AbstractAmenity amenity, Size parentSize, String parentName, Npc inhabitant, Class<?> clazz) {
    var type = amenity == null ? null : amenity.getType();
    var withType = type == null ? "" : HYPHEN + type.name().toLowerCase();
    var withSize = parentSize == null ? "" : HYPHEN + parentSize.name().toLowerCase();
    var className = clazz.getSimpleName().toLowerCase();
    var pathNameWithTypeAndSize = "%s%s%s".formatted(className, withType, withSize);
    var pathNameWithTypeOnly = "%s%s".formatted(className, withType);
    var words = new ArrayList<String>();
    log.debug(
        "Attempting to generate string for class '{}' with '{}' or '{}'",
        className,
        pathNameWithTypeAndSize != null ? pathNameWithTypeAndSize : "null",
        pathNameWithTypeOnly != null ? pathNameWithTypeOnly : "null");

    randomWordForEachSuffix(words, pathNameWithTypeAndSize);
    randomWordForEachSuffix(words, pathNameWithTypeOnly);
    randomWordFromSpecificFile(words, pathNameWithTypeAndSize);
    randomWordFromSpecificFile(words, pathNameWithTypeOnly);
    setFallbackStringIfListEmpty(words, className);

    processFileNamePlaceholders(words, pathNameWithTypeAndSize, type);
    processor.process(words, parentName, inhabitant, amenity);
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

  private String npcNameFrom(boolean firstName, boolean lastName, Class<?> clazz) {
    var className = clazz.getSimpleName().toLowerCase();
    var words = new ArrayList<String>();
    log.debug(
        "Attempting to generate {} {} {} for class '{}'",
        firstName && lastName ? "first and last name" : "",
        firstName && !lastName ? "first name" : "",
        !firstName && lastName ? "last name" : "",
        className);

    if (firstName) {
      randomWordFromSpecificFile(words, className + HYPHEN + FIRST_NAME);
    }
    if (lastName) {
      randomWordFromSpecificFile(words, className + HYPHEN + LAST_NAME);
    }
    setFallbackStringIfListEmpty(words, className);

    return String.join(" ", words).trim();
  }

  @Override
  public String enemyNameFrom(Class<?> clazz, DungeonDetails.Type type) {
    var className = clazz.getSimpleName().toLowerCase();
    var pathName = className + BASIC_ENEMY_SUFFIX;
    var words = new ArrayList<String>();
    log.debug("Attempting to generate {} of type {}", className, type);
    randomWordFromSpecificFile(words, pathName);
    setFallbackStringIfListEmpty(words, className);
    return words.get(0).trim() + " " + type.name().toLowerCase();
  }

  @Override
  public String dungeonNameFrom(Class<?> clazz, DungeonDetails.Type type) {
    var className = clazz.getSimpleName().toLowerCase();
    var pathNameWithType = className + HYPHEN + type.name().toLowerCase();
    var words = new ArrayList<String>();
    log.debug("Attempting to generate dungeon name for class '{}'", className);
    randomWordFromSpecificFile(words, pathNameWithType);
    randomWordFromSpecificFile(words, className);
    setFallbackStringIfListEmpty(words, className);
    return words.get(0).trim();
  }

  // TODO: Think about what descriptions would make sense for different types of dungeons
  @Override
  public String dungeonDescriptionFrom(Class<?> ignoredClass, DungeonDetails.Type ignoredType) {
    return "A dark and foreboding place";
  }

  /**
   * Adds one, random word from each file with the given suffixes to the list of words, with there
   * being a 25% chance that a word from the file with the suffix '--middle' is added.
   */
  private void randomWordForEachSuffix(List<String> words, String partialFileName) {
    if (words.isEmpty()) {
      for (String suffix : SUFFIXES) {
        var result = txtReader.read(partialFileName + suffix);
        if (!result.isEmpty() && (!suffix.equals(SUFFIX_MIDDLE) || random.nextInt(3) == 0)) {
          words.add(txtReader.getRandom(result, random));
        }
      }
    }
  }

  /** Adds one, random word from the file with the given name to the list of words. */
  private void randomWordFromSpecificFile(List<String> words, String fileName) {
    if (words.isEmpty()) {
      var result = txtReader.read(fileName);
      if (!result.isEmpty()) {
        words.add(txtReader.getRandom(result, random));
      }
    }
  }

  private void setFallbackStringIfListEmpty(List<String> words, String className) {
    if (words.isEmpty()) {
      log.error("No input files found for class '{}'", className);
      words.add(className.toUpperCase() + " " + RandomStringUtils.randomNumeric(3));
    }
  }

  private void processFileNamePlaceholders(List<String> words, String pathName, Type type) {
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
