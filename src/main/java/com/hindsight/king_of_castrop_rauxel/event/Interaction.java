package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Interaction {

  private List<Action> actions = new ArrayList<>();
  private Integer nextInteraction;
  @Setter private String text;

  public Interaction(String text, List<Action> actions) {
    this.text = text;
    this.actions = actions;
    this.nextInteraction = null;
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
