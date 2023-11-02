package com.hindsight.king_of_castrop_rauxel.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
    var protocol = AppProperties.class.getResource(AppProperties.class.getSimpleName() + ".class");
    if (protocol == null) {
      throwInvalidRuntime(null);
    }
    switch (protocol.getProtocol()) {
      case "jar" -> isRunningAsJar = true;
      case "file" -> isRunningAsJar = false;
      default -> throwInvalidRuntime(protocol.getProtocol());
    }
    log.info("Running " + (Boolean.TRUE.equals(isRunningAsJar) ? "as JAR" : "inside IDE"));
  }

  private static void throwInvalidRuntime(String env) {
    throw new IllegalStateException(
        "Runtime environment is %s but must be JAR or IDE ".formatted(env));
  }
}
