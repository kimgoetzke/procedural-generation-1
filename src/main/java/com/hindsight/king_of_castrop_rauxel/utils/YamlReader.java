package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.DialogueAction;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.event.Dialogue;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hindsight.king_of_castrop_rauxel.event.Interaction;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

@Slf4j
public class YamlReader {

  private final String folder;
  private static final String FILE_EXTENSION = ".yml";

  public YamlReader(String folder) {
    this.folder = folder;
  }

  public List<Dialogue> readDialogueList(String fileName) {
    var inputStream =
        this.getClass().getClassLoader().getResourceAsStream(folder + fileName + FILE_EXTENSION);
    var yaml = new Yaml();
    Map<String, Object> data = yaml.load(inputStream);
    var dialogues = new ArrayList<Dialogue>();
    dialogues.add(parseDialogue(data, Event.State.AVAILABLE));
    dialogues.add(parseDialogue(data, Event.State.ACTIVE));
    dialogues.add(parseDialogue(data, Event.State.READY));
    dialogues.add(parseDialogue(data, Event.State.COMPLETED));
    dialogues.add(parseDialogue(data, Event.State.DECLINED));
    return dialogues;
  }

  public Dialogue readDialogue(String fileName) {
    var inputStream =
        this.getClass().getClassLoader().getResourceAsStream(folder + fileName + FILE_EXTENSION);
    var yaml = new Yaml();
    return parseDialogue(yaml.load(inputStream), Event.State.NONE);
  }

  @SuppressWarnings("unchecked")
  private static Dialogue parseDialogue(Map<String, Object> data, Event.State state) {
    var dialogue = new Dialogue(state);
    var interactions = (List<Map<String, Object>>) data.get(state.name().toLowerCase());
    for (Map<String, Object> interactionData : interactions) {
      var text = (String) interactionData.get("text");
      var n = (Integer) interactionData.get("n");
      var interaction = new Interaction(text, new ArrayList<>(), n);
      var actions = (List<Map<String, Object>>) interactionData.get("actions");
      if (actions != null) {
        for (Map<String, Object> a : actions) {
          var eventStateData = (String) a.get("eventState");
          Event.State eventState =
              eventStateData == null ? null : Event.State.valueOf(eventStateData);
          var playerStateData = (String) a.get("playerState");
          Player.State playerState =
              playerStateData == null ? null : Player.State.valueOf(playerStateData);
          Action action =
              DialogueAction.builder()
                  .index((int) a.get("index"))
                  .name((String) a.get("name"))
                  .eventState(eventState)
                  .playerState(playerState)
                  .nextInteraction((Integer) a.get("nextInteraction"))
                  .build();
          interaction.getActions().add(action);
        }
      }
      dialogue.getInteractions().add(interaction);
    }
    return dialogue;
  }
}
