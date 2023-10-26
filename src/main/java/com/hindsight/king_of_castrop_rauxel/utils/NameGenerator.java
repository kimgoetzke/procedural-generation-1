package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.location.*;

public interface NameGenerator extends Generator {

  String locationNameFrom(Class<?> clazz);

  String locationNameFrom(
      AbstractAmenity amenity, Size parentSize, String parentName, Npc inhabitant, Class<?> clazz);

  String npcFirstNameFrom(Class<?> clazz);

  String npcLastNameFrom(Class<?> clazz);

  String enemyNameFrom(Class<?> clazz, DungeonDetails.Type type);

  String dungeonNameFrom(Class<?> clazz, DungeonDetails.Type type);

  String dungeonDescriptionFrom(Class<?> clazz, DungeonDetails.Type type);
}
