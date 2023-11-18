package com.hindsight.king_of_castrop_rauxel.game;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;
import com.hindsight.king_of_castrop_rauxel.world.World;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GameHandler {

  private final World world;

  public GameHandler(World world) {
    this.world = world;
  }

  public void updateWorld(Player player) {
    updateWorldCoords(player);
  }

  private void updateWorldCoords(Player player) {
    var worldCoords = world.getCurrentChunk().getCoordinates().getWorld();
    if (!player.getCoordinates().equalTo(worldCoords, Coordinates.CoordType.WORLD)) {
      log.info(String.format("Player is leaving: %s%n", world.getCurrentChunk().getSummary()));
      world.setCurrentChunk(player.getCoordinates().getWorld());
      log.info(String.format("Player is entering: %s%n", world.getCurrentChunk().getSummary()));
    }
  }

  public void updateCurrentEventDialogue(Player player) {
    if (!player.hasCurrentEvent()) {
      return;
    }
    var currentEvent = player.getCurrentEvent();
    currentEvent.progressDialogue();
    if (player.getState() != Player.State.IN_DIALOGUE) {
      currentEvent.resetDialogue();
      return;
    }
    if (!currentEvent.hasNextInteraction()) {
      player.setState(player.getPreviousState());
    }
  }
}
