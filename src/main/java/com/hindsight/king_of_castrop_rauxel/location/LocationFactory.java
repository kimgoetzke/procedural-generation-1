package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.utils.DataServices;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LocationFactory {

  private final AppProperties appProperties;
  private final Generators generators;
  private final DataServices dataServices;
  private final PoiFactory poiFactory;

  public AbstractLocation createSettlement(
      Pair<Integer, Integer> worldCoords, Pair<Integer, Integer> chunkCoords) {
    return new Settlement(
        worldCoords, chunkCoords, generators, dataServices, appProperties, poiFactory);
  }
}
