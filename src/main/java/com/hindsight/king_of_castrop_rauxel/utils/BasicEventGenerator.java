package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.event.Dialogue;
import com.hindsight.king_of_castrop_rauxel.event.DialogueEvent;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicEventGenerator implements EventGenerator {

  private static final String FOLDER =
      "dialogues"
          + System.getProperty("file.separator")
          + "multistep"
          + System.getProperty("file.separator");
  private final TxtReader txtReader = new TxtReader(FOLDER);
  private final YamlReader yamlReader = new YamlReader(FOLDER);
  private Random random;

  public void setRandom(Random parentRandom) {
    random = parentRandom;
  }

  @Override
  public Event singleStepDialogue(Npc npc) {
    var pathName = "NPC-IDLE";
    var text = loopThroughFiles(pathName);
    var interactions = List.of(new Dialogue.Interaction(text, List.of(), null));
    var dialogue = new Dialogue(interactions);
    return new DialogueEvent(List.of(dialogue), npc, true);
  }

  @Override
  public Event multiStepDialogue(Npc npc) {
    var dialogues = yamlReader.readDialogueList("parcel");
    return new DialogueEvent(dialogues, npc, true);
  }

  private String loopThroughFiles(String pathName) {
    var result = txtReader.read(pathName);
    if (!result.isEmpty()) {
      return txtReader.getRandom(result, random).trim();
    }
    throw new IllegalArgumentException("No file found for path name '%s'".formatted(pathName));
  }
}
