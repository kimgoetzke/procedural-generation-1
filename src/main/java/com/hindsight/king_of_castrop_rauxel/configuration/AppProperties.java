package com.hindsight.king_of_castrop_rauxel.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "generation")
public class AppProperties {

  private AutoUnload autoUnload;

  @Getter
  @Setter
  public static class AutoUnload {

    private boolean world;
  }
}
