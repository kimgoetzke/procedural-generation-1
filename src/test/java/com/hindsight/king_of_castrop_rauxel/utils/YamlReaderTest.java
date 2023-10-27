package com.hindsight.king_of_castrop_rauxel.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.DialogueAction;
import com.hindsight.king_of_castrop_rauxel.event.*;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

class YamlReaderTest extends YamlReader {

  private static final int COUNT = 2;
  private static final String EXPECTED_TEXT = "EXPECTED_TEXT";
  private static final String EXPECTED_ACTION =
      "DialogueAction(index=0, name=EXPECTED_TEXT, eventState=NONE, playerState=null, nextInteraction=2)";

  /** This test is largely used to generate the expected structure for Yaml files. */
  @Test
  void writeObjectToYaml() {
    var options = new LoaderOptions();
    var representer = getRepresenter();
    representer.addClassTag(Dialogue.class, new Tag(DIALOGUE_TAG));
    representer.addClassTag(DialogueAction.class, new Tag(ACTION_TAG));
    var constructor = getConstructor(options);
    var yaml = new Yaml(constructor, representer, new DumperOptions());
    var eventDto = getEventDto();
    var data = yaml.dumpAsMap(eventDto);
    var dialogues = data.split("!dialogue", -1).length - 1;
    var actions = data.split("!action", -1).length - 1;
    assertThat(data).isNotNull();
    assertThat(dialogues).isEqualTo(12);
    assertThat(actions).isEqualTo(48);
  }

  @Test
  void whenValidYaml_readYamlToEventDto() {
    var underTest = new YamlReader();
    var data = underTest.read("yaml-reader-test-file.yml");
    assertThat(data).isNotNull();
    assertThat(data.eventDetails).isNotNull();
    assertThat(data.eventDetails.getEventType()).isEqualTo(Event.Type.REACH);
    assertThat(data.eventDetails.getAbout()).isEqualTo(EXPECTED_TEXT);
    assertThat(data.eventDetails.getRewards().get(0).getType()).isEqualTo(Reward.Type.GOLD);
    assertThat(data.eventDetails.getRewards().get(0).getMinValue()).isEqualTo(10);
    assertThat(data.eventDetails.getRewards().get(0).getMaxValue()).isEqualTo(15);
    assertThat(data.participantData).isNotNull();
    var giverDialogues = data.participantData.get(Role.EVENT_GIVER);
    assertThat(giverDialogues).hasSize(5);
    assertThat(data.participantData.get(Role.EVENT_TARGET)).hasSize(5);
    var giverInteraction1 = giverDialogues.get(0).getInteractions().get(1);
    assertThat(giverDialogues.get(0).getInteractions()).hasSize(4);
    assertThat(giverInteraction1.getText()).isEqualTo(EXPECTED_TEXT);
    assertThat(giverInteraction1.getActions()).hasSize(2);
    assertThat(giverInteraction1.getActions().get(0).getName()).isEqualTo(EXPECTED_TEXT);
    assertThat(giverInteraction1.getActions().get(0).toString()).hasToString(EXPECTED_ACTION);
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
