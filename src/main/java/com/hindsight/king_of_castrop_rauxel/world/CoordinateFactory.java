package com.hindsight.king_of_castrop_rauxel.world;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import org.springframework.data.util.Pair;

public class CoordinateFactory {

  private final int worldSize;
  private final int chunkSize;

  public CoordinateFactory(AppProperties appProperties) {
    this.worldSize = appProperties.getWorldProperties().size();
    this.chunkSize = appProperties.getChunkProperties().size();
  }

  public Coordinates create(
      Pair<Integer, Integer> worldCoords, Pair<Integer, Integer> chunkCoords) {
    return new Coordinates(worldCoords, chunkCoords, worldSize, chunkSize);
  }

  public Coordinates create(Pair<Integer, Integer> chunkCoords, Chunk chunk) {
    return new Coordinates(chunkCoords, chunk, worldSize, chunkSize);
  }

  public Coordinates create(Pair<Integer, Integer> coords, Coordinates.CoordType type) {
    return new Coordinates(coords, type, worldSize, chunkSize);
  }
}
