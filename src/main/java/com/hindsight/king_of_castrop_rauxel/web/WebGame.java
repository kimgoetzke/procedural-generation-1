package com.hindsight.king_of_castrop_rauxel.web;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.ActionHandler;
import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.encounter.web.EncounterSummaryDto;
import com.hindsight.king_of_castrop_rauxel.graph.Graph;
import com.hindsight.king_of_castrop_rauxel.location.Dungeon;
import com.hindsight.king_of_castrop_rauxel.web.dto.ActionResponsesDto;
import com.hindsight.king_of_castrop_rauxel.web.dto.PlayerDto;
import com.hindsight.king_of_castrop_rauxel.web.dto.WebResponseDto;
import com.hindsight.king_of_castrop_rauxel.world.World;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
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
  @Getter private Player player;

  public WebResponseDto getPlayer(String userName) {
    world.setCurrentChunk(world.getCentreCoords());
    var startLocation = world.getCurrentChunk().getCentralLocation(graph);
    player = new Player(userName, startLocation, appProperties);
    var dto = PlayerDto.from(player);
    playerRepository.save(dto);
    return new WebResponseDto(dto);
  }

  public WebResponseDto getInitialActions() {
    var actions = new ArrayList<Action>();
    actionHandler.getThisPoiActions(player, actions);
    var dto = ActionResponsesDto.from(actions);
    return new WebResponseDto(dto);
  }

  public WebResponseDto processAction(int choice) {
    var actions = new ArrayList<Action>();
    takeAction(choice, actions);
    getActions(player.getState(), actions);
    var encounterSummary = getEncounterSummary();
    var actionResponses = ActionResponsesDto.from(actions);
    var playerDto = PlayerDto.from(player);
    if (encounterSummary != null) {
      return new WebResponseDto(actionResponses, encounterSummary, playerDto);
    }
    return new WebResponseDto(actionResponses, playerDto);
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
      case IN_MENU -> actionHandler.getMenuActions(player, actions);
      case DEBUGGING -> actionHandler.getDebugActions(player, actions);
    }
  }
}
