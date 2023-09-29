package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.event.Dialogue;
import com.hindsight.king_of_castrop_rauxel.event.DialogueEvent;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class BasicEventGenerator implements EventGenerator {

  private static final String FOLDER = "dialogues" + System.getProperty("file.separator");
  private static final String FILE_EXTENSION = ".txt";
  private Random random;

  public void setRandom(Random parentRandom) {
    random = parentRandom;
  }

  @Override
  public Event emptyDialogueEvent(Npc npc) {
    var pathName = "NPC-IDLE";
    var text = loopThroughFiles(pathName);
    var dialogue = new Dialogue(List.of(new Dialogue.Interaction(text, List.of())));
    return new DialogueEvent(dialogue, npc, Event.EventType.DIALOGUE);
  }

  private String loopThroughFiles(String pathName) {
    var result = readLinesFromFile(pathName);
    if (!result.isEmpty()) {
      return getRandomLine(result).trim();
    }
    throw new IllegalArgumentException("No file found for path name '%s'".formatted(pathName));
  }

  private List<String> readLinesFromFile(String fileName) {
    InputStream inputStream =
        BasicEventGenerator.class
            .getClassLoader()
            .getResourceAsStream(FOLDER + fileName + FILE_EXTENSION);
    if (inputStream != null) {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        return reader.lines().map(String::trim).toList();
      } catch (IOException e) {
        log.warn("File '{}' not found", fileName);
      }
    }
    return new ArrayList<>();
  }

  private String getRandomLine(List<String> lines) {
    int randomIndex = random.nextInt(lines.size());
    return lines.get(randomIndex);
  }
}
