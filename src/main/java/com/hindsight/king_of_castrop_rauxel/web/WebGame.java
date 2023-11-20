package com.hindsight.king_of_castrop_rauxel.web;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.world.World;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired), access = AccessLevel.PRIVATE)
public class WebGame {

  private final AppProperties appProperties;
  private final World world;
  private final Graph graph;
  private final PlayerRepository playerRepository;

  public PlayerDto getPlayer(String userName) {
    world.setCurrentChunk(world.getCentreCoords());
    var startLocation = world.getCurrentChunk().getCentralLocation(graph);
    var player = new Player(userName, startLocation, appProperties);
    var dto = PlayerDto.from(player);
    playerRepository.save(dto);
    return dto;
  }

  public List<Action> getInitialActions(String playerId) {
    log.info("Retrieving actions for: {}", playerId);
    var player = playerRepository.findById(playerId);
    log.info("Retrieved player: {}", player);
    return List.of();
  }

  public List<Action> getNextActions(String playerId, int choice) {
    log.info("Processing action '{}' for: {}", choice, playerId);
    // Should also return player as stats may have changed now...
    return List.of();
  }
}
