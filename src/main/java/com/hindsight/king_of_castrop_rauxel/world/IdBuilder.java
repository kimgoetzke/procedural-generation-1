package com.hindsight.king_of_castrop_rauxel.world;

import java.util.*;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdBuilder {

  private static final Map<Class<?>, String> abbreviations = new HashMap<>();
  private static final String SEPARATOR = "~";

  public static String idFrom(Class<?> clazz, Coordinates coordinates) {
    var threeLetters = getAbbreviationFor(clazz);
    return threeLetters + SEPARATOR + coordinates.gX() + coordinates.gY();
  }

  public static String idFrom(Class<?> clazz) {
    var threeLetters = getAbbreviationFor(clazz);
    var uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    return threeLetters + SEPARATOR + uuid.toUpperCase();
  }

  public static String idFrom(Class<?> clazz, String parentId) {
    var threeLetters = getAbbreviationFor(clazz);
    var parentSubString = parentId.substring(parentId.indexOf(SEPARATOR) + 1);
    return threeLetters + SEPARATOR + parentSubString;
  }

  private static String getAbbreviationFor(Class<?> clazz) {
    if (abbreviations.containsKey(clazz)) {
      return abbreviations.get(clazz);
    }
    return generateAbbreviationFor(clazz);
  }

  private static String generateAbbreviationFor(Class<?> clazz) {
    var threeLetters = "";
    var isUnique = false;
    var offset = 0;
    while (!isUnique) {
      threeLetters = getThreeLetters(clazz, offset);
      if (!abbreviations.containsValue(threeLetters)) {
        abbreviations.put(clazz, threeLetters);
        isUnique = true;
      }
      offset++;
    }
    return threeLetters;
  }

  private static String getThreeLetters(Class<?> clazz, int offset) {
    if (clazz.getSimpleName().length() < offset) {
      throw new IllegalStateException(
          "Could not find unique abbreviation for class %s, failed at offset %s"
              .formatted(clazz.getSimpleName(), offset));
    }
    return clazz.getSimpleName().substring(offset, offset + 3).toUpperCase();
  }
}
