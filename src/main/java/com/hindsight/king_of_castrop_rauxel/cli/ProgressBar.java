package com.hindsight.king_of_castrop_rauxel.cli;

import com.hindsight.king_of_castrop_rauxel.location.Location;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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

  public static final boolean WINDOWS = System.getProperty("os.name").contains("Windows");
  private static final List<String> horseFrames = List.of(HORSE_1, HORSE_2, HORSE_3, HORSE_4);
  private static boolean hideHorsey = true;

  static {
    try {
      hideHorsey =
          ProgressBar.class
                  .getClassLoader()
                  .loadClass("com.intellij.rt.execution.application.AppMainV2")
              != null;
    } catch (ClassNotFoundException ignored) {
      // Not running from IntelliJ so horse animation can be displayed - running the application
      // via IntelliJ will not allow the console to be cleared which is required for the animation
      // to work
    }
    log.info("Running from IntelliJ: " + hideHorsey);
  }

  // TODO: Allow interrupting (returning progress %) and resuming later (accepting progress %)
  public static void displayProgress(Location from, Location to) {
    if (from.equals(to)) {
      return;
    }
    var progressBarWidth = 100;
    var totalSteps = 100;
    var millisecondsPerStep = from.distanceTo(to) * SPEED_MODIFIER;
    var currentStep = 0;
    System.out.printf("%n%n");

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
      System.out.print("\r" + progressBar);
      try {
        Thread.sleep((long) millisecondsPerStep);
        currentStep++;
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  private static void displayHorseFrame(int currentStep) {
    if (hideHorsey) {
      return;
    }
    clearConsole();
    int frameIndex = currentStep % horseFrames.size();
    String frame = horseFrames.get(frameIndex);
    System.out.println(frame);
  }

  private static void clearConsole() {
    try {
      if (WINDOWS) {
        new ProcessBuilder("cmd.exe", "/c", "cls").inheritIO().start().waitFor();
      } else {
        System.out.print("\033[H\033[2J");
        System.out.flush();
      }
    } catch (Exception e) {
      log.info("Failed to clear console", e);
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
    }
  }
}
