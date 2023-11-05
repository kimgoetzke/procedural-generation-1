package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.event.DefeatEventDetails;
import com.hindsight.king_of_castrop_rauxel.event.EventDetails;
import com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Processes placeholders in strings at runtime. Note that the order in which they are processed is
 * important: More specific placeholders must be processed before more generic ones e.g. &OF must be
 * processed before &O.
 */
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
  private static final String PLACEHOLDER_ENEMY_TYPE = "&E";
  private static final String PLACEHOLDER_REWARD = "&R";

  /** Used to process events, in particular actions, interactions, and eventDetails. */
  public String process(String toProcess, Npc owner, Npc targetNpc) {
    return processOwnerPlaceholders(toProcess, owner)
        .replace(PLACEHOLDER_TARGET_NPC_FIRST_NAME, targetNpc.getFirstName())
        .replace(PLACEHOLDER_TARGET_NPC, targetNpc.getName())
        .replace(PLACEHOLDER_TARGET_POI, targetNpc.getHome().getName())
        .replace(PLACEHOLDER_TARGET_LOCATION, targetNpc.getHome().getParent().getName());
  }

  /** Used to process events, in particular actions and interactions. */
  public String process(String toProcess, Npc owner) {
    return processOwnerPlaceholders(toProcess, owner);
  }

  private String processOwnerPlaceholders(String toProcess, Npc owner) {
    return toProcess
        .replace(PLACEHOLDER_PARENT, owner.getHome().getName())
        .replace(PLACEHOLDER_LOCATION, owner.getHome().getParent().getName())
        .replace(PLACEHOLDER_POI_NAME, owner.getName())
        .replace(PLACEHOLDER_OWNER_FIRST_NAME, owner.getFirstName())
        .replace(PLACEHOLDER_OWNER, owner.getName());
  }

  /** Used to process events, in particular interactions. */
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

  /** Used to process defeat events, in particular interactions. */
  public String process(String toProcess, DefeatEventDetails eventDetails) {
    var poi = eventDetails.getPoi();
    var poiName = poi == null ? "" : poi.getName();
    var enemyType = eventDetails.getEnemyType();
    var enemyName = enemyType == null ? "" : enemyType.toString().toLowerCase();
    return toProcess
        .replace(PLACEHOLDER_ENEMY_TYPE, enemyName)
        .replace(PLACEHOLDER_TARGET_POI, poiName);
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
      throw new IllegalStateException("No inhabitant at: %s".formatted(amenity.getSummary()));
    } else if (inhabitant.getHome() != amenity) {
      throw new IllegalStateException("'%s' already has a home".formatted(inhabitant.getName()));
    }
    log.info("Injecting inhabitant first name '{}' into '{}'", inhabitant.getFirstName(), word);
    toProcess.set(
        toProcess.indexOf(word), word.replaceFirst(PLACEHOLDER_OWNER, inhabitant.getFirstName()));
  }
}
