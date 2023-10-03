package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.DialogueAction;
import com.hindsight.king_of_castrop_rauxel.event.Dialogue;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import static com.hindsight.king_of_castrop_rauxel.event.Dialogue.*;

@Slf4j
public class YamlReader {

  private final String folder;
  private static final String FILE_EXTENSION = ".yml";

  public YamlReader(String folder) {
    this.folder = folder;
  }

  public Dialogue read(String fileName) {
    var inputStream =
        this.getClass().getClassLoader().getResourceAsStream(folder + fileName + FILE_EXTENSION);
    var yaml = new Yaml();
    return parseDialogue(yaml.load(inputStream));
  }

  @SuppressWarnings("unchecked")
  private static Dialogue parseDialogue(Map<String, Object> data) {
    var dialogue = new Dialogue();
    var interactions = (List<Map<String, Object>>) data.get("interactions");
    for (Map<String, Object> interactionData : interactions) {
      var text = (String) interactionData.get("text");
      var n = (Integer) interactionData.get("n");
      var interaction = new Interaction(text, new ArrayList<>(), n);
      var actions = (List<Map<String, Object>>) interactionData.get("actions");
      if (actions != null) {
        for (Map<String, Object> a : actions) {
          Action action =
              DialogueAction.builder()
                  .index((int) a.get("index"))
                  .name((String) a.get("name"))
                  .choice(Event.EventChoice.valueOf((String) a.get("choice")))
                  .nextInteraction((int) a.get("nextInteraction"))
                  .build();
          interaction.actions().add(action);
        }
      }

      dialogue.getInteractions().add(interaction);
    }

    return dialogue;
  }
}
