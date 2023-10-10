package com.hindsight.king_of_castrop_rauxel.event;

import static com.hindsight.king_of_castrop_rauxel.event.Role.EVENT_GIVER;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DialogueEvent implements Event {

  @EqualsAndHashCode.Exclude private final List<Participant> participants;
  private final EventDetails eventDetails;
  @Setter private Npc currentNpc;
  @Setter private Dialogue currentDialogue;
  @Setter private State eventState;
  @Setter private boolean isRepeatable;

  public DialogueEvent(
      EventDetails eventDetails, List<Participant> participants, boolean isRepeatable) {
    this.eventDetails = eventDetails;
    this.participants = participants;
    this.eventState = State.AVAILABLE;
    this.isRepeatable = isRepeatable;
    var eventGiver =
        participants.stream()
            .filter(p -> p.role().equals(EVENT_GIVER))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("NPC map must contain EVENT_GIVER role"));
    setActive(eventGiver.npc());
  }
}
