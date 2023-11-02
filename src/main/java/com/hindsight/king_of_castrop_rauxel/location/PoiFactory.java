package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.encounter.EncounterHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PoiFactory {

  private final AppProperties appProperties;
  private final EncounterHandler encounterHandler;

  public PointOfInterest createInstance(Location parent, Npc npc, PointOfInterest.Type type) {
    return switch (type) {
      case DUNGEON -> createDungeon(type, npc, parent);
      case SHOP -> new Shop(type, npc, parent);
      default -> new Amenity(type, npc, parent);
    };
  }

  public Dungeon createDungeon(PointOfInterest.Type type, Npc npc, Location parent) {
    return new Dungeon(type, npc, parent, appProperties, encounterHandler);
  }
}
