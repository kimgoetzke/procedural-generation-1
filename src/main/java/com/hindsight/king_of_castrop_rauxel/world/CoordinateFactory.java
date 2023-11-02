package com.hindsight.king_of_castrop_rauxel.world;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CoordinateFactory {

  private final AppProperties appProperties;

  public Coordinates createCoordinates(
      Pair<Integer, Integer> worldCoords, Pair<Integer, Integer> chunkCoords) {
    var worldSize = appProperties.getWorldProperties().size();
    var chunkSize = appProperties.getChunkProperties().size();
    return new Coordinates(worldCoords, chunkCoords);
  }

  public Coordinates createCoordinates(Pair<Integer, Integer> chunkCoords, Chunk chunk) {
    var worldSize = appProperties.getWorldProperties().size();
    var chunkSize = appProperties.getChunkProperties().size();
    return new Coordinates(chunkCoords, chunk);
  }

  public Coordinates createCoordinates(Pair<Integer, Integer> coords, Coordinates.CoordType type) {
    var worldSize = appProperties.getWorldProperties().size();
    var chunkSize = appProperties.getChunkProperties().size();
    return new Coordinates(coords, type);
  }
}
