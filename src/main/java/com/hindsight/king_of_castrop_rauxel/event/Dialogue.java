package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import java.util.ArrayList;
import java.util.List;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import lombok.Getter;

public final class Dialogue {

  @Getter private final List<Interaction> interactions;
  private int current = 0;

  public Dialogue(List<Interaction> interactions) {
    this.interactions = interactions;
  }

  public Dialogue() {
    this.interactions = new ArrayList<>();
  }

  boolean hasCurrent() {
    return current < interactions.size();
  }

  Interaction getCurrent() {
    return interactions.get(current);
  }

  void setCurrent(int i) {
    current = i;
  }

  boolean hasNext() {
    return current + 1 < interactions.size();
  }

  void progress(Player player) {
    if (!hasNext()) {
      complete(player);
      return;
    }
    var linkedInteraction = getInteractions().get(current).nextInteraction();
    if (linkedInteraction == null) {
      current++;
      return;
    }
    if (linkedInteraction > getInteractions().size()) {
      complete(player);
      return;
    }
    current = getInteractions().get(current).nextInteraction();
  }

  void complete(Player player) {
    player.getCurrentEvent().setComplete();
    player.setCurrentEvent(null);
    player.setState(Player.PlayerState.AT_POI);
  }

  void reset() {
    current = 0;
  }

  @Override
  public String toString() {
    return "Dialogue(interactions=" + interactions + ", current=" + current + ")";
  }

  public record Interaction(String text, List<Action> actions, Integer nextInteraction) {
    @Override
    public String toString() {
      return "Interaction(text=" + text + ", actions=" + actions.size() + ")";
    }
  }
}
