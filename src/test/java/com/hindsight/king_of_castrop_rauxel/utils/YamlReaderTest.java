package com.hindsight.king_of_castrop_rauxel.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.DialogueAction;
import com.hindsight.king_of_castrop_rauxel.event.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

class YamlReaderTest extends YamlReader {

  public static final int COUNT = 2;

  private Yaml underTest;

  @BeforeEach
  void setUp() {
    var options = new LoaderOptions();
    var representer = getRepresenter();
    var constructor = getConstructor(options);
    underTest = new Yaml(constructor, representer, new DumperOptions());
  }

  @Test
  void writeObjectToYaml() {
    var eventDto = getEventDto();
    var data = underTest.dumpAsMap(eventDto);
    var dialogues = data.split("!dialogue", -1).length - 1;
    var actions = data.split("!action", -1).length - 1;
    assertThat(data).isNotNull();
    assertThat(dialogues).isEqualTo(12);
    assertThat(actions).isEqualTo(48);
  }

  @Test
  void whenValidYaml_readYamlToEventDto() {
    // ...
    var eventDto = getEventDto();
    var yaml = "";
    var data = underTest.load(yaml);
    // ...
  }

  private EventDto getEventDto() {
    var eventDetails = new EventDetails();
    var participantsData = new EnumMap<Role, List<Dialogue>>(Role.class);
    participantsData.put(Role.EVENT_GIVER, getDialogues());
    participantsData.put(Role.EVENT_TARGET, getDialogues());
    return new EventDto(eventDetails, participantsData);
  }

  private List<Dialogue> getDialogues() {
    var dialogues = new ArrayList<Dialogue>();
    for (var state : Event.State.values()) {
      var dialogue = new Dialogue();
      dialogue.setState(state);
      dialogue.setInteractions(getInteractions());
      dialogues.add(dialogue);
    }
    return dialogues;
  }

  private List<Interaction> getInteractions() {
    var interactions = new ArrayList<Interaction>();
    for (int i = 0; i < COUNT; i++) {
      var actions = getActions();
      var interaction = new Interaction("Hello %s!".formatted(i), actions);
      interactions.add(interaction);
    }
    return interactions;
  }

  private List<Action> getActions() {
    var actions = new ArrayList<Action>();
    for (int j = 0; j < COUNT; j++) {
      actions.add(DialogueAction.builder().index(2).name("Say " + j).build());
    }
    return actions;
  }
}
