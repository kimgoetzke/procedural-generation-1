package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.event.*;

import java.util.List;
import java.util.Random;

import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicEventGenerator implements EventGenerator {

  public static final String LINE_SEPARATOR = System.getProperty("file.separator");
  private static final String BASE_FOLDER = "events" + LINE_SEPARATOR;
  private static final String MULTI_STEP_FOLDER = "multi-step" + LINE_SEPARATOR;
  private static final String REACH_FOLDER = "reach" + LINE_SEPARATOR;
  private static final String SINGLE_STEP_FOLDER = "single-step" + LINE_SEPARATOR;
  private static final int MAX_ATTEMPTS = 3;
  private final TxtReader txtReader = new TxtReader(BASE_FOLDER);
  private final YamlReader yamlReader = new YamlReader(BASE_FOLDER);
  private final PlaceholderProcessor processor = new PlaceholderProcessor();
  private Random random;

  public void setRandom(Random parentRandom) {
    random = parentRandom;
    processor.setRandom(parentRandom);
  }

  @Override
  public Event singleStepDialogue(Npc npc) {
    var pathName = SINGLE_STEP_FOLDER + "NPC-IDLE";
    var text = readRandomLineFromFile(pathName);
    var interactions = List.of(new Interaction(text, List.of(), null));
    var dialogues = List.of(new Dialogue(interactions));
    var participants = List.of(new Participant(npc, dialogues));
    processPlaceholders(npc, null, dialogues);
    return new DialogueEvent(new EventDetails(), participants, true);
  }

  private String readRandomLineFromFile(String pathName) {
    var result = txtReader.read(pathName);
    if (!result.isEmpty()) {
      return txtReader.getRandom(result, random).trim();
    }
    throw new IllegalArgumentException("No file found for path name '%s'".formatted(pathName));
  }

  @Override
  public Event multiStepDialogue(Npc npc) {
    var eventDto = yamlReader.read(MULTI_STEP_FOLDER + "a-close-friends-parcel");
    var dialogues = eventDto.participantData.get(Role.EVENT_GIVER);
    var participants = List.of(new Participant(npc, dialogues));
    var eventDetails = eventDto.eventDetails;
    initialiseRewards(eventDetails);
    processPlaceholders(npc, null, dialogues);
    return new DialogueEvent(eventDetails, participants, true);
  }

  private void initialiseRewards(EventDetails eventDetails) {
    for (var reward : eventDetails.getRewards()) {
      reward.load(random);
      log.info("Initialised reward: {}", reward);
    }
  }

  @Override
  public Event deliveryEvent(Npc npc) {
    var eventDto = yamlReader.read(REACH_FOLDER + "a-close-friends-parcel");
    var giverNpcDialogues = eventDto.participantData.get(Role.EVENT_GIVER);
    var giverParticipant = new Participant(npc, giverNpcDialogues);
    var targetNpcDialogues = eventDto.participantData.get(Role.EVENT_TARGET);
    var targetPoi = tryToFindAPoi(npc);
    if (targetPoi != null) {
      var targetNpc = targetPoi.getNpc();
      var targetParticipant = new Participant(targetNpc, targetNpcDialogues);
      var participants = List.of(giverParticipant, targetParticipant);
      initialiseRewards(eventDto.eventDetails);
      processPlaceholders(npc, targetNpc, eventDto);
      processPlaceholders(npc, targetNpc, giverNpcDialogues);
      processPlaceholders(npc, targetNpc, targetNpcDialogues);
      processPlaceholders(eventDto, List.of(giverNpcDialogues, targetNpcDialogues));
      return new ReachEvent(eventDto.eventDetails, participants, targetPoi);
    }
    return null;
  }

  private PointOfInterest tryToFindAPoi(Npc npc) {
    for (int i = 0; i < MAX_ATTEMPTS; i++) {
      var poi = findPoiInSameLocation(npc);
      if (poi != null) {
        return poi;
      }
    }
    log.warn("Cannot generate DeliveryEvent - no POI available for {}", npc);
    return null;
  }

  private PointOfInterest findPoiInSameLocation(Npc npc) {
    var pointsOfInterest = npc.getHome().getParent().getPointsOfInterest();
    var randomNumber = random.nextInt(0, pointsOfInterest.size());
    var poi = pointsOfInterest.get(randomNumber);
    var hasNoNpc = poi.getNpc() == null;
    var isSamePoi = npc.getHome().equals(poi);
    if (hasNoNpc || isSamePoi) {
      return null;
    }
    return poi;
  }

  private void processPlaceholders(Npc npc, Npc targetNpc, EventDto eventDto) {
    var eventDetails = eventDto.eventDetails;
    var about = processor.process(eventDetails.getAbout(), npc, targetNpc);
    eventDetails.setAbout(about);
  }

  private void processPlaceholders(Npc npc, Npc targetNpc, List<Dialogue> dialogues) {
    for (var dialogue : dialogues) {
      for (var interaction : dialogue.getInteractions()) {
        processText(npc, targetNpc, interaction);
        processActions(npc, targetNpc, interaction);
      }
    }
  }

  private void processPlaceholders(EventDto eventDto, List<List<Dialogue>> listOfLists) {
    for (var dialogues : listOfLists) {
      for (var dialogue : dialogues) {
        for (var interaction : dialogue.getInteractions()) {
          interaction.setText(processor.process(interaction.getText(), eventDto.eventDetails));
        }
      }
    }
  }

  private void processText(Npc npc, Npc targetNpc, Interaction interaction) {
    String processedText;
    if (targetNpc == null) {
      processedText = processor.process(interaction.getText(), npc);
    } else {
      processedText = processor.process(interaction.getText(), npc, targetNpc);
    }
    interaction.setText(processedText);
  }

  private void processActions(Npc npc, Npc targetNpc, Interaction interaction) {
    var actions = interaction.getActions();
    for (var action : actions) {
      String processedName;
      if (targetNpc == null) {
        processedName = processor.process(action.getName(), npc);
      } else {
        processedName = processor.process(action.getName(), npc, targetNpc);
      }
      action.setName(processedName);
    }
  }
}
