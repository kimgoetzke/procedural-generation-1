package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import java.util.List;

import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ReachEvent implements Event {

  @EqualsAndHashCode.Exclude private final Npc npc;
  @EqualsAndHashCode.Exclude private final Npc targetNpc;
  @EqualsAndHashCode.Exclude private final PointOfInterest targetPoi;
  private final List<Dialogue> dialogues;
  private final Type type;
  @Setter private Dialogue currentDialogue;
  @Setter private State eventState;
  @Setter private boolean isRepeatable;

  public ReachEvent(List<Dialogue> dialogues, Npc npc, Npc target, PointOfInterest targetPoi) {
    this.dialogues = dialogues;
    this.npc = npc;
    this.targetNpc = target;
    this.type = Type.REACH;
    this.eventState = State.AVAILABLE;
    this.targetPoi = targetPoi;
    this.isRepeatable = false;
    setCurrentDialogue(
        dialogues.stream()
            .filter(d -> d.getState() == State.AVAILABLE)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No dialogue for state AVAILABLE")));
  }
}
