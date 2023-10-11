package com.hindsight.king_of_castrop_rauxel.utils;

import static com.hindsight.king_of_castrop_rauxel.utils.BasicNameGenerator.FOLDER;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.event.EventDetails;
import com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlaceholderProcessor {

  private static final String PLACEHOLDER_PARENT = "&P";
  private static final String PLACEHOLDER_LOCATION = "&L";
  private static final String PLACEHOLDER_POI_NAME = "&I";
  private static final String PLACEHOLDER_OWNER = "&O";
  private static final String PLACEHOLDER_OWNER_FIRST_NAME = "&OF";
  private static final String PLACEHOLDER_TARGET_NPC = "&TO";
  private static final String PLACEHOLDER_TARGET_NPC_FIRST_NAME = "&TOF";
  private static final String PLACEHOLDER_TARGET_POI = "&TI";
  private static final String PLACEHOLDER_TARGET_LOCATION = "&TL";
  private static final String PLACEHOLDER_REWARD = "&R";
  public static final String FALLBACK_INHABITANT = "INHABITANT--fallback";
  private final TxtReader txtReader = new TxtReader(FOLDER);
  private Random random;

  public void setRandom(Random parentRandom) {
    random = parentRandom;
  }

  public String process(String any, Npc owner, Npc targetNpc) {
    any = processOwnerPlaceholders(any, owner);
    any = any.replace(PLACEHOLDER_TARGET_NPC, targetNpc.getName());
    any = any.replace(PLACEHOLDER_TARGET_NPC_FIRST_NAME, targetNpc.getFirstName());
    any = any.replace(PLACEHOLDER_TARGET_POI, targetNpc.getHome().getName());
    any = any.replace(PLACEHOLDER_TARGET_LOCATION, targetNpc.getHome().getParent().getName());
    return any;
  }

  public String process(String any, Npc owner) {
    return processOwnerPlaceholders(any, owner);
  }

  private String processOwnerPlaceholders(String any, Npc owner) {
    any = any.replace(PLACEHOLDER_PARENT, owner.getHome().getName());
    any = any.replace(PLACEHOLDER_LOCATION, owner.getHome().getParent().getName());
    any = any.replace(PLACEHOLDER_POI_NAME, owner.getName());
    any = any.replace(PLACEHOLDER_OWNER, owner.getName());
    any = any.replace(PLACEHOLDER_OWNER_FIRST_NAME, owner.getFirstName());
    return any;
  }

  public String process(String any, EventDetails eventDetails) {
    var rewards = eventDetails.getRewards();
    if (rewards == null) {
      return any.replace(PLACEHOLDER_REWARD, "none");
    }
    var rewardsString = new StringBuilder();
    for (var reward : rewards) {
      rewardsString.append(reward.toString()).append(", ");
    }
    rewardsString.setLength(rewardsString.length() - 2);
    return any.replace(PLACEHOLDER_REWARD, rewardsString);
  }

  /** Used to process Location and PointOfInterest names. */
  public void process(
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
      var fallbackName = getInhabitantFallbackName();
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

  private String getInhabitantFallbackName() {
    var result = txtReader.read(PlaceholderProcessor.FALLBACK_INHABITANT);
    if (result.isEmpty()) {
      throw new IllegalStateException(
          "Failed to find fallback name in '%s'"
              .formatted(PlaceholderProcessor.FALLBACK_INHABITANT));
    }
    return txtReader.getRandom(result, random);
  }
}
