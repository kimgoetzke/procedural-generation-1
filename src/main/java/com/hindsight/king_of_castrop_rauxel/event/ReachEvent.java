package com.hindsight.king_of_castrop_rauxel.event;

import static com.hindsight.king_of_castrop_rauxel.event.Role.EVENT_GIVER;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ReachEvent implements Event {

  private final EventDetails eventDetails;
  @EqualsAndHashCode.Exclude private final List<Participant> participants;
  @EqualsAndHashCode.Exclude @Setter private Npc currentNpc;
  @EqualsAndHashCode.Exclude @Setter private Dialogue currentDialogue;
  @Setter private State eventState;
  @Setter private boolean isRepeatable;

  public ReachEvent(EventDetails eventDetails, List<Participant> participants) {
    this.eventDetails = eventDetails;
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

  public String toString() {
    var eventGiverHome = eventDetails.getEventGiver().getHome();
    var eventTarget = participants.get(1).npc();
    var eventTargetHome = eventTarget.getHome();
    return "From "
        + eventDetails.getEventGiver().getName()
        + ", "
        + eventGiverHome.getName()
        + " of "
        + eventGiverHome.getParent().getName()
        + ": Speak with "
        + eventTarget.getName()
        + ", "
        + eventTargetHome.getName()
        + " of "
        + eventTargetHome.getParent().getName()
        + " - Status: "
        + eventState;
  }
}
