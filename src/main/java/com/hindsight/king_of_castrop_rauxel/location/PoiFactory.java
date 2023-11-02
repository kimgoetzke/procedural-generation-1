package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.world.CoordinateFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PoiFactory {

  private final AppProperties appProperties;
  private final CoordinateFactory coordinateFactory;

  public PointOfInterest createPoiInstance(Location parent, Npc npc, PointOfInterest.Type type) {
    return switch (type) {
      case DUNGEON -> new Dungeon(appProperties, type, npc, parent);
      case SHOP -> new Shop(type, npc, parent);
      default -> new Amenity(type, npc, parent);
    };
  }
}
