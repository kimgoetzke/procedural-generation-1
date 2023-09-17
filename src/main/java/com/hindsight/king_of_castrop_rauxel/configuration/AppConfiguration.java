package com.hindsight.king_of_castrop_rauxel.configuration;

import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.utils.BasicStringGenerator;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import com.hindsight.king_of_castrop_rauxel.world.World;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
public class AppConfiguration {

  @Bean
  public Scanner scanner() {
    return new Scanner(System.in);
  }

  @Bean
  public StringGenerator stringGenerator() {
    return new BasicStringGenerator();
  }

  @Bean
  public World world() {
    return new World();
  }

  @Bean
  public Graph<AbstractLocation> map() {
    return new Graph<>(true);
  }
}
