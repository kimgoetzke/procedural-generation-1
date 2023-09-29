package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

import static com.hindsight.king_of_castrop_rauxel.location.AbstractLocation.Size;

public interface NameGenerator {

  void setRandom(Random random);

  String locationNameFrom(Class<?> clazz);

  String locationNameFrom(
      AbstractAmenity amenity, Size parentSize, String parentName, Npc inhabitant, Class<?> clazz);

  String npcFirstNameFrom(Class<?> clazz);

  String npcLastNameFrom(Class<?> clazz);

  String npcNameFrom(boolean firstName, boolean lastName, Class<?> clazz);

  static String generatePlaceholder(String className) {
    int length = 5;
    boolean useLetters = true;
    boolean useNumbers = false;
    return "%s %s".formatted(className, RandomStringUtils.random(length, useLetters, useNumbers));
  }
}
