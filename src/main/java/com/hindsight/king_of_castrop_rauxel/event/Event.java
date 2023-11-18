package com.hindsight.king_of_castrop_rauxel.event;

import static com.google.common.base.Preconditions.checkArgument;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Events are linked to NPCs. Each NPC has at least one primary event. Events can be of different
 * types, such as dialogues (single vs multistep dialogues), defeat events or reach events.
 */
public interface Event {

  EventDetails getEventDetails();

  List<Participant> getParticipants();

  default List<Npc> getParticipantNpcs() {
    return getParticipants().stream().map(Participant::npc).toList();
  }

  default void setActive(Npc npc) {
    var participant =
        getParticipants().stream().filter(p -> p.npc().equals(npc)).findFirst().orElseThrow();
    setActive(participant);
  }

  default void setActive(Participant participant) {
    var dialogue =
        participant.dialogues().stream()
            .filter(d -> d.getState() == getEventState() || d.getState() == State.NONE)
            .findFirst()
            .orElseThrow();
    setCurrentDialogue(dialogue);
    setCurrentNpc(participant.npc());
  }

  void setCurrentNpc(Npc npc);

  Npc getCurrentNpc();

  /**
   * Ideally, this method is only be used inside this interface. The intended way of changing the
   * event state is by using the {@link #progressEvent(State)} or {@link #completeEvent(Player)}
   * methods.
   */
  void setEventState(State state);

  State getEventState();

  boolean isRepeatable();

  /**
   * This method is only called by DialogueAction when changing the event state. Current interaction
   * is set to -1 because it'll be incremented by 1 during the DialogLoop's post-processing which is
   * before any interaction is displayed. Making this more fool-proof is would be critical if this
   * project had * a bigger scope.
   */
  default void progressEvent(State state) {
    setEventState(state);
    setCurrentDialogue(getDialogue(state));
    setCurrentInteraction(-1);
  }

  /**
   * At this point, only by DialogueAction when completing a quest. Current interaction is set to -1
   * because it'll be incremented by 1 during the DialogLoop's post-processing which is before any
   * interaction is displayed. Making this more fool-proof is would be critical if this project had
   * a bigger scope.
   */
  default void completeEvent(Player player) {
    if (isRepeatable()) {
      resetEvent();
    } else {
      setEventState(Event.State.COMPLETED);
      setCurrentDialogue(getDialogue(Event.State.COMPLETED));
      setCurrentInteraction(-1);
    }
    if (getEventDetails().hasRewards()) {
      getEventDetails().getRewards().forEach(r -> r.give(player));
    }
    player.setCurrentEvent(null);
    player.setState(Player.State.AT_POI);
  }

  default void resetEvent() {
    getParticipants().forEach(p -> p.dialogues().forEach(Dialogue::reset));
    setCurrentDialogue(getDialogue(Event.State.AVAILABLE));
    setEventState(Event.State.AVAILABLE);
  }

  default List<Dialogue> getCurrentNpcDialogues() {
    return getParticipants().stream()
        .filter(p -> p.npc().equals(getCurrentNpc()))
        .findFirst()
        .orElseThrow()
        .dialogues();
  }

  default Dialogue getDialogue(State state) {
    return getCurrentNpcDialogues().stream()
        .filter(d -> d.getState() == state)
        .findFirst()
        .orElseThrow();
  }

  Dialogue getCurrentDialogue();

  void setCurrentDialogue(Dialogue dialogue);

  default boolean hasNextDialogue() {
    if (getCurrentDialogue().getState() == Event.State.NONE) {
      return false;
    }
    return getCurrentDialogue().getState().ordinal() < Event.State.values().length - 1;
  }

  default void progressDialogue() {
    getCurrentDialogue().progress();
  }

  default void rewindBy(int relativeStep) {
    getCurrentDialogue().rewindBy(relativeStep);
  }

  default void resetDialogue() {
    getCurrentDialogue().reset();
  }

  /**
   * Returns true if the current interaction is the first interaction of the dialogue, regardless of
   * the state.
   */
  default boolean isBeginningOfDialogue() {
    return getCurrentDialogue().isFirstInteraction();
  }

  /**
   * Used to filter out events that not primary events or have no action associated at this point
   * before displaying them to the player. Note that events require dialogues for all stages, even
   * stages that should be filtered out with this method which makes calling this method optional.
   * However, it is recommended to use this method to improve the game experience.
   */
  default boolean isDisplayable(Npc npc) {
    var primaryEvent = npc.getPrimaryEvent();
    if (primaryEvent == null) {
      return false;
    }
    if (primaryEvent.getEventDetails() == getEventDetails()) {
      return true;
    }
    return getEventState() == Event.State.ACTIVE
        || getEventState() == Event.State.READY
        || getEventState() == Event.State.NONE;
  }

  default boolean hasCurrentInteraction() {
    return getCurrentDialogue().hasCurrentInteraction();
  }

  default void setCurrentInteraction(int i) {
    var isOutOfBounds = i > getCurrentDialogue().getInteractions().size();
    checkArgument(!isOutOfBounds, "The next interaction index is out of bounds: ", i);
    getCurrentDialogue().setCurrentInteraction(i);
  }

  default Interaction getCurrentInteraction() {
    return getCurrentDialogue().getCurrentInteraction();
  }

  default List<Action> getCurrentActions() {
    if (!hasCurrentInteraction()) {
      return List.of();
    }
    return getCurrentInteraction().getActions();
  }

  default boolean hasNextInteraction() {
    return getCurrentDialogue().hasNextInteraction();
  }

  @Getter
  @Slf4j
  enum State {
    NONE("None", 0),
    AVAILABLE("Available", 1),
    ACTIVE("Active", 2),
    READY("Ready", 3),
    COMPLETED("Completed", 4),
    DECLINED("Declined", 5);

    private final String name;
    private final int ordinal;

    State(String name, int ordinal) {
      this.name = name;
      this.ordinal = ordinal;
    }
  }

  enum Type {
    DIALOGUE,
    DEFEAT,
    REACH
  }
}
