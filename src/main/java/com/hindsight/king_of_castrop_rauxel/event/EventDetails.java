package com.hindsight.king_of_castrop_rauxel.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public final class EventDetails {

  private final String id;
  private final Event.Type eventType;
  private final List<Reward> rewards;
  @Setter private String about;

  public EventDetails(Event.Type eventType, String about, List<Reward> rewards) {
    this.id = "EVT~" + UUID.randomUUID();
    this.eventType = eventType;
    this.about = about;
    this.rewards = rewards;
  }

  public EventDetails() {
    this.id = "EVT~" + UUID.randomUUID();
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
