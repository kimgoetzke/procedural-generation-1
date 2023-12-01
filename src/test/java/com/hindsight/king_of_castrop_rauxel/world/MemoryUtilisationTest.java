package com.hindsight.king_of_castrop_rauxel.world;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MemoryUtilisationTest extends BaseWorldTest {

  /**
   * This test should remain disabled by default. It may take up to 5 minutes to run as it generates
   * 400 chunks. It's recommended to also turn the log level to OFF to avoid the console being
   * flooded and to see the progress/memory stats during the testing.
   */
  @Test
  @Disabled("This test is only for manual testing as it a very long time to run.")
  void whenVisitingHundredsOfChunks_memoryUtilisationRemainsTheSame() {
    var max = 20; // Full load with appProperties.getWorldProperties().size()
    var rt = Runtime.getRuntime();
    var maxTotal = 0L;
    var maxFree = 0L;
    var maxUsed = 0L;
    var maxMemoryValues = new long[3];

    for (var i = 0; i < max; i++) {
      for (var j = 0; j < max; j++) {
        var coords = Pair.of(i, j);
        world.setCurrentChunk(coords);
        var total = rt.totalMemory();
        var free = rt.freeMemory();
        var used = total - free;
        maxMemoryValues = updateMaxUtilisation(maxTotal, maxFree, maxUsed, total, free, used);
        maxTotal = maxMemoryValues[0];
        maxFree = maxMemoryValues[1];
        maxUsed = maxMemoryValues[2];
        System.out.println(memoryToString(coords, total, free, used));
      }
    }

    System.out.println("Max memory usage during run:");
    System.out.println(memoryToString(Pair.of(0, 0), maxTotal, maxFree, maxUsed));
    var totalMemory = rt.totalMemory() / (1024.0 * 1024);
    assertThat(totalMemory).isLessThan(400);
  }

  private long[] updateMaxUtilisation(
      long maxTotal, long maxFree, long maxUsed, long total, long free, long used) {
    if (total > maxTotal) {
      maxTotal = total;
    }
    if (free > maxFree) {
      maxFree = free;
    }
    if (used > maxUsed) {
      maxUsed = used;
    }
    return new long[] {maxTotal, maxFree, maxUsed};
  }

  private String memoryToString(
      Pair<Integer, Integer> coords, long totalMemory, long freeMemory, long usedMemory) {
    return String.format(
        "w(%s,%s) - Total: %.2f MB, free: %.2f MB, used: %.2f MB",
        coords.getFirst(),
        coords.getSecond(),
        totalMemory / (1024.0 * 1024),
        freeMemory / (1024.0 * 1024),
        usedMemory / (1024.0 * 1024));
  }
}
