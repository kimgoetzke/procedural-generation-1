package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class DialogueEvent implements Event {

  @EqualsAndHashCode.Exclude private final Npc npc;
  private final List<Npc> targetNpcs = List.of();
  private final List<Dialogue> dialogues;
  private final Type type;
  @Setter private Dialogue currentDialogue;
  @Setter private State eventState;
  @Setter private boolean isRepeatable;

  public DialogueEvent(List<Dialogue> dialogues, Npc npc, boolean isRepeatable) {
    this.dialogues = dialogues;
    this.npc = npc;
    this.type = Type.DIALOGUE;
    this.eventState = State.AVAILABLE;
    this.isRepeatable = isRepeatable;
    setCurrentDialogue(
        dialogues.stream()
            .filter(d -> d.getState() == State.AVAILABLE)
            .findFirst()
            .orElse(
                dialogues.stream()
                    .filter(d -> d.getState() == State.NONE)
                    .findFirst()
                    .orElseThrow(
                        () ->
                            new IllegalStateException(
                                "Dialogues must contain an AVAILABLE or NONE state"))));
  }
}
