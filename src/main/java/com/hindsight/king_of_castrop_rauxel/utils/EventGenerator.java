package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.event.Event;

import java.util.Random;

public interface EventGenerator {

  void setRandom(Random random);

  Event simpleDialogueEvent(Npc npc);
}
