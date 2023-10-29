package com.hindsight.king_of_castrop_rauxel.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentResolver {

  @Value("${spring.profiles.active}")
  private String activeProfile;

  public boolean isDev() {
    return activeProfile.contains("dev");
  }

  public boolean isCli() {
    return activeProfile.contains("cli");
  }

  public boolean isNotCli() {
    return !activeProfile.contains("cli");
  }
}
