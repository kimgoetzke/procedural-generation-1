package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;

import static com.hindsight.king_of_castrop_rauxel.utils.BasicNameGenerator.FOLDER;

@Slf4j
public class PlaceholderProcessor {

  private static final String PLACEHOLDER_PARENT = "&P";
  private static final String PLACEHOLDER_OWNER = "&O";
  private static final String PLACEHOLDER_POI_NAME = "&I";
  private static final String PLACEHOLDER_LOCATION = "&L";
  private static final String PLACEHOLDER_TARGET_NPC = "&TO";
  private static final String PLACEHOLDER_TARGET_POI = "&TI";
  private static final String PLACEHOLDER_TARGET_LOCATION = "&TL";
  public static final String FALLBACK_INHABITANT = "INHABITANT--fallback";
  private final TxtReader txtReader = new TxtReader(FOLDER);
  private Random random;

  public void setRandom(Random parentRandom) {
    random = parentRandom;
  }

  public String process(String text, Npc owner, Npc targetNpc) {
    text = text.replace(PLACEHOLDER_LOCATION, owner.getHome().getParent().getName());
    text = text.replace(PLACEHOLDER_OWNER, owner.getName());
    text = text.replace(PLACEHOLDER_TARGET_NPC, targetNpc.getName());
    text = text.replace(PLACEHOLDER_TARGET_POI, targetNpc.getHome().getName());
    text = text.replace(PLACEHOLDER_TARGET_LOCATION, targetNpc.getHome().getParent().getName());
    text = text.replace(PLACEHOLDER_POI_NAME, owner.getHome().getName());
    return text;
  }

  public String process(String text, Npc owner) {
    text = text.replace(PLACEHOLDER_LOCATION, owner.getHome().getParent().getName());
    text = text.replace(PLACEHOLDER_OWNER, owner.getName());
    text = text.replace(PLACEHOLDER_POI_NAME, owner.getHome().getName());
    return text;
  }

  public void processList(
      List<String> words, String parentName, Npc inhabitant, AbstractAmenity amenity) {
    for (String word : words) {
      injectParentName(words, word, parentName);
      injectInhabitantName(words, word, inhabitant, amenity);
    }
  }

  private void injectParentName(List<String> words, String word, String parentName) {
    if (word.contains(PLACEHOLDER_PARENT) && parentName != null) {
      log.info("Injecting parent class name '{}' into '{}'", parentName, word);
      words.set(words.indexOf(word), word.replace(PLACEHOLDER_PARENT, parentName));
    }
  }

  private void injectInhabitantName(
      List<String> words, String word, Npc inhabitant, AbstractAmenity amenity) {
    if (!word.contains(PLACEHOLDER_OWNER)) {
      return;
    }
    if (inhabitant == null) {
      var fallbackName = getFallbackName();
      words.set(words.indexOf(word), word.replaceFirst(PLACEHOLDER_OWNER, fallbackName));
      log.warn("Inhabitant was null when generating {}, using {} instead", word, fallbackName);
      return;
    } else if (inhabitant.getHome() != amenity) {
      throw new IllegalStateException(
          "Inhabitant '" + inhabitant.getName() + "' already has a different home");
    }
    log.info("Injecting inhabitant first name '{}' into '{}'", inhabitant.getFirstName(), word);
    words.set(words.indexOf(word), word.replaceFirst(PLACEHOLDER_OWNER, inhabitant.getFirstName()));
  }

  private String getFallbackName() {
    var result = txtReader.read(PlaceholderProcessor.FALLBACK_INHABITANT);
    if (result.isEmpty()) {
      throw new IllegalStateException(
          "Failed to find fallback name in '%s'"
              .formatted(PlaceholderProcessor.FALLBACK_INHABITANT));
    }
    return txtReader.getRandom(result, random);
  }
}
