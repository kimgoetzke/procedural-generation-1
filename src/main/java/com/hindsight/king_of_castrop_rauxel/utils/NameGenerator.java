package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity;
import com.hindsight.king_of_castrop_rauxel.location.DungeonHandler;
import com.hindsight.king_of_castrop_rauxel.location.Size;

public interface NameGenerator extends Generator {

  String locationNameFrom(Class<?> clazz);

  String locationNameFrom(
      AbstractAmenity amenity, Size parentSize, String parentName, Npc inhabitant, Class<?> clazz);

  String dungeonNameFrom(Class<?> clazz);

  String npcFirstNameFrom(Class<?> clazz);

  String npcLastNameFrom(Class<?> clazz);

  String npcNameFrom(boolean firstName, boolean lastName, Class<?> clazz);

  String enemyNameFrom(Class<?> clazz, DungeonHandler.Type type);
}
