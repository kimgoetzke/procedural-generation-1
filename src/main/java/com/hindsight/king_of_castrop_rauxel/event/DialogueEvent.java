package com.hindsight.king_of_castrop_rauxel.event;

import static com.hindsight.king_of_castrop_rauxel.event.Role.EVENT_GIVER;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import java.util.List;

import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DialogueEvent implements Event {

  private final EventDetails eventDetails;
  @EqualsAndHashCode.Exclude private final List<Participant> participants;
  @EqualsAndHashCode.Exclude @Setter private Npc currentNpc;
  @EqualsAndHashCode.Exclude @Setter private Dialogue currentDialogue;
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

  public String toString() {
    var currentNpcHome = currentNpc.getHome();
    return "Dialogue with "
        + CliComponent.npc(currentNpc)
        + " at "
        + CliComponent.poi(currentNpcHome)
        + " of "
        + CliComponent.location(currentNpcHome.getParent())
        + " | Status: "
        + CliComponent.status(eventState);
  }
}
