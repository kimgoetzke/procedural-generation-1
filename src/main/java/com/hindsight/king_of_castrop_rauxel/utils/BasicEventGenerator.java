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
    processPlaceholders(npc, dialogues, null);
    return new DialogueEvent(dialogues, npc, true);
  }

  @Override
  public Event multiStepDialogue(Npc npc) {
    var response = yamlReader.read(MULTI_STEP_FOLDER + "a-close-friends-parcel");
    var dialogues = response.get(0);
    processPlaceholders(npc, dialogues, null);
    return new DialogueEvent(dialogues, npc, true);
  }

  @Override
  public Event deliveryEvent(Npc npc) {
    var response = yamlReader.read(REACH_FOLDER + "a-close-friends-parcel");
    var npcDialogues = response.get(0);
    var targetNpcDialogues = response.get(1);
    var targetPoi = findAnotherPoiInSameLocation(npc, 1);
    if (targetPoi != null) {
      var targetNpc = targetPoi.getNpc();
      processPlaceholders(npc, npcDialogues, targetNpc);
      processPlaceholders(npc, targetNpcDialogues, targetNpc);
      return new ReachEvent(npc, npcDialogues, List.of(targetNpc), targetNpcDialogues, targetPoi);
    }
    return null;
  }

  private PointOfInterest findAnotherPoiInSameLocation(Npc npc, int attempt) {
    var pointsOfInterest = npc.getHome().getParent().getPointsOfInterest();
    var randomNumber = random.nextInt(0, pointsOfInterest.size());
    var poi = pointsOfInterest.get(randomNumber);
    if (poi.getNpc() == null || poi.getNpc().equals(npc)) {
      log.debug("Found no (valid) NPC in target POI candidate: {}", poi.getSummary());
      if (attempt >= 3) {
        log.warn(
            "Failed to find a (valid) NPC for target POI - no delivery quest can be generated for {}",
            npc.getHome());
        return null;
      }
      findAnotherPoiInSameLocation(npc, attempt + 1);
    }
    return poi;
  }

  private void processPlaceholders(Npc npc, List<Dialogue> dialogues, Npc targetNpc) {
    for (var dialogue : dialogues) {
      for (var interaction : dialogue.getInteractions()) {
        processText(npc, targetNpc, interaction);
        processActions(npc, targetNpc, interaction);
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

  private String readRandomLineFromFile(String pathName) {
    var result = txtReader.read(pathName);
    if (!result.isEmpty()) {
      return txtReader.getRandom(result, random).trim();
    }
    throw new IllegalArgumentException("No file found for path name '%s'".formatted(pathName));
  }
}
