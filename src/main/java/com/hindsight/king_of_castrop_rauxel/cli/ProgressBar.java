package com.hindsight.king_of_castrop_rauxel.cli;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.lang.System.out;

@Slf4j
@Component
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ProgressBar {

  /**
   * The speed modifier for the progress bar. The higher the value, the slower the progress.
   * Examples: A modifier of 1 means that it takes 10 seconds to travel a distance of 100 km. A
   * modifier of 0.5 means that it takes 5 seconds to travel a distance of 100 km.
   */
  public static final float SPEED_MODIFIER = 0.1F;

  // TODO: Allow interrupting (returning progress %) and resuming later (accepting progress %)
  public static void displayProgress(Location from, Location to) {
    if (from.equals(to)) {
      return;
    }
    var progressBarWidth = 100;
    var totalSteps = 100;
    var millisecondsPerStep = from.distanceTo(to) * SPEED_MODIFIER;
    prepareCli();
    for (int step = 0; step <= totalSteps; step++) {
      var progress = (float) step / totalSteps;
      var filledWidth = (int) (progress * progressBarWidth);
      var emptyWidth = progressBarWidth - filledWidth;
      var progressPercentage = progress * 100;

      String progressBar =
          from.getName()
              + " ["
              + ">".repeat(Math.max(0, filledWidth))
              + " ".repeat(Math.max(0, emptyWidth))
              + "] "
              + to.getName()
              + " - "
              + (int) progressPercentage
              + "%";
      out.print("\r" + progressBar);
      try {
        Thread.sleep((long) millisecondsPerStep);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  private static void prepareCli() {
    if (Boolean.TRUE.equals(AppProperties.getIsRunningAsJar())) {
      CliComponent.clearConsole();
      out.printf("%n");
    } else {
      out.printf("%n%n");
    }
  }
}
