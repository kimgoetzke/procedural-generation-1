package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.characters.Player;

import java.util.List;

/**
 * Events are linked to NPCs. Each NPC owns at least one event. Events can be of different types,
 * such as dialogues (single vs multistep dialogues), defeat quests or reach quests.
 */
public interface Event {

  Npc getNpc();

  EventType getType();

  void setState(EventState state);

  default void setComplete() {
    if (isRepeatable()) {
      setState(EventState.AVAILABLE);
      reset();
    } else {
      setState(EventState.COMPLETED);
    }
  }

  EventState getState();

  boolean isRepeatable();

  default boolean isBeginningOfDialogue() {
    return getState() == EventState.AVAILABLE;
  }

  Dialogue getDialogue();

  default boolean hasCurrentInteraction() {
    return getDialogue().hasCurrent();
  }

  default void setCurrentInteraction(int i) {
    if (i > getDialogue().getInteractions().size()) {
      throw new IllegalArgumentException("The next interaction index is out of bounds: " + i);
    }
    getDialogue().setCurrent(i);
  }

  default Dialogue.Interaction getCurrentInteraction() {
    return getDialogue().getCurrent();
  }

  default List<Action> getCurrentActions() {
    return getCurrentInteraction().actions();
  }

  default void progressDialogue(Player player) {
    getDialogue().progress(player);
  }

  default void reset() {
    getDialogue().reset();
  }

  enum EventState {
    UNAVAILABLE,
    AVAILABLE,
    ACTIVE,
    READY,
    COMPLETED,
    DECLINED
  }

  enum EventType {
    DIALOGUE,
    DEFEAT,
    REACH
  }

  enum EventChoice {
    ACCEPT,
    DECLINE
  }
}
