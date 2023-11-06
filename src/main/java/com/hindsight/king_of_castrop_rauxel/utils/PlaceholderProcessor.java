package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.event.DefeatEventDetails;
import com.hindsight.king_of_castrop_rauxel.event.EventDetails;
import com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Processes placeholders in strings at runtime. Note that the order in which they are processed is
 * important: More specific placeholders must be processed before more generic ones e.g. &OF must be
 * processed before &O.
 */
@Slf4j
public class PlaceholderProcessor {

  // TODO: Allow injecting player name in PlaceholderProcessor with &PL
  private static final String PARENT = "&P";
  private static final String LOCATION = "&L";
  private static final String POI_NAME = "&I";
  private static final String OWNER_FIRST_NAME = "&OF";
  private static final String OWNER = "&O";
  private static final String TARGET_NPC_FIRST_NAME = "&TOF";
  private static final String TARGET_NPC = "&TO";
  private static final String TARGET_POI = "&TI";
  private static final String TARGET_LOCATION = "&TL";
  private static final String ENEMY_TYPE = "&E";
  private static final String REWARD = "&R";

  /** Used to process events, in particular actions, interactions, and eventDetails. */
  public String process(String toProcess, Npc owner, Npc targetNpc) {
    return processOwnerPlaceholders(toProcess, owner)
        .replace(TARGET_NPC_FIRST_NAME, targetNpc.getFirstName())
        .replace(TARGET_NPC, targetNpc.getName())
        .replace(TARGET_POI, targetNpc.getHome().getName())
        .replace(TARGET_LOCATION, targetNpc.getHome().getParent().getName());
  }

  /** Used to process events, in particular actions and interactions. */
  public String process(String toProcess, Npc owner) {
    return processOwnerPlaceholders(toProcess, owner);
  }

  private String processOwnerPlaceholders(String toProcess, Npc owner) {
    return toProcess
        .replace(PARENT, owner.getHome().getName())
        .replace(LOCATION, owner.getHome().getParent().getName())
        .replace(POI_NAME, owner.getName())
        .replace(OWNER_FIRST_NAME, owner.getFirstName())
        .replace(OWNER, owner.getName());
  }

  /** Used to process events, in particular interactions. */
  public String process(String toProcess, EventDetails eventDetails) {
    var rewards = eventDetails.getRewards();
    if (rewards == null || rewards.isEmpty()) {
      return toProcess.replace(REWARD, "none");
    }
    var rewardsString = new StringBuilder();
    for (var reward : rewards) {
      rewardsString.append(reward.toString()).append(", ");
    }
    rewardsString.setLength(rewardsString.length() - 2);
    return toProcess.replace(REWARD, rewardsString);
  }

  /** Used to process defeat events, in particular interactions. */
  public String process(String toProcess, DefeatEventDetails eventDetails) {
    var poi = eventDetails.getPoi();
    var poiName = poi == null ? "" : poi.getName();
    var enemyType = eventDetails.getEnemyType();
    var enemyName = enemyType == null ? "" : enemyType.toString().toLowerCase();
    return toProcess
        .replace(ENEMY_TYPE, enemyName)
        .replace(TARGET_POI, poiName);
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
    if (word.contains(PARENT) && parentName != null) {
      log.info("Injecting parent class name '{}' into '{}'", parentName, word);
      toProcess.set(toProcess.indexOf(word), word.replace(PARENT, parentName));
    }
  }

  private void injectInhabitantName(
      List<String> toProcess, String word, Npc inhabitant, AbstractAmenity amenity) {
    if (!word.contains(OWNER)) {
      return;
    }
    checkNotNull(inhabitant, "No inhabitant at: %s", amenity.getSummary());
    checkState(inhabitant.getHome() == amenity, "%s already has a home", inhabitant.getName());
    log.info("Injecting inhabitant first name '{}' into '{}'", inhabitant.getFirstName(), word);
    toProcess.set(
        toProcess.indexOf(word), word.replaceFirst(OWNER, inhabitant.getFirstName()));
  }
}
