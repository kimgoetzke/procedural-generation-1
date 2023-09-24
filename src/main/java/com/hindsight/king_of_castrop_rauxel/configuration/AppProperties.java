package com.hindsight.king_of_castrop_rauxel.configuration;

import com.hindsight.king_of_castrop_rauxel.world.Bounds;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "generation")
public class AppProperties {

  // WORLD PROPERTIES
  public static final int WORLD_SIZE = 50;
  public static final int REMOVAL_ZONE = 2;

  // CHUNK PROPERTIES
  public static final int CHUNK_SIZE = 500;
  public static final int MIN_PLACEMENT_DISTANCE = 5;
  public static final int MAX_NEIGHBOUR_DISTANCE = 100;
  public static final int GENERATION_TRIGGER_ZONE = 100;
  public static final Bounds DENSITY = new Bounds(5, 10);

  // LOCATION PROPERTIES
  // Settlements
  public static final Bounds XS_INHABITANTS = new Bounds(1, 10);
  public static final Bounds S_INHABITANTS = new Bounds(11, 100);
  public static final Bounds M_INHABITANTS = new Bounds(101, 1000);
  public static final Bounds L_INHABITANTS = new Bounds(1001, 10000);
  public static final Bounds XL_INHABITANTS = new Bounds(10000, 250000);
  public static final Bounds XS_AREA = new Bounds(1, 1);
  public static final Bounds S_AREA = new Bounds(1, 2);
  public static final Bounds M_AREA = new Bounds(1, 3);
  public static final Bounds L_AREA = new Bounds(2, 8);
  public static final Bounds XL_AREA = new Bounds(15, 40);
  public static final Bounds XS_AMENITIES_ENTRANCE = new Bounds(0, 0);
  public static final Bounds S_AMENITIES_ENTRANCE = new Bounds(0, 1);
  public static final Bounds M_AMENITIES_ENTRANCE = new Bounds(2, 4);
  public static final Bounds L_AMENITIES_ENTRANCE = new Bounds(1, 1);
  public static final Bounds XL_AMENITIES_ENTRANCE = new Bounds(4, 8);
  public static final Bounds XS_AMENITIES_MAIN_SQUARE = new Bounds(1, 1);
  public static final Bounds S_AMENITIES_MAIN_SQUARE = new Bounds(1, 1);
  public static final Bounds M_AMENITIES_MAIN_SQUARE = new Bounds(1, 1);
  public static final Bounds L_AMENITIES_MAIN_SQUARE = new Bounds(1, 1);
  public static final Bounds XL_AMENITIES_MAIN_SQUARE = new Bounds(1, 1);
  public static final Bounds XS_AMENITIES_SHOP = new Bounds(0, 1);
  public static final Bounds S_AMENITIES_SHOP = new Bounds(1, 3);
  public static final Bounds M_AMENITIES_SHOP = new Bounds(3, 6);
  public static final Bounds L_AMENITIES_SHOP = new Bounds(5, 10);
  public static final Bounds XL_AMENITIES_SHOP = new Bounds(8, 14);
  public static final Bounds XS_AMENITIES_QUEST_LOCATION = new Bounds(0, 2);
  public static final Bounds S_AMENITIES_QUEST_LOCATION = new Bounds(2, 5);
  public static final Bounds M_AMENITIES_QUEST_LOCATION = new Bounds(3, 8);
  public static final Bounds L_AMENITIES_QUEST_LOCATION = new Bounds(6, 12);
  public static final Bounds XL_AMENITIES_QUEST_LOCATION = new Bounds(10, 20);

  private AutoUnload autoUnload;

  @Getter
  @Setter
  public static class AutoUnload {

    private boolean world;
  }
}
