package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.event.Event;

public interface EventGenerator extends Generator {

  Event singleStepDialogue(Npc npc);

  Event multiStepDialogue(Npc npc);

  Event deliveryEvent(Npc npc);
}
