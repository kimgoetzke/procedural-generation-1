package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;

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

  default boolean hasNextInteraction() {
    return getDialogue().hasNextInteraction();
  }

  default Dialogue.Interaction getNextInteraction() {
    return getDialogue().getNextInteraction();
  }

  default void progress() {
    getDialogue().progress();
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
    PENDING,
    ACCEPT,
    DECLINE
  }
}
