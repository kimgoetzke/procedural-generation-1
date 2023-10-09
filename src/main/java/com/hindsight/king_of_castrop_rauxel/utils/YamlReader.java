package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.DialogueAction;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.event.Dialogue;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.event.Interaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hindsight.king_of_castrop_rauxel.event.Reward;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

@Slf4j
public class YamlReader {

  private final String folder;
  private static final String FILE_EXTENSION = ".yml";

  public YamlReader(String folder) {
    this.folder = folder;
  }

  public List<List<Dialogue>> read(String fileName) {
    var inputStream =
        this.getClass().getClassLoader().getResourceAsStream(folder + fileName + FILE_EXTENSION);
    var yaml = new Yaml();
    Map<String, Object> data = yaml.load(inputStream);
    var settings = parseSettings(data);
    if (settings != null) {
      return switch (settings.eventType) {
        case DIALOGUE -> List.of(getDialoguesFrom(data));
        case REACH -> readReachEvent(data, settings.about);
        case DEFEAT -> List.of(getDialoguesFrom(data)); // TODO: Implement DEFEAT event
      };
    }
    // If no settings in Yaml file, assume it's a multi-step dialogue event
    return List.of(getDialoguesFrom(data));
  }

  @SuppressWarnings("unchecked")
  private static YamlSettings parseSettings(Map<String, Object> data) {
    var settingsData = (List<Map<String, Object>>) data.get("settings");
    var eventTypeData = (String) settingsData.get(0).get("eventType");
    Event.Type eventType = eventTypeData == null ? null : Event.Type.valueOf(eventTypeData);
    if (eventType == null) {
      return null;
    }
    var about = (String) settingsData.get(0).get("about");
    var rewardsList = new ArrayList<Reward>();
    var rewards = (List<Map<String, Object>>) settingsData.get(0).get("reward");
    if (rewards != null) {
      for (Map<String, Object> r : rewards) {
        var rewardTypeData = (String) r.get("type");
        Reward.Type rewardType =
          rewardTypeData == null ? null : Reward.Type.valueOf(rewardTypeData);
        Reward reward =
          Reward.builder()
            .type(rewardType)
            .minValue((int) r.get("min"))
            .minValue((int) r.get("max"))
            .build();
        rewardsList.add(reward);
      }
    }
    return new YamlSettings(eventType, about, rewardsList);
  }

  /** Returns a list with 2 dialogues lists, one for the npc and one for the target npc. */
  private static List<List<Dialogue>> readReachEvent(Map<String, Object> data, String about) {
    var npcDialogueData = parseDataFrom(data, "npc");
    var npcDialogue = getDialoguesFrom(npcDialogueData);
    npcDialogue.get(0).setAbout(about);
    var targetDialogueData = parseDataFrom(data, "targetNpc");
    var targetNpcDialogue = getDialoguesFrom(targetDialogueData);
    targetNpcDialogue.get(0).setAbout(about);
    return List.of(npcDialogue, targetNpcDialogue);
  }

  private static List<Dialogue> getDialoguesFrom(Map<String, Object> data) {
    var dialogues = new ArrayList<Dialogue>();
    dialogues.add(parseDialogue(data, Event.State.AVAILABLE));
    dialogues.add(parseDialogue(data, Event.State.ACTIVE));
    dialogues.add(parseDialogue(data, Event.State.READY));
    dialogues.add(parseDialogue(data, Event.State.COMPLETED));
    dialogues.add(parseDialogue(data, Event.State.DECLINED));
    return dialogues;
  }

  @SuppressWarnings("unchecked")
  private static Dialogue parseDialogue(Map<String, Object> data, Event.State state) {
    var dialogue = new Dialogue(state);
    var interactions = (List<Map<String, Object>>) data.get(state.name().toLowerCase());
    for (Map<String, Object> interactionData : interactions) {
      var text = (String) interactionData.get("text");
      var n = (Integer) interactionData.get("n");
      var interaction = new Interaction(text, new ArrayList<>(), n);
      parseActions(interactionData, interaction);
      dialogue.getInteractions().add(interaction);
    }
    return dialogue;
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> parseDataFrom(Map<String, Object> data, String key) {
    return (Map<String, Object>) data.get(key);
  }

  @SuppressWarnings("unchecked")
  private static void parseActions(Map<String, Object> interactionData, Interaction interaction) {
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
  }

  private record YamlSettings(Event.Type eventType, String about, List<Reward> rewards) {}

}
