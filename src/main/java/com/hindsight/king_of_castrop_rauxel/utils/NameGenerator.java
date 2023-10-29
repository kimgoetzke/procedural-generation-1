package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.encounter.DungeonDetails;
import com.hindsight.king_of_castrop_rauxel.location.*;

public interface NameGenerator extends Generator {

  String locationNameFrom(Class<?> clazz);

  String locationNameFrom(
      Class<?> clazz, AbstractAmenity amenity, Size parentSize, String parentName, Npc inhabitant);

  String shopNameFrom(AbstractAmenity amenity, Shop.Type type, String parentName, Npc inhabitant);

  String npcFirstNameFrom(Class<?> clazz);

  String npcLastNameFrom(Class<?> clazz);

  String enemyNameFrom(Class<?> clazz, DungeonDetails.Type type);

  String dungeonNameFrom(Class<?> clazz, DungeonDetails.Type type);

  String dungeonDescriptionFrom(Class<?> clazz, DungeonDetails.Type type);
}
