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
  @EqualsAndHashCode.Exclude private final List<Npc> targetNpcs;
  @EqualsAndHashCode.Exclude private final PointOfInterest targetPoi;
  private final List<Dialogue> dialogues;
  private final List<Dialogue> targetDialogues;
  private final Type type;
  @Setter private Dialogue currentDialogue;
  @Setter private State eventState;
  @Setter private boolean isRepeatable;

  // TODO:
  //  - Fix bug where target NPC can start the event
  //  - Figure out how to customise speakWith action for targetNpc
  //  - Read reward from YAML file and give through targetNpc
  public ReachEvent(
      Npc npc,
      List<Dialogue> dialogues,
      List<Npc> targetNpcs,
      List<Dialogue> targetDialogues,
      PointOfInterest targetPoi) {
    this.dialogues = dialogues;
    this.targetDialogues = targetDialogues;
    this.npc = npc;
    this.targetNpcs = targetNpcs;
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
