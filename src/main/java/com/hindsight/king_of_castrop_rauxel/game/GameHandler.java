package com.hindsight.king_of_castrop_rauxel.game;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;
import com.hindsight.king_of_castrop_rauxel.world.World;
import com.hindsight.king_of_castrop_rauxel.world.ChunkHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GameHandler {

  private final World world;
  private final Graph<AbstractLocation> map;
  private final ChunkHandler chunkHandler;

  public GameHandler(World world, Graph<AbstractLocation> map, ChunkHandler chunkHandler) {
    this.world = world;
    this.map = map;
    this.chunkHandler = chunkHandler;
  }

  public void updateWorld(Player player) {
    updateWorldCoords(player);
    generateNextChunk(player);
  }

  private void updateWorldCoords(Player player) {
    var worldCoords = world.getCurrentChunk().getCoordinates().getWorld();
    if (!player.getCoordinates().equalTo(worldCoords, Coordinates.CoordType.WORLD)) {
      log.info(String.format("Player is leaving: %s%n", world.getCurrentChunk().getSummary()));
      world.setCurrentChunk(player.getCoordinates().getWorld());
      log.info(String.format("Player is entering: %s%n", world.getCurrentChunk().getSummary()));
    }
  }

  private void generateNextChunk(Player player) {
    var chunkCoords = player.getCurrentLocation().getCoordinates().getChunk();
    if (chunkHandler.isInsideTriggerZone(chunkCoords)) {
      var whereNext = chunkHandler.nextChunkPosition(chunkCoords);
      log.info("Player is inside {}ern trigger zone", whereNext.getName().toLowerCase());
      if (world.hasChunk(whereNext)) {
        log.info("{} chunk already exists - skipping generation", whereNext.getName());
        return;
      }
      world.generateChunk(whereNext, map);
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
