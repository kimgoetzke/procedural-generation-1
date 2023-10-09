package com.hindsight.king_of_castrop_rauxel.event;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public final class Dialogue {

  @Getter private final List<Interaction> interactions;
  @Getter private final Event.State state;
  private int current = 0;

  public Dialogue(List<Interaction> interactions) {
    this.interactions = interactions;
    this.state = Event.State.NONE;
  }

  public Dialogue(Event.State state) {
    this.interactions = new ArrayList<>();
    this.state = state;
  }

  boolean hasCurrentInteraction() {
    return current >= 0 && current < interactions.size();
  }

  Interaction getCurrentInteraction() {
    return interactions.get(current);
  }

  void setCurrentInteraction(int i) {
    current = i;
  }

  boolean isFirstInteraction() {
    return current == 0;
  }

  void progress() {
    var next = hasCurrentInteraction() ? getCurrentInteraction().getNextInteraction() : null;
    if (next == null) {
      current++;
      return;
    }
    if (next > getInteractions().size()) {
      reset();
      return;
    }
    setCurrentInteraction(next);
  }

  void rewindBy(int i) {
    current -= i;
    if (current < 0) {
      current = 0;
    }
  }

  void reset() {
    current = 0;
  }

  @Override
  public String toString() {
    return "Dialogue(interactions=" + interactions + ", current=" + current + ")";
  }
}
