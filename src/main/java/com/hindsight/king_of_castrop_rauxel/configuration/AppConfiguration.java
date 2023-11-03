package com.hindsight.king_of_castrop_rauxel.configuration;

import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.items.ConsumableService;
import com.hindsight.king_of_castrop_rauxel.utils.*;
import com.hindsight.king_of_castrop_rauxel.utils.BasicTerrainGenerator;
import com.hindsight.king_of_castrop_rauxel.world.CoordinateFactory;
import com.hindsight.king_of_castrop_rauxel.world.World;
import com.hindsight.king_of_castrop_rauxel.world.ChunkHandler;
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
  public AppProperties appProperties() {
    return new AppProperties();
  }

  @Bean
  public Generators generators() {
    var nameGenerator = new BasicNameGenerator(new FolderReader());
    var eventGenerator = new BasicEventGenerator(new FolderReader());
    var terrainGenerator = new BasicTerrainGenerator(appProperties());
    return new Generators(nameGenerator, eventGenerator, terrainGenerator);
  }

  @Bean
  public DataServices dataServices() {
    return new DataServices(consumableService);
  }

  @Bean
  public Graph map() {
    return new Graph(true);
  }

  @Bean
  public World world() {
    return new World(appProperties(), chunkHandler());
  }

  @Bean
  public ChunkHandler chunkHandler() {
    return new ChunkHandler(map(), appProperties(), generators(), dataServices());
  }

  @Bean
  public CoordinateFactory coordinateFactory() {
    return new CoordinateFactory(appProperties());
  }
}
