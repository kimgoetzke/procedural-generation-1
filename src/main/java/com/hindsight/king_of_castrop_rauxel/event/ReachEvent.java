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
  private final Type type;
  @Setter private Npc currentNpc;
  @Setter private Dialogue currentDialogue;
  @Setter private State eventState;
  @Setter private boolean isRepeatable;

  // TODO:
  //  - Fix bug where target NPC can start the event
  //  - Figure out how to customise speakWith action for targetNpc
  //  - Read reward from YAML file and give through targetNpc
  public ReachEvent(List<Participant> participants, PointOfInterest targetPoi) {
    this.participants = participants;
    this.type = Type.REACH;
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
