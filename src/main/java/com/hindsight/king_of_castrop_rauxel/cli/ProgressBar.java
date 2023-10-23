package com.hindsight.king_of_castrop_rauxel.cli;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.SPEED_MODIFIER;
import static java.lang.System.out;

@Slf4j
@Component
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ProgressBar {

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
