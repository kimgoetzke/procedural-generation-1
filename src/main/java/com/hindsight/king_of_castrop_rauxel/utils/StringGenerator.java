package com.hindsight.king_of_castrop_rauxel.utils;

import static com.hindsight.king_of_castrop_rauxel.location.AbstractLocation.*;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;

public interface StringGenerator {
  void setRandom(Random random);

  String locationNameFrom(Class<?> clazz);

  String locationNameFrom(AbstractAmenity amenity, Class<?> clazz);

  String locationNameFrom(String parentName, Class<?> clazz);

  String locationNameFrom(
    AbstractAmenity amenity, Size parentSize, String parentName, List<Npc> inhabitants, Class<?> clazz);

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
