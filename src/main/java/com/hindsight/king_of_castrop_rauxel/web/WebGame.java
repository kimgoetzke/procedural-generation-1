package com.hindsight.king_of_castrop_rauxel.web;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.ActionHandler;
import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.encounter.web.EncounterSummaryDto;
import com.hindsight.king_of_castrop_rauxel.game.GameHandler;
import com.hindsight.king_of_castrop_rauxel.graph.Graph;
import com.hindsight.king_of_castrop_rauxel.location.Dungeon;
import com.hindsight.king_of_castrop_rauxel.web.dto.PlayerDto;
import com.hindsight.king_of_castrop_rauxel.web.dto.QuestDto;
import com.hindsight.king_of_castrop_rauxel.web.dto.WebResponse;
import com.hindsight.king_of_castrop_rauxel.web.exception.GenericWebException;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;
import com.hindsight.king_of_castrop_rauxel.world.World;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired), access = AccessLevel.PRIVATE)
public class WebGame {

  private final AppProperties appProperties;
  private final World world;
  private final Graph graph;
  private final PlayerRepository playerRepository;
  private final ActionHandler actionHandler;
  private final GameHandler gameHandler;
  @Getter private Player player;

  public WebResponse startGame(String userName) {
    world.setCurrentChunk(world.getCentreCoords());
    var startLocation = world.getCurrentChunk().getCentralLocation(graph);
    player = new Player(userName, startLocation, appProperties);
    var playerDto = PlayerDto.from(player);
    var actions = new ArrayList<Action>();
    getActions(player.getState(), actions);
    playerRepository.save(playerDto);
    return new WebResponse(actions, playerDto);
  }

  /** Resumes a game from a saved state. Can be used once a persistent database is implemented. */
  public WebResponse resumeGame(String userName) {
    var playerDto = playerRepository.findByName(userName);
    if (playerDto == null) {
      throw new GenericWebException("User not found", HttpStatus.NOT_FOUND);
    }
    var coords = getCoordinates(playerDto);
    world.setCurrentChunk(coords.getWorld());
    var currentLocation = world.getCurrentChunk().getLocation(coords);
    player = new Player(userName, currentLocation, appProperties);
    var actions = new ArrayList<Action>();
    getActions(player.getState(), actions);
    return new WebResponse(actions, playerDto);
  }

  public WebResponse playGame(int choice) {
    var actions = new ArrayList<Action>();
    takeAction(choice, actions);
    gameHandler.updateWorld(player);
    var interactions = getInteractions();
    getActions(player.getState(), actions);
    var encounterSummary = getEncounterSummary();
    var playerDto = PlayerDto.from(player);
    playerRepository.save(playerDto);
    return getWebResponse(encounterSummary, actions, playerDto, interactions);
  }

  public List<QuestDto> getQuests() {
    var allEvents = player.getEvents();
    return allEvents.stream().map(QuestDto::new).toList();
  }

  private static WebResponse getWebResponse(
      EncounterSummaryDto encounterSummary,
      ArrayList<Action> actions,
      PlayerDto playerDto,
      List<String> interactions) {
    if (encounterSummary != null) {
      return new WebResponse(actions, encounterSummary, playerDto);
    }
    if (!interactions.isEmpty()) {
      return new WebResponse(actions, interactions, playerDto);
    }
    return new WebResponse(actions, playerDto);
  }

  private List<String> getInteractions() {
    var isInDialogue = player.getState().equals(Player.State.IN_DIALOGUE);
    var hasCurrentEvent = player.hasCurrentEvent();
    if (!isInDialogue || !hasCurrentEvent) {
      return List.of();
    }
    var interactions = new ArrayList<String>();
    return getInteractions(interactions);
  }

  private List<String> getInteractions(List<String> interactions) {
    while (player.getCurrentEvent().hasCurrentInteraction()) {
      interactions.add(player.getCurrentEvent().getCurrentInteraction().getText());
      if (!player.getCurrentEvent().getCurrentActions().isEmpty()) {
        break;
      }
      player.getCurrentEvent().progressDialogue();
    }
    return interactions;
  }

  private EncounterSummaryDto getEncounterSummary() {
    if (player.getState().equals(Player.State.IN_COMBAT)) {
      var poi = player.getCurrentPoi();
      if (poi instanceof Dungeon dungeon) {
        return dungeon.getSequence().getPreviousEncounterSummary();
      }
    }
    return null;
  }

  private void takeAction(int choice, ArrayList<Action> actions) {
    getActions(player.getState(), actions);
    executeActionOrThrow(choice, actions);
  }

  private void executeActionOrThrow(int choice, ArrayList<Action> actions) {
    var action =
        actions.stream()
            .filter(a -> a.getIndex() == choice)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Action does not exist"));
    action.execute(player);
  }

  private void getActions(Player.State state, ArrayList<Action> actions) {
    switch (state) {
      case CHOOSING_POI -> actionHandler.getChoosePoiActions(player, actions);
      case AT_POI -> actionHandler.getThisPoiActions(player, actions);
      case IN_DIALOGUE -> actionHandler.getDialogueActions(player, actions);
      case IN_COMBAT -> actionHandler.getCombatActions(player, actions);
      case DEBUGGING -> actionHandler.getDebugActions(player, actions);
      default -> throw new GenericWebException("Unexpected state: " + state, HttpStatus.FORBIDDEN);
    }
  }

  private Coordinates getCoordinates(PlayerDto playerDto) {
    var worldSize = appProperties.getWorldProperties().size();
    var chunkSize = appProperties.getChunkProperties().size();
    var globalCoords = Pair.of(playerDto.getX(), playerDto.getY());
    return new Coordinates(globalCoords, Coordinates.CoordType.GLOBAL, worldSize, chunkSize);
  }
}
