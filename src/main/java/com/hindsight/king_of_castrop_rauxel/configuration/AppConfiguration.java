package com.hindsight.king_of_castrop_rauxel.configuration;

import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.utils.BasicEventGenerator;
import com.hindsight.king_of_castrop_rauxel.utils.BasicNameGenerator;
import com.hindsight.king_of_castrop_rauxel.utils.EventGenerator;
import com.hindsight.king_of_castrop_rauxel.utils.NameGenerator;
import com.hindsight.king_of_castrop_rauxel.world.World;
import com.hindsight.king_of_castrop_rauxel.world.WorldHandler;
import java.util.Scanner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

  @Bean
  public Scanner scanner() {
    return new Scanner(System.in);
  }

  @Bean
  public NameGenerator stringGenerator() {
    return new BasicNameGenerator();
  }

  @Bean
  public EventGenerator eventGenerator() {
    return new BasicEventGenerator();
  }

  @Bean
  public AppProperties appProperties() {
    return new AppProperties();
  }

  @Bean
  public World world() {
    return new World(appProperties(), worldBuilder());
  }

  @Bean
  public Graph<AbstractLocation> map() {
    return new Graph<>(true);
  }

  @Bean
  public WorldHandler worldBuilder() {
    return new WorldHandler(map(), stringGenerator(), eventGenerator());
  }
}
