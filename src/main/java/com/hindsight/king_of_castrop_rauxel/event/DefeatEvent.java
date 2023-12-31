package com.hindsight.king_of_castrop_rauxel.event;

import static com.hindsight.king_of_castrop_rauxel.event.Role.EVENT_GIVER;

import com.hindsight.king_of_castrop_rauxel.character.Enemy;
import com.hindsight.king_of_castrop_rauxel.character.Npc;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class DefeatEvent implements Event {

  // General event fields
  private final EventDetails eventDetails;
  @EqualsAndHashCode.Exclude private final List<Participant> participants;
  @EqualsAndHashCode.Exclude @Setter private Npc currentNpc;
  @EqualsAndHashCode.Exclude @Setter private Dialogue currentDialogue;
  @Setter private State eventState;
  @Setter private boolean isRepeatable;

  // DefeatEvent specific fields
  private final TaskType taskType;
  private final PointOfInterest poi;
  private final Enemy.Type enemyType;
  private final int toDefeat;
  private int defeated;

  public enum TaskType {
    KILL_ALL_AT_POI,
    KILL_TYPE_ANYWHERE,
  }

  public DefeatEvent(
      EventDetails eventDetails, DefeatEventDetails defeatDetails, List<Participant> participants) {
    this.eventDetails = eventDetails;
    this.taskType = defeatDetails.getTaskType();
    this.poi = defeatDetails.getPoi();
    this.enemyType = defeatDetails.getEnemyType();
    this.toDefeat = defeatDetails.getToDefeat();
    this.participants = participants;
    this.eventState = State.AVAILABLE;
    this.isRepeatable = false;
    var eventGiver =
        participants.stream()
            .filter(p -> p.role().equals(EVENT_GIVER))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("NPC map must contain EVENT_GIVER role"));
    setActive(eventGiver.npc());
  }

  public void setEventStateToReady(PointOfInterest poi) {
    if (this.poi.equals(poi)) {
      eventState = State.READY;
      log.info("{} event at {} is now {}", taskType, this.poi.getName(), eventState);
    }
  }

  public void incrementDefeated(Enemy.Type defeatedEnemyType) {
    if (!defeatedEnemyType.equals(enemyType)) {
      return;
    }
    defeated++;
    if (toDefeat > 0 && defeated >= toDefeat) {
      eventState = State.READY;
    }
    log.info("Defeated {} of {} {} (state: {})", defeated, toDefeat, enemyType, eventState);
  }

  public String toString() {
    var eventGiverHome = eventDetails.getEventGiver().getHome();
    return switch (taskType) {
      case KILL_TYPE_ANYWHERE -> {
        var d = Math.min(toDefeat, defeated);
        yield CliComponent.task(
                "Find and kill: %s (%d of %d)"
                    .formatted(enemyType.toString().toLowerCase(), d, toDefeat))
            + " | From: "
            + CliComponent.npc(eventDetails.getEventGiver())
            + ", "
            + CliComponent.poi(eventGiverHome)
            + " of "
            + CliComponent.location(eventGiverHome.getParent())
            + " | Status: "
            + CliComponent.status(eventState);
      }
      case KILL_ALL_AT_POI -> CliComponent.task(
              "Clear all enemies at %s of %s".formatted(poi.getName(), poi.getParent().getName()))
          + " | From: "
          + CliComponent.npc(eventDetails.getEventGiver())
          + ", "
          + CliComponent.poi(eventGiverHome)
          + " of "
          + CliComponent.location(eventGiverHome.getParent())
          + " | Status: "
          + CliComponent.status(eventState);
    };
  }
}
