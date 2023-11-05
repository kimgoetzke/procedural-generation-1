package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.event.*;
import com.hindsight.king_of_castrop_rauxel.location.Dungeon;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicEventGenerator implements EventGenerator {

  private static final String NPC_DISMISSIVE = "npc-dismissive";
  private static final String FALLBACK_ONE_LINER = "Hum?";
  private final FolderReader folderReader;
  private final TxtReader txtReader;
  private final YamlReader yamlReader = new YamlReader();
  private final PlaceholderProcessor processor = new PlaceholderProcessor();
  private Random random;

  @Getter @Setter private boolean isInitialised;

  public BasicEventGenerator(FolderReader folderReader) {
    this.folderReader = folderReader;
    this.txtReader = new TxtReader(folderReader.getSingleStepEventFolder());
  }

  public void initialise(Random parentRandom) {
    this.random = parentRandom;
    setInitialised(true);
  }

  public Event generate(Npc npc) {
    throwIfNotInitialised();
    var randomInt = random.nextInt(Event.Type.values().length);
    var eventCandidate =
        switch (randomInt) {
          case 0 -> singleStepDialogue(npc);
          case 1 -> multiStepDialogue(npc);
          case 2 -> deliveryEvent(npc);
          case 3 -> defeatEvent(npc);
          default -> {
            log.error("You did not implement all event types: {} is missing ", randomInt);
            yield null;
          }
        };
    return eventCandidate == null ? singleStepDialogue(npc) : eventCandidate;
  }

  /**
   * Returns an Event that only has a single interaction in the dialogue and no response from the
   * player is possible/permitted. The NPC will dismiss the player with a random one-liner. However,
   * sentiment can be customised in the future e.g. to integrate factions or the concept of loyalty.
   */
  private Event singleStepDialogue(Npc npc) {
    var text = readRandomLineFromFile(NPC_DISMISSIVE);
    var interactions = List.of(new Interaction(text, List.of()));
    var dialogues = List.of(new Dialogue(interactions));
    var participants = List.of(new Participant(npc, dialogues));
    process(dialogues, npc, null);
    return new DialogueEvent(new EventDetails(), participants, true);
  }

  private String readRandomLineFromFile(String fileName) {
    var result = txtReader.read(fileName);
    if (!result.isEmpty()) {
      return txtReader.getRandom(result, random).trim();
    }
    log.error("No file found for path name '%s'".formatted(fileName));
    return FALLBACK_ONE_LINER;
  }

  /**
   * Returns an Event that has multiple interactions in the dialogue, involves player actions that
   * affect the state and content of the dialogue (branching). However, it is limited to a single
   * NPC.
   */
  private Event multiStepDialogue(Npc npc) {
    var eventPath = folderReader.getRandomEventPath(Event.Type.DIALOGUE, random);
    var eventDto = yamlReader.read(eventPath);
    var dialogues = eventDto.participantData.get(Role.EVENT_GIVER);
    var participants = List.of(new Participant(npc, dialogues));
    var eventDetails = eventDto.eventDetails;
    initialiseRewards(eventDetails);
    processPlaceholders(eventDto, participants);
    return new DialogueEvent(eventDetails, participants, true);
  }

  /**
   * Returns an event that involves one NPC, allows branching dialogues with actions for the player,
   * and must involve defeating 1) a specified number of a specified enemy type (anywhere in the
   * world) or 2) all enemies at a specified POI. This event type must involve a single reward for
   * the player at some point in the event.
   */
  private Event defeatEvent(Npc npc) {
    var eventPath = folderReader.getRandomEventPath(Event.Type.DEFEAT, random);
    var eventDto = yamlReader.read(eventPath);
    var dialogues = eventDto.participantData.get(Role.EVENT_GIVER);
    var participants = List.of(new Participant(npc, dialogues));
    var eventDetails = eventDto.eventDetails;
    var defeatDetails = eventDto.defeatDetails;
    setEventGiver(eventDto.eventDetails, npc);
    initialiseRewards(eventDetails);
    return defeatEventOrNull(npc, defeatDetails, eventDto, participants, eventDetails);
  }

  private DefeatEvent defeatEventOrNull(
      Npc npc,
      DefeatEventDetails defeatDetails,
      EventDto eventDto,
      List<Participant> participants,
      EventDetails eventDetails) {
    if (defeatDetails.getTaskType() != DefeatEvent.TaskType.KILL_ALL_AT_POI) {
      var dungeon = tryToFindDungeon(npc);
      if (dungeon != null) {
        setDungeon(defeatDetails, dungeon);
        processPlaceholders(eventDto, participants);
        return new DefeatEvent(eventDetails, defeatDetails, participants);
      }
    }
    if (defeatDetails.getEnemyType() != null || defeatDetails.getToDefeat() > 0) {
      processPlaceholders(eventDto, participants);
      return new DefeatEvent(eventDetails, defeatDetails, participants);
    }
    log.debug("Cannot generate DefeatEvent - no dungeon available for {}", npc);
    return null;
  }

  private Dungeon tryToFindDungeon(Npc npc) {
    var pois = npc.getHome().getParent().getPointsOfInterest();
    var dungeons = pois.stream().filter(Dungeon.class::isInstance).toList();
    return dungeons.isEmpty() ? null : (Dungeon) dungeons.get(random.nextInt(dungeons.size()));
  }

  /**
   * Returns an Event that involves two NPCs and branching dialogues with actions for each NPC. This
   * type of event must involve a single reward for the player at some point in the event.
   */
  private Event deliveryEvent(Npc npc) {
    var eventPath = folderReader.getRandomEventPath(Event.Type.REACH, random);
    var eventDto = yamlReader.read(eventPath);
    var giverNpcDialogues = eventDto.participantData.get(Role.EVENT_GIVER);
    var giverParticipant = new Participant(npc, giverNpcDialogues);
    var targetNpcDialogues = eventDto.participantData.get(Role.EVENT_TARGET);
    return reachEventOrNull(npc, targetNpcDialogues, giverParticipant, eventDto);
  }

  private ReachEvent reachEventOrNull(
      Npc npc, List<Dialogue> targetNpcDialogues, Participant giverParticipant, EventDto eventDto) {
    var targetPoi = tryToFindPoi(npc);
    if (targetPoi != null) {
      var targetNpc = targetPoi.getNpc();
      var targetParticipant = new Participant(targetNpc, Role.EVENT_TARGET, targetNpcDialogues);
      var participants = List.of(giverParticipant, targetParticipant);
      setEventGiver(eventDto.eventDetails, npc);
      initialiseRewards(eventDto.eventDetails);
      processPlaceholders(eventDto, participants);
      return new ReachEvent(eventDto.eventDetails, participants);
    }
    log.warn("Cannot generate DeliveryEvent - no POI available for {}", npc);
    return null;
  }

  private PointOfInterest tryToFindPoi(Npc npc) {
    var availablePois = npc.getHome().getParent().getPointsOfInterest();
    for (int i = 0; i < availablePois.size(); i++) {
      var poi = findPoiInSameLocation(npc, availablePois);
      if (poi != null) {
        return poi;
      }
    }
    return null;
  }

  private PointOfInterest findPoiInSameLocation(Npc npc, List<PointOfInterest> pois) {
    var randomNumber = random.nextInt(0, pois.size());
    var poi = pois.get(randomNumber);
    var hasNoNpc = poi.getNpc() == null;
    var isSamePoi = npc.getHome().equals(poi);
    var isNotEligible = poi.getType() == PointOfInterest.Type.DUNGEON;
    if (hasNoNpc || isSamePoi || isNotEligible) {
      return null;
    }
    return poi;
  }

  private void setEventGiver(EventDetails eventDetails, Npc npc) {
    eventDetails.setEventGiver(npc);
  }

  private void setDungeon(DefeatEventDetails defeatDetails, Dungeon dungeon) {
    defeatDetails.setPoi(dungeon);
  }

  private void initialiseRewards(EventDetails eventDetails) {
    for (var reward : eventDetails.getRewards()) {
      reward.load(random);
      log.info("Initialised reward of {} for {}", reward, eventDetails.getId());
    }
  }

  private void processPlaceholders(EventDto eventDto, List<Participant> participants) {
    var giverNpc = getNpc(participants, Role.EVENT_GIVER);
    var targetNpc = getNpc(participants, Role.EVENT_TARGET);
    var gDialogue = eventDto.participantData.get(Role.EVENT_GIVER);
    var tDialogue = eventDto.participantData.get(Role.EVENT_TARGET);
    process(eventDto, giverNpc, targetNpc);
    process(gDialogue, eventDto.defeatDetails);
    process(gDialogue, giverNpc, targetNpc);
    process(tDialogue, giverNpc, targetNpc);
    var dialoguesList = tDialogue == null ? List.of(gDialogue) : List.of(gDialogue, tDialogue);
    process(dialoguesList, eventDto);
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
    var tAboutProcessed = process(eventDetails.getAboutTarget(), npc, targetNpc);
    eventDetails.setAboutTarget(tAboutProcessed);
    var gAboutProcessed = process(eventDetails.getAboutGiver(), npc, targetNpc);
    eventDetails.setAboutGiver(gAboutProcessed);
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

  private void process(List<Dialogue> toProcess, DefeatEventDetails defeatDetails) {
    if (toProcess == null || defeatDetails == null) {
      return;
    }
    for (var dialogue : toProcess) {
      for (var interaction : dialogue.getInteractions()) {
        interaction.setText(processor.process(interaction.getText(), defeatDetails));
      }
    }
  }

  private void process(Interaction toProcess, Npc npc, Npc targetNpc) {
    var actions = toProcess.getActions();
    actions.forEach(action -> action.setName(process(action.getName(), npc, targetNpc)));
  }

  private String process(String toProcess, Npc npc, Npc targetNpc) {
    if (toProcess.isEmpty()) {
      return toProcess;
    }
    if (targetNpc == null) {
      return processor.process(toProcess, npc);
    } else {
      return processor.process(toProcess, npc, targetNpc);
    }
  }
}
