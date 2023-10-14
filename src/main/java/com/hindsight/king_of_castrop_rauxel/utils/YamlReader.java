package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.action.DialogueAction;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.event.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

@Slf4j
public class YamlReader {

  private static final String EVENT_GIVER = "eventGiver";
  private static final String EVENT_TARGET = "eventTarget";

  @SuppressWarnings("unchecked")
  public EventDto read(String fileName) {
    var inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
    var yaml = new Yaml();
    var data = (Map<String, Object>) yaml.load(inputStream);
    var eventDetails = parseEventDetails(data);
    if (eventDetails != null) {
      return switch (eventDetails.getEventType()) {
        case DIALOGUE -> readDialogueEvent(data, eventDetails);
        case REACH -> readReachEvent(data, eventDetails);
        case DEFEAT -> readDialogueEvent(data, eventDetails); // TODO: Implement DEFEAT event
      };
    }
    // If there are no eventDetails in the file, assume it's a multistep dialogue event
    return readDialogueEvent(data, new EventDetails());
  }

  @SuppressWarnings("unchecked")
  private static EventDetails parseEventDetails(Map<String, Object> data) {
    var detailsData = (List<Map<String, Object>>) data.get("eventDetails");
    var eventTypeData = (String) detailsData.get(0).get("eventType");
    var eventType = eventTypeData == null ? null : Event.Type.valueOf(eventTypeData);
    if (eventType == null) {
      return null;
    }
    var about = (String) detailsData.get(0).get("about");
    var rewardsList = new ArrayList<Reward>();
    var rewards = (List<Map<String, Object>>) detailsData.get(0).get("reward");
    if (rewards != null) {
      for (Map<String, Object> r : rewards) {
        var rewardTypeData = (String) r.get("type");
        var rewardType = rewardTypeData == null ? null : Reward.Type.valueOf(rewardTypeData);
        var reward = new Reward(rewardType, (int) r.get("min"), (int) r.get("max"));
        rewardsList.add(reward);
      }
    }
    return new EventDetails(eventType, about, rewardsList);
  }

  /** Returns a list with 2 dialogues lists, one for the npc and one for the target npc. */
  private static EventDto readReachEvent(Map<String, Object> data, EventDetails eventDetails) {
    var giverNpcDialogue = getDialoguesFor(EVENT_GIVER, data, eventDetails);
    var targetNpcDialogue = getDialoguesFor(EVENT_TARGET, data, eventDetails);
    return new EventDto(
        eventDetails,
        Map.of(Role.EVENT_GIVER, giverNpcDialogue, Role.EVENT_TARGET, targetNpcDialogue));
  }

  private static EventDto readDialogueEvent(Map<String, Object> data, EventDetails eventDetails) {
    return new EventDto(eventDetails, Map.of(Role.EVENT_GIVER, getDialoguesFrom(data)));
  }

  private static List<Dialogue> getDialoguesFor(
      String eventRole, Map<String, Object> data, EventDetails eventDetails) {
    var giverDialogueData = parseDataFor(eventRole, data);
    var giverNpcDialogue = getDialoguesFrom(giverDialogueData);
    giverNpcDialogue.get(0).setAbout(eventDetails.getAbout());
    return giverNpcDialogue;
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> parseDataFor(String key, Map<String, Object> data) {
    return (Map<String, Object>) data.get(key);
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
  private static void parseActions(Map<String, Object> interactionData, Interaction interaction) {
    var actions = (List<Map<String, Object>>) interactionData.get("actions");
    if (actions != null) {
      for (Map<String, Object> a : actions) {
        var eventStateData = (String) a.get("eventState");
        var eventState = eventStateData == null ? null : Event.State.valueOf(eventStateData);
        var playerStateData = (String) a.get("playerState");
        var playerState = playerStateData == null ? null : Player.State.valueOf(playerStateData);
        var action =
            DialogueAction.builder()
                .name((String) a.get("name"))
                .eventState(eventState)
                .playerState(playerState)
                .nextInteraction((Integer) a.get("nextInteraction"))
                .build();
        interaction.getActions().add(action);
      }
    }
  }
}