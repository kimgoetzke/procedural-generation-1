package com.hindsight.king_of_castrop_rauxel.utils;

import static com.hindsight.king_of_castrop_rauxel.utils.FileReader.*;
import static com.hindsight.king_of_castrop_rauxel.utils.FileReader.BASE_FOLDER;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.event.*;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicEventGenerator implements EventGenerator {

  private static final int MAX_ATTEMPTS = 3;
  private static final String FALLBACK_ONE_LINER = "Hum?";
  private final PlaceholderProcessor processor = new PlaceholderProcessor();
  private final YamlProcessor yamlProcessor = new YamlProcessor();
  private final FileReader fileReader;
  private TxtProcessor txtProcessor;
  private Random random;

  public BasicEventGenerator(FileReader fileReader) {
    this.fileReader = fileReader;
  }

  public void setRandom(Random parentRandom) {
    random = parentRandom;
    txtProcessor = new TxtProcessor(BASE_FOLDER + fileReader.getFileSeparator());
    processor.setRandom(parentRandom);
  }

  // TODO: Get a random file from the folder instead of hardcoding the file name
  //  - Read number of files in relevant folder
  //  - Select a random one to be read
  //  - Move file reading into separate @Component fileReader
  //  - Rename YamlReader and TxtReader to _Processor and accept files/streams instead

  // TODO: Think through how one-line responses should be handled
  //  - Should they be events?
  //  - How to categorise them? Dismissive? Friendly? Neutral? Desperate?
  @Override
  public Event singleStepDialogue(Npc npc) {
    var pathName = SINGLE_STEP_FOLDER + fileReader.getFileSeparator() + "NPC-IDLE";
    var text = readRandomLineFromFile(pathName);
    var interactions = List.of(new Interaction(text, List.of(), null));
    var dialogues = List.of(new Dialogue(interactions));
    var participants = List.of(new Participant(npc, dialogues));
    process(dialogues, npc, null);
    return new DialogueEvent(new EventDetails(), participants, true);
  }

  private String readRandomLineFromFile(String pathName) {
    var result = txtProcessor.read(pathName);
    if (!result.isEmpty()) {
      return txtProcessor.getRandom(result, random).trim();
    }
    log.error(
        "No file found for path name '%s%s'"
            .formatted(BASE_FOLDER + fileReader.getFileSeparator(), pathName));
    return FALLBACK_ONE_LINER;
  }

  @Override
  public Event multiStepDialogue(Npc npc) {
    var eventPath = fileReader.getRandomEventPath(Event.Type.DIALOGUE, random);
    var eventDto = yamlProcessor.read(eventPath);
    var dialogues = eventDto.participantData.get(Role.EVENT_GIVER);
    var participants = List.of(new Participant(npc, dialogues));
    var eventDetails = eventDto.eventDetails;
    initialiseRewards(eventDetails);
    processPlaceholders(eventDto, participants);
    return new DialogueEvent(eventDetails, participants, true);
  }

  @Override
  public Event deliveryEvent(Npc npc) {
    var eventPath = fileReader.getRandomEventPath(Event.Type.REACH, random);
    var eventDto = yamlProcessor.read(eventPath);
    var giverNpcDialogues = eventDto.participantData.get(Role.EVENT_GIVER);
    var giverParticipant = new Participant(npc, giverNpcDialogues);
    var targetNpcDialogues = eventDto.participantData.get(Role.EVENT_TARGET);
    var targetPoi = tryToFindAPoi(npc);
    if (targetPoi != null) {
      var targetNpc = targetPoi.getNpc();
      var targetParticipant = new Participant(targetNpc, Role.EVENT_TARGET, targetNpcDialogues);
      var participants = List.of(giverParticipant, targetParticipant);
      initialiseRewards(eventDto.eventDetails);
      processPlaceholders(eventDto, participants);
      return new ReachEvent(eventDto.eventDetails, participants);
    }
    return null;
  }

  private void initialiseRewards(EventDetails eventDetails) {
    for (var reward : eventDetails.getRewards()) {
      reward.load(random);
      log.info("Initialised reward: {}", reward);
    }
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

  private void processPlaceholders(EventDto eventDto, List<Participant> participants) {
    var giverNpcDialogues = eventDto.participantData.get(Role.EVENT_GIVER);
    var targetNpcDialogues = eventDto.participantData.get(Role.EVENT_TARGET);
    var giverNpc = getNpc(participants, Role.EVENT_GIVER);
    var targetNpc = getNpc(participants, Role.EVENT_TARGET);
    process(eventDto, giverNpc, targetNpc);
    process(giverNpcDialogues, giverNpc, targetNpc);
    process(targetNpcDialogues, giverNpc, targetNpc);
    var listOfDialogues =
        targetNpcDialogues == null
            ? List.of(giverNpcDialogues)
            : List.of(giverNpcDialogues, targetNpcDialogues);
    process(listOfDialogues, eventDto);
  }

  private Npc getNpc(List<Participant> participants, Role role) {
    return participants.stream()
        .filter(p -> p.role() == role)
        .map(Participant::npc)
        .findFirst()
        .orElse(null);
  }

  private void process(EventDto toProcess, Npc npc, Npc targetNpc) {
    var eventDetails = toProcess.eventDetails;
    if (eventDetails.getAbout() == null) {
      return;
    }
    var about = processor.process(eventDetails.getAbout(), npc, targetNpc);
    eventDetails.setAbout(about);
  }

  private void process(List<Dialogue> toProcess, Npc npc, Npc targetNpc) {
    if (toProcess == null) {
      return;
    }
    for (var dialogue : toProcess) {
      for (var interaction : dialogue.getInteractions()) {
        interaction.setText(process(interaction.getText(), npc, targetNpc));
        process(interaction, npc, targetNpc);
      }
    }
  }

  private void process(List<List<Dialogue>> toProcess, EventDto eventDto) {
    for (var dialogues : toProcess) {
      for (var dialogue : dialogues) {
        for (var interaction : dialogue.getInteractions()) {
          interaction.setText(processor.process(interaction.getText(), eventDto.eventDetails));
        }
      }
    }
  }

  private void process(Interaction toProcess, Npc npc, Npc targetNpc) {
    var actions = toProcess.getActions();
    actions.forEach(action -> action.setName(process(action.getName(), npc, targetNpc)));
  }

  private String process(String toProcess, Npc npc, Npc targetNpc) {
    if (targetNpc == null) {
      return processor.process(toProcess, npc);
    } else {
      return processor.process(toProcess, npc, targetNpc);
    }
  }
}
