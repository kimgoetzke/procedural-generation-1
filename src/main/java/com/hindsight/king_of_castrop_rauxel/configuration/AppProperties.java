package com.hindsight.king_of_castrop_rauxel.configuration;

import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = "settings")
public class AppProperties {

  private Generation generation;

  private AutoUnload autoUnload;

  private Environment environment;

  @Getter private static Boolean isRunningAsJar;

  static {
    determineRuntimeEnvironment();
  }

  public record Generation(AutoUnload autoUnload) {}

  public record AutoUnload(boolean world) {}

  public record Environment(boolean useConsoleUi, boolean clearConsole) {}

  private static void determineRuntimeEnvironment() {
    var protocol = CliComponent.class.getResource(CliComponent.class.getSimpleName() + ".class");
    switch (Objects.requireNonNull(protocol).getProtocol()) {
      case "jar" -> isRunningAsJar = true;
      case "file" -> isRunningAsJar = false;
      default -> log.error("Cannot determine runtime environment (JAR vs IDE)");
    }
    if (isRunningAsJar != null) {
      log.info("Running " + (Boolean.TRUE.equals(isRunningAsJar) ? "as JAR" : "inside IDE"));
    }
  }
}
