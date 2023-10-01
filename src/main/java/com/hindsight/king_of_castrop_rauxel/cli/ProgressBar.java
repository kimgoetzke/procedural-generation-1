package com.hindsight.king_of_castrop_rauxel.cli;

import com.hindsight.king_of_castrop_rauxel.location.Location;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.System.out;

@Slf4j
@Component
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ProgressBar {

  private static final String HORSE_1 =
      """
                      ++=
                  =@@@@@@@@
    #*@@@%@@@@@@@@@@@@@@ :%@
  :@@    @@@@@@@@@@@@@@
   .    *@@@=-@@@@@@@@#
       @- %.       .#  @:
      -  *          :    +
    #     *         +
                    :
    """;

  private static final String HORSE_2 =
      """
                      .+#%-
                    #@@@@%%%-
          -#@@#+==+#@@@@#
+#-=%@@%--@@@@@@@@@@@@@@
  =+=.    -%@@@@@@@@@@@*
          .*%-    ..*%*+.
           =#*      +=...
              =  :. :
    """;

  private static final String HORSE_3 =
      """
                    .+:
                *%@@@@@%-
               :%@@@@+  .
  =##=.=%@@@@@%%@@@@@=
+@@*   %@@@@@@@@@@@@@%
::       =@@@%@@@@@@@@@=:
          #+        .+  ..
          *= .-          :
            :
  """;

  private static final String HORSE_4 =
      """
                     *@@@@
         *@@=    -@@@@@@%@@.
   #@@#-@@@@@@@@@@@@@@@    +
  @@@  -@@#%@@@@@@@@@@*=-
        %@ *@       @@:   +
       %*   @.        +*
       =       %.       +:
        +                 .
    """;

  /**
   * The speed modifier for the progress bar. The higher the value, the slower the progress.
   * Examples: A modifier of 1 means that it takes 10 seconds to travel a distance of 100 km. A
   * modifier of 0.5 means that it takes 5 seconds to travel a distance of 100 km.
   */
  public static final float SPEED_MODIFIER = 0.1F;

  private static final List<String> horseFrames = List.of(HORSE_1, HORSE_2, HORSE_3, HORSE_4);

  // TODO: Allow interrupting (returning progress %) and resuming later (accepting progress %)
  public static void displayProgress(Location from, Location to) {
    if (from.equals(to)) {
      return;
    }
    var progressBarWidth = 100;
    var totalSteps = 100;
    var millisecondsPerStep = from.distanceTo(to) * SPEED_MODIFIER;
    var currentStep = 0;
    out.printf("%n%n");

    for (int step = 0; step <= totalSteps; step++) {
      var progress = (float) step / totalSteps;
      var filledWidth = (int) (progress * progressBarWidth);
      var emptyWidth = progressBarWidth - filledWidth;
      var progressPercentage = progress * 100;

      displayHorseFrame(currentStep);
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
        currentStep++;
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  private static void displayHorseFrame(int currentStep) {
    if (CliComponent.isUsingIntelliJ()) {
      return;
    }
    CliComponent.clearConsole();
    int frameIndex = currentStep % horseFrames.size();
    String frame = horseFrames.get(frameIndex);
    out.println(frame);
  }
}
