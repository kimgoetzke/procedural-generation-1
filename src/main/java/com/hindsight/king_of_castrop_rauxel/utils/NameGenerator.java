package com.hindsight.king_of_castrop_rauxel.utils;

import static com.hindsight.king_of_castrop_rauxel.location.AbstractLocation.Size;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.cli.combat.DungeonDetails;
import com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity;
import java.util.Random;

public interface NameGenerator {

  void initialise(Random random);

  String locationNameFrom(Class<?> clazz);

  String locationNameFrom(
      AbstractAmenity amenity, Size parentSize, String parentName, Npc inhabitant, Class<?> clazz);

  String dungeonNameFrom(Class<?> clazz);

  String npcFirstNameFrom(Class<?> clazz);

  String npcLastNameFrom(Class<?> clazz);

  String npcNameFrom(boolean firstName, boolean lastName, Class<?> clazz);

  String enemyNameFrom(Class<?> clazz, DungeonDetails.Type type);
}
