package com.hindsight.king_of_castrop_rauxel.web;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.ActionHandler;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
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

  public PlayerDto getPlayer(String userName) {
    world.setCurrentChunk(world.getCentreCoords());
    var startLocation = world.getCurrentChunk().getCentralLocation(graph);
    player = new Player(userName, startLocation, appProperties);
    var dto = PlayerDto.from(player);
    playerRepository.save(dto);
    return dto;
  }

  public ActionResponsesDto getInitialActions() {
    log.info("Retrieving actions for player: {}, {}", player.getName(), player.getId());
    var actions = new ArrayList<Action>();
    actionHandler.getThisPoiActions(player, actions);
    return ActionResponsesDto.from(actions);
  }

  public ActionResponsesDto processAction(int choice) {
    log.info("Processing choice '{}' for player: {}, {}", choice, player.getName(), player.getId());
    var actions = new ArrayList<Action>();
    getActions(player.getState(), actions);
    actions.get(choice).execute(player);
    getActions(player.getState(), actions);
    // Should also return player as stats may have changed now...
    return ActionResponsesDto.from(actions);
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
