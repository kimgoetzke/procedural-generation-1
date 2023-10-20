package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.PoiAction;
import com.hindsight.king_of_castrop_rauxel.event.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

class YamlReaderTest {

  private final Random random = new Random();

  @Test
  void write() {
    var eventDetails = new EventDetails();
    var participantsData = new EnumMap<Role, List<Dialogue>>(Role.class);
    participantsData.put(Role.EVENT_GIVER, getDialogues());
    participantsData.put(Role.EVENT_TARGET, getDialogues());
    var eventDto = new EventDto(eventDetails, participantsData);
    var yaml = new Yaml();
    var data = yaml.dumpAsMap(eventDto);
    System.out.println(data);
  }

  private List<Dialogue> getDialogues() {
    var dialogues = new ArrayList<Dialogue>();
    for (var state : Event.State.values()) {
      var dialogue = new Dialogue();
      dialogue.setState(state);
      dialogue.setInteractions(getInteractions(randomInt(1, 4)));
      dialogues.add(dialogue);
    }
    return dialogues;
  }

  private List<Interaction> getInteractions(int count) {
    var interactions = new ArrayList<Interaction>();
    for (int i = 0; i < count; i++) {
      var actions = getActions(randomInt(1, 3));
      var interaction = new Interaction("Hello %s!".formatted(i), actions);
      interactions.add(interaction);
    }
    return interactions;
  }

  private List<Action> getActions(int count) {
    var actions = new ArrayList<Action>();
    for (int j = 0; j < count; j++) {
      actions.add(PoiAction.builder().index(2).name("Go to POI " + j).build());
    }
    return actions;
  }

  private int randomInt(int min, int max) {
    return random.nextInt(max - min + 1) + min;
  }
}
