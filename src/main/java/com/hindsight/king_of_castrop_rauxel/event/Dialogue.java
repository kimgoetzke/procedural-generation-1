package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.action.Action;

import java.util.List;

public final class Dialogue {

  private final List<Interaction> interactions;
  private int nextInteraction;

  public Dialogue(List<Interaction> interactions) {
    this.interactions = interactions;
    this.nextInteraction = 0;
  }

  public boolean hasNext() {
    return nextInteraction < interactions.size();
  }

  public Interaction getNext() {
    return interactions.get(nextInteraction);
  }

  public void progress() {
    nextInteraction++;
  }

  public record Interaction(String text, List<Action> actions) {}
}