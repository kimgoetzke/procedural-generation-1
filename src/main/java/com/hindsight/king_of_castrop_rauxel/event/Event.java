package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;

public interface Event {

  Npc getNpc();

  EventType getType();

  void setState(EventState state);

  EventState getState();

  Dialogue getDialogue();

  default boolean hasNext() {
    return getDialogue().hasNext();
  }

  default Dialogue.Interaction getNext() {
    return getDialogue().getNext();
  }

  default void progress() {
    getDialogue().progress();
  }

  enum EventState {
    UNAVAILABLE,
    AVAILABLE,
    ACTIVE,
    READY,
    COMPLETED
  }

  enum EventType {
    DIALOGUE,
    DEFEAT,
    REACH
  }
}
