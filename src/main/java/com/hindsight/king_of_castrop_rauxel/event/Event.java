package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;

public interface Event {

  Npc getNpc();

  EventType getType();

  void setState(EventState state);

  default void setComplete() {
    if (isRepeatable()) {
      setState(EventState.AVAILABLE);
      getDialogue().reset();
    } else {
      setState(EventState.COMPLETED);
    }
  }

  EventState getState();

  boolean isRepeatable();

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
