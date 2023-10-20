package com.hindsight.king_of_castrop_rauxel.event;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public final class Dialogue {

  @Setter @Getter private List<Interaction> interactions = new ArrayList<>();
  @Setter @Getter private Event.State state;
  private int current = 0;

  public Dialogue(List<Interaction> interactions) {
    this.interactions.addAll(interactions);
    this.state = Event.State.NONE;
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

  boolean hasNextInteraction() {
    return current < interactions.size() - 1;
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
