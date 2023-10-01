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

  private static final String FOLDER = "dialogues" + System.getProperty("file.separator");
  private static final String FILE_EXTENSION = ".txt";
  private final FileProcessor fileProcessor = new FileProcessor(FOLDER, FILE_EXTENSION);
  private Random random;

  public void setRandom(Random parentRandom) {
    random = parentRandom;
  }

  @Override
  public Event simpleDialogueEvent(Npc npc) {
    var pathName = "NPC-IDLE";
    var text = loopThroughFiles(pathName);
    var dialogue = new Dialogue(List.of(new Dialogue.Interaction(text, List.of())));
    return new DialogueEvent(dialogue, npc, Event.EventType.DIALOGUE, true);
  }

  private String loopThroughFiles(String pathName) {
    var result = fileProcessor.readLinesFromFile(pathName);
    if (!result.isEmpty()) {
      return fileProcessor.getRandomLine(result, random).trim();
    }
    throw new IllegalArgumentException("No file found for path name '%s'".formatted(pathName));
  }
}
