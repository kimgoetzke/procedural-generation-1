package com.hindsight.king_of_castrop_rauxel.cli;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.lang.System.out;

@Slf4j
@Component
public class CliComponent {

  public static final boolean WINDOWS = System.getProperty("os.name").contains("Windows");

  @Getter private static boolean isRunningInIntelliJ = true;

  public enum FMT {
    RESET("\033[0m"),

    // Normal colour font
    BLACK("\033[0;30m"),
    RED("\033[0;31m"),
    GREEN("\033[0;32m"),
    YELLOW("\033[0;33m"),
    BLUE("\033[0;34m"),
    MAGENTA("\033[0;35m"),
    CYAN("\033[0;36m"),
    WHITE("\033[0;37m"),
    DEFAULT("\033[0;39m"),

    // Bold font
    BLACK_BOLD("\033[1;30m"),
    RED_BOLD("\033[1;31m"),
    GREEN_BOLD("\033[1;32m"),
    YELLOW_BOLD("\033[1;33m"),
    BLUE_BOLD("\033[1;34m"),
    MAGENTA_BOLD("\033[1;35m"),
    CYAN_BOLD("\033[1;36m"),
    WHITE_BOLD("\033[1;37m"),
    DEFAULT_BOLD("\033[1;39m"),

    // Underlined font
    BLACK_UNDERLINED("\033[4;30m"),
    RED_UNDERLINED("\033[4;31m"),
    GREEN_UNDERLINED("\033[4;32m"),
    YELLOW_UNDERLINED("\033[4;33m"),
    BLUE_UNDERLINED("\033[4;34m"),
    MAGENTA_UNDERLINED("\033[4;35m"),
    CYAN_UNDERLINED("\033[4;36m"),
    WHITE_UNDERLINED("\033[4;37m"),

    // Background colours
    BLACK_BACKGROUND("\033[40m"),
    RED_BACKGROUND("\033[41m"),
    GREEN_BACKGROUND("\033[42m"),
    YELLOW_BACKGROUND("\033[43m"),
    BLUE_BACKGROUND("\033[44m"),
    MAGENTA_BACKGROUND("\033[45m"),
    CYAN_BACKGROUND("\033[46m"),
    WHITE_BACKGROUND("\033[47m"),

    // High intensity font colours
    BLACK_BRIGHT("\033[0;90m"),
    RED_BRIGHT("\033[0;91m"),
    GREEN_BRIGHT("\033[0;92m"),
    YELLOW_BRIGHT("\033[0;93m"),
    BLUE_BRIGHT("\033[0;94m"),
    MAGENTA_BRIGHT("\033[0;95m"),
    CYAN_BRIGHT("\033[0;96m"),
    WHITE_BRIGHT("\033[0;97m"),

    // Bold + high intensity font colours
    BLACK_BOLD_BRIGHT("\033[1;90m"),
    RED_BOLD_BRIGHT("\033[1;91m"),
    GREEN_BOLD_BRIGHT("\033[1;92m"),
    YELLOW_BOLD_BRIGHT("\033[1;93m"),
    BLUE_BOLD_BRIGHT("\033[1;94m"),
    MAGENTA_BOLD_BRIGHT("\033[1;95m"),
    CYAN_BOLD_BRIGHT("\033[1;96m"),
    WHITE_BOLD_BRIGHT("\033[1;97m"),

    // High intensity backgrounds colours
    BLACK_BACKGROUND_BRIGHT("\033[0;100m"),
    RED_BACKGROUND_BRIGHT("\033[0;101m"),
    GREEN_BACKGROUND_BRIGHT("\033[0;102m"),
    YELLOW_BACKGROUND_BRIGHT("\033[0;103m"),
    BLUE_BACKGROUND_BRIGHT("\033[0;104m"),
    MAGENTA_BACKGROUND_BRIGHT("\033[0;105m"),
    CYAN_BACKGROUND_BRIGHT("\033[0;106m"),
    WHITE_BACKGROUND_BRIGHT("\033[0;107m");

    private final String code;

    FMT(String code) {
      this.code = code;
    }

    @Override
    public String toString() {
      return code;
    }
  }

  static {
    try {
      isRunningInIntelliJ =
          CliComponent.class
                  .getClassLoader()
                  .loadClass("com.intellij.rt.execution.application.AppMainV2")
              != null;
    } catch (ClassNotFoundException ignored) {
      // Not running from IntelliJ so horse animation can be displayed - running the application
      // via IntelliJ will not allow the console to be cleared which is required for the animation
      // to work
    }
    log.info("Running from IntelliJ: " + isRunningInIntelliJ);
  }

  public static void clearConsole() {
    try {
      if (WINDOWS) {
        new ProcessBuilder("cmd.exe", "/c", "cls").inheritIO().start().waitFor();
      } else {
        out.print("\033[H\033[2J");
        out.flush();
      }
    } catch (Exception e) {
      log.info("Failed to clear console", e);
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
    }
  }
}
