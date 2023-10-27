package com.hindsight.king_of_castrop_rauxel.configuration;

import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.utils.*;
import com.hindsight.king_of_castrop_rauxel.utils.BasicTerrainGenerator;
import com.hindsight.king_of_castrop_rauxel.utils.TerrainGenerator;
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
  public NameGenerator nameGenerator() {
    return new BasicNameGenerator(folderReader());
  }

  @Bean
  public EventGenerator eventGenerator() {
    return new BasicEventGenerator(folderReader());
  }

  @Bean
  public TerrainGenerator terrainGenerator() {
    return new BasicTerrainGenerator();
  }

  @Bean
  public Generators generators() {
    return new Generators(nameGenerator(), eventGenerator(), terrainGenerator());
  }

  @Bean
  public FolderReader folderReader() {
    return new FolderReader();
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
    return new WorldHandler(map(), generators());
  }
}
