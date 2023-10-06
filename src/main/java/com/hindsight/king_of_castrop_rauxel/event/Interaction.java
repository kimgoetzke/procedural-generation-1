package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class Interaction {

  private final List<Action> actions;
  private final Integer nextInteraction;
  @Setter private String text;

  public Interaction(String text, List<Action> actions, Integer nextInteraction) {
    this.text = text;
    this.actions = actions;
    this.nextInteraction = nextInteraction;
  }

  @Override
  public String toString() {
    return "Interaction(text="
        + text
        + ", actions="
        + actions.size()
        + ", nextInteraction="
        + nextInteraction
        + ")";
  }
}
