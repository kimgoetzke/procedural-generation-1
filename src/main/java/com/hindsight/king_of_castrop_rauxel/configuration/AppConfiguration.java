package com.hindsight.king_of_castrop_rauxel.configuration;

import com.hindsight.king_of_castrop_rauxel.encounter.EncounterHandler;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.items.ConsumableService;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.location.LocationFactory;
import com.hindsight.king_of_castrop_rauxel.location.LocationHandler;
import com.hindsight.king_of_castrop_rauxel.location.PoiFactory;
import com.hindsight.king_of_castrop_rauxel.utils.*;
import com.hindsight.king_of_castrop_rauxel.utils.BasicTerrainGenerator;
import com.hindsight.king_of_castrop_rauxel.utils.TerrainGenerator;
import com.hindsight.king_of_castrop_rauxel.world.World;
import com.hindsight.king_of_castrop_rauxel.world.WorldHandler;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AppConfiguration {

  private final ConsumableService consumableService;

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
    return new BasicTerrainGenerator(appProperties());
  }

  @Bean
  public Generators generators() {
    return new Generators(nameGenerator(), eventGenerator(), terrainGenerator());
  }

  @Bean
  public DataServices dataServices() {
    return new DataServices(consumableService);
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
  public EncounterHandler encounterHandler() {
    return new EncounterHandler(appProperties());
  }

  @Bean
  public LocationFactory locationFactory() {
    return new LocationFactory(appProperties(), generators(), dataServices(), poiFactory());
  }

  @Bean
  public PoiFactory poiFactory() {
    return new PoiFactory(appProperties(), encounterHandler());
  }

  @Bean
  public LocationHandler locationHandler() {
    return new LocationHandler(appProperties(), locationFactory());
  }

  @Bean
  public WorldHandler worldBuilder() {
    return new WorldHandler(map(), appProperties(), locationFactory());
  }
}
