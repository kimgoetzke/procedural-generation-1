package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;

import java.util.List;

public record Participant(Npc npc, Role role, List<Dialogue> dialogues) {

  public Participant(Npc npc, List<Dialogue> dialogues) {
    this(npc, Role.EVENT_GIVER, dialogues);
  }
}
