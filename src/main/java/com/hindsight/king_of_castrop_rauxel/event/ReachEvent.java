package com.hindsight.king_of_castrop_rauxel.event;

import static com.hindsight.king_of_castrop_rauxel.event.Role.EVENT_GIVER;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ReachEvent implements Event {

  @EqualsAndHashCode.Exclude private final List<Participant> participants;
  @EqualsAndHashCode.Exclude private final PointOfInterest targetPoi;
  private final EventDetails eventDetails;
  @EqualsAndHashCode.Exclude @Setter private Npc currentNpc;
  @EqualsAndHashCode.Exclude @Setter private Dialogue currentDialogue;
  @Setter private State eventState;
  @Setter private boolean isRepeatable;

  // TODO:
  //  - Give reward through targetNpc upon event completion
  //  - Consider removing targetPoi as should be accessible via participants
  public ReachEvent(
      EventDetails eventDetails, List<Participant> participants, PointOfInterest targetPoi) {
    this.eventDetails = eventDetails;
    this.participants = participants;
    this.eventState = State.AVAILABLE;
    this.targetPoi = targetPoi;
    this.isRepeatable = false;
    var eventGiver =
        participants.stream()
            .filter(p -> p.role().equals(EVENT_GIVER))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("NPC map must contain EVENT_GIVER role"));
    setActive(eventGiver.npc());
  }
}
