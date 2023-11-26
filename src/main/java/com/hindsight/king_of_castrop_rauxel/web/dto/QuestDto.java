package com.hindsight.king_of_castrop_rauxel.web.dto;

import com.hindsight.king_of_castrop_rauxel.character.Npc;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.event.Role;
import lombok.Data;

@Data
public class QuestDto {

  private final String about;
  private final Event.Type eventType;
  private final Event.State eventState;
  private final QuestParticipant questGiver;
  private final QuestParticipant questTarget;

  public QuestDto(Event event) {
    var name = event.getEventDetails().getAboutGiver();
    this.about = name.isEmpty() ? "A dialogue" : name;
    this.eventType = event.getEventDetails().getEventType();
    this.eventState = event.getEventState();
    this.questGiver =
        event.getParticipants().stream()
            .filter(p -> p.role().equals(Role.EVENT_GIVER))
            .findFirst()
            .map(p -> QuestParticipant.from(p.npc()))
            .orElse(null);
    this.questTarget =
        event.getParticipants().stream()
            .filter(p -> p.role().equals(Role.EVENT_TARGET))
            .findFirst()
            .map(p -> QuestParticipant.from(p.npc()))
            .orElse(null);
  }

  public record QuestParticipant(String name, String location, String poi) {

    public static QuestParticipant from(Npc npc) {
      return new QuestParticipant(
          npc.getName(), npc.getHome().getParent().getName(), npc.getHome().getName());
    }
  }
}
