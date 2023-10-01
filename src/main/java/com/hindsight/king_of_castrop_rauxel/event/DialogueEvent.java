package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DialogueEvent implements Event {

  private final Dialogue dialogue;
  @EqualsAndHashCode.Exclude private final Npc npc;
  private final EventType type;
  @Setter private EventState state;
  @Setter private boolean isRepeatable;

  public DialogueEvent(Dialogue dialogue, Npc npc, EventType type, boolean isRepeatable) {
    this.dialogue = dialogue;
    this.npc = npc;
    this.type = type;
    this.state = EventState.AVAILABLE;
    this.isRepeatable = isRepeatable;
  }
}
