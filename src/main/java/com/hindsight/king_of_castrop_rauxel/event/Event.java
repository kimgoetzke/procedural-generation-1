package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;

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

  Dialogue getDialogue();

  default boolean hasCurrentInteraction() {
    return getDialogue().hasCurrent();
  }

  default Dialogue.Interaction getCurrentInteraction() {
    return getDialogue().getCurrent();
  }

  default List<Action> getCurrentActions() {
    return getCurrentInteraction().actions();
  }

  default boolean hasNextInteraction() {
    return getDialogue().hasNext();
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
