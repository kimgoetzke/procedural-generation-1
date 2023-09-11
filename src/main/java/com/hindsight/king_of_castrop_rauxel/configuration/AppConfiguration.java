package com.hindsight.king_of_castrop_rauxel.configuration;

import com.hindsight.king_of_castrop_rauxel.utils.BasicStringGenerator;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

  @Bean
  public StringGenerator stringGenerator() {
    return new BasicStringGenerator();
  }
}
