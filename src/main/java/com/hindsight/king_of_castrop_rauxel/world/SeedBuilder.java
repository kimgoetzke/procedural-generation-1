package com.hindsight.king_of_castrop_rauxel.world;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SeedBuilder {

  /*
   * Effectively returns a long concatenating the x and y coordinates.
   * Example:
   * - Assume x = 1 and y = 2: x in binary is 0001 and y is 0010.
   * - The number of digits in y is 1, so yDigits * 3 = 3.
   * - Shifting x to the left by 3 positions gives 1000.
   * - Performing a bitwise OR with y gives 1010, which is the binary representation of 12.
   */
  public static long seedFrom(Pair<Integer, Integer> coordinates) {
    var x = (int) coordinates.getFirst();
    var y = (int) coordinates.getSecond();
    var yDigits = (int) Math.log10(y) + 1;
    return ((long) x << (yDigits * 3)) | y;
  }
}
