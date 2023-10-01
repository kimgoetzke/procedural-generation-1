package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.action.DialogueAction;
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

  @Override
  public Event multistepDialogueEvent(Npc npc) {
    var i1 =
        new Dialogue.Interaction(
            "Hey you, what do you want?%n%nYou look like you'll be trouble.", List.of());
    var i2 =
        new Dialogue.Interaction(
            "Are you going to be trouble?!",
            List.of(
                DialogueAction.builder()
                    .index(1)
                    .name("Yes")
                    .choice(Event.EventChoice.ACCEPT)
                    .nextInteraction(3)
                    .build(),
                DialogueAction.builder()
                    .index(2)
                    .name("No")
                    .nextInteraction(4)
                    .choice(Event.EventChoice.DECLINE)
                    .build()));
    var i3 = new Dialogue.Interaction("I like you, mate.", List.of());
    var i4 = new Dialogue.Interaction("You're a weak little shit, aren't you?", List.of());
    var dialogue = new Dialogue(List.of(i1, i2, i3, i4));
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
