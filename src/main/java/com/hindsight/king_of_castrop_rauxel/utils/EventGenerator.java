package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.character.Npc;
import com.hindsight.king_of_castrop_rauxel.event.Event;

public interface EventGenerator extends Generator {

  Event generate(Npc npc);
}
