package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.world.IdBuilder;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public final class EventDetails {

  private String id;
  private Event.Type eventType;
  private List<Reward> rewards;
  private String about;

  public EventDetails() {
    this.id = IdBuilder.idFrom(this.getClass());
    this.eventType = Event.Type.DIALOGUE;
    this.about = "";
    this.rewards = List.of();
  }

  public boolean hasRewards() {
    return rewards != null && !rewards.isEmpty();
  }

  @Override
  public String toString() {
    return "EventDetails(id=" + id + ", eventType=" + eventType + ", rewards=" + rewards + ")";
  }
}
