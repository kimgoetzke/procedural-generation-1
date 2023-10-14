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

  // TODO: Allow injecting player name in PlaceholderProcessor with &PL
  private static final String PLACEHOLDER_PARENT = "&P";
  private static final String PLACEHOLDER_LOCATION = "&L";
  private static final String PLACEHOLDER_POI_NAME = "&I";
  private static final String PLACEHOLDER_OWNER_FIRST_NAME = "&OF";
  private static final String PLACEHOLDER_OWNER = "&O";
  private static final String PLACEHOLDER_TARGET_NPC_FIRST_NAME = "&TOF";
  private static final String PLACEHOLDER_TARGET_NPC = "&TO";
  private static final String PLACEHOLDER_TARGET_POI = "&TI";
  private static final String PLACEHOLDER_TARGET_LOCATION = "&TL";
  private static final String PLACEHOLDER_REWARD = "&R";
  public static final String FALLBACK_INHABITANT = "INHABITANT--fallback";
  private final TxtReader txtReader = new TxtReader(FOLDER);
  private Random random;

  public void setRandom(Random parentRandom) {
    random = parentRandom;
  }

  /** Used to process events. */
  public String process(String toProcess, Npc owner, Npc targetNpc) {
    toProcess = processOwnerPlaceholders(toProcess, owner);
    toProcess = toProcess.replace(PLACEHOLDER_TARGET_NPC_FIRST_NAME, targetNpc.getFirstName());
    toProcess = toProcess.replace(PLACEHOLDER_TARGET_NPC, targetNpc.getName());
    toProcess = toProcess.replace(PLACEHOLDER_TARGET_POI, targetNpc.getHome().getName());
    toProcess =
        toProcess.replace(PLACEHOLDER_TARGET_LOCATION, targetNpc.getHome().getParent().getName());
    return toProcess;
  }

  /** Used to process events. */
  public String process(String toProcess, Npc owner) {
    return processOwnerPlaceholders(toProcess, owner);
  }

  private String processOwnerPlaceholders(String toProcess, Npc owner) {
    toProcess = toProcess.replace(PLACEHOLDER_PARENT, owner.getHome().getName());
    toProcess = toProcess.replace(PLACEHOLDER_LOCATION, owner.getHome().getParent().getName());
    toProcess = toProcess.replace(PLACEHOLDER_POI_NAME, owner.getName());
    toProcess = toProcess.replace(PLACEHOLDER_OWNER_FIRST_NAME, owner.getFirstName());
    toProcess = toProcess.replace(PLACEHOLDER_OWNER, owner.getName());
    return toProcess;
  }

  public String process(String toProcess, EventDetails eventDetails) {
    var rewards = eventDetails.getRewards();
    if (rewards == null || rewards.isEmpty()) {
      return toProcess.replace(PLACEHOLDER_REWARD, "none");
    }
    var rewardsString = new StringBuilder();
    for (var reward : rewards) {
      rewardsString.append(reward.toString()).append(", ");
    }
    rewardsString.setLength(rewardsString.length() - 2);
    return toProcess.replace(PLACEHOLDER_REWARD, rewardsString);
  }

  /** Used to process Location and PointOfInterest names. */
  public void process(
      List<String> toProcess, String parentName, Npc inhabitant, AbstractAmenity amenity) {
    for (String word : toProcess) {
      injectParentName(toProcess, word, parentName);
      injectInhabitantName(toProcess, word, inhabitant, amenity);
    }
  }

  private void injectParentName(List<String> toProcess, String word, String parentName) {
    if (word.contains(PLACEHOLDER_PARENT) && parentName != null) {
      log.info("Injecting parent class name '{}' into '{}'", parentName, word);
      toProcess.set(toProcess.indexOf(word), word.replace(PLACEHOLDER_PARENT, parentName));
    }
  }

  private void injectInhabitantName(
      List<String> toProcess, String word, Npc inhabitant, AbstractAmenity amenity) {
    if (!word.contains(PLACEHOLDER_OWNER)) {
      return;
    }
    if (inhabitant == null) {
      var fallbackName = getInhabitantFallbackName();
      toProcess.set(toProcess.indexOf(word), word.replaceFirst(PLACEHOLDER_OWNER, fallbackName));
      log.warn("Inhabitant was null when generating {}, using {} instead", word, fallbackName);
      return;
    } else if (inhabitant.getHome() != amenity) {
      throw new IllegalStateException(
          "Inhabitant '" + inhabitant.getName() + "' already has a different home");
    }
    log.info("Injecting inhabitant first name '{}' into '{}'", inhabitant.getFirstName(), word);
    toProcess.set(
        toProcess.indexOf(word), word.replaceFirst(PLACEHOLDER_OWNER, inhabitant.getFirstName()));
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
