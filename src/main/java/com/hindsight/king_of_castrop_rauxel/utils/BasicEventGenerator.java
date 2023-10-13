package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.event.*;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicEventGenerator implements EventGenerator {

  public static final String LINE_SEPARATOR = System.getProperty("file.separator");
  private static final String BASE_FOLDER = "events" + LINE_SEPARATOR;
  private static final String MULTI_STEP_FOLDER = "multi-step" + LINE_SEPARATOR;
  private static final String REACH_FOLDER = "reach" + LINE_SEPARATOR;
  private static final String SINGLE_STEP_FOLDER = "single-step" + LINE_SEPARATOR;
  private static final int MAX_ATTEMPTS = 3;
  public static final String FALLBACK_ONE_LINER = "Hum?";
  private final TxtReader txtReader = new TxtReader(BASE_FOLDER);
  private final YamlReader yamlReader = new YamlReader(BASE_FOLDER);
  private final PlaceholderProcessor processor = new PlaceholderProcessor();
  private Random random;
  private Map<Event.Type, List<Path>> eventFilePaths;

  public void setRandom(Random parentRandom) {
    random = parentRandom;
    processor.setRandom(parentRandom);
    loadEventFilesMap();
    System.out.println("Random DIALOGUE: " + getRandomEventPath(Event.Type.DIALOGUE));
    System.out.println("Random REACH: " + getRandomEventPath(Event.Type.REACH));
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
    var pathName = SINGLE_STEP_FOLDER + "NPC-IDLE";
    var text = readRandomLineFromFile(pathName);
    var interactions = List.of(new Interaction(text, List.of(), null));
    var dialogues = List.of(new Dialogue(interactions));
    var participants = List.of(new Participant(npc, dialogues));
    process(dialogues, npc, null);
    return new DialogueEvent(new EventDetails(), participants, true);
  }

  private String readRandomLineFromFile(String pathName) {
    var result = txtReader.read(pathName);
    if (!result.isEmpty()) {
      return txtReader.getRandom(result, random).trim();
    }
    log.error("No file found for path name '%s%s'".formatted(BASE_FOLDER, pathName));
    return FALLBACK_ONE_LINER;
  }

  @Override
  public Event multiStepDialogue(Npc npc) {
    var eventDto = yamlReader.read(MULTI_STEP_FOLDER + "the-obvious-question");
    var dialogues = eventDto.participantData.get(Role.EVENT_GIVER);
    var participants = List.of(new Participant(npc, dialogues));
    var eventDetails = eventDto.eventDetails;
    initialiseRewards(eventDetails);
    processPlaceholders(eventDto, participants);
    return new DialogueEvent(eventDetails, participants, true);
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

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  private void loadEventFilesMap() {
    eventFilePaths = new EnumMap<>(Event.Type.class);
    for (var t : Event.Type.values()) {
      var subFolder =
          switch (t) {
            case REACH -> REACH_FOLDER;
            default -> MULTI_STEP_FOLDER;
          };
      var path = BASE_FOLDER + subFolder;
      try {
        var paths = getAllFilesFrom(path);
        eventFilePaths.put(t, paths);
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    }
    for (var e : eventFilePaths.entrySet()) {
      System.out.println("Key: " + e.getKey());
      System.out.println("Values: ");
      e.getValue().forEach(x -> System.out.println(" - " + x));
    }
  }

  private List<Path> getAllFilesFrom(String folder) throws URISyntaxException {
    var resource = getClass().getClassLoader().getResource(folder);
    if (resource != null) {
      var startUri = Paths.get(resource.toURI());
      try (var stream = Files.walk(startUri)) {
        return stream.filter(Files::isRegularFile).toList();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    throw new IllegalArgumentException("Folder '%s' not found".formatted(folder));
  }

  private String getRandomEventPath(Event.Type type) {
    var paths = eventFilePaths.get(type);
    var randomIndex = random.nextInt(0, paths.size());
    return paths.get(randomIndex).toString();
  }
}
