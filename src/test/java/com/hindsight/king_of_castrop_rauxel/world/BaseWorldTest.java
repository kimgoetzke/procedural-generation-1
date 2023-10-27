package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;
import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.*;
import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.XL_AMENITIES_QUEST_LOCATION;
import static com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity.*;
import static com.hindsight.king_of_castrop_rauxel.location.LocationBuilder.*;

import com.hindsight.king_of_castrop_rauxel.action.debug.DebugActionFactory;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.location.LocationBuilder;
import com.hindsight.king_of_castrop_rauxel.location.Size;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class BaseWorldTest {

  protected static final Map<Size, SettlementConfig> fakeConfig = new EnumMap<>(Size.class);

  @Autowired protected Generators generators;
  @Autowired protected AppProperties appProperties;
  @Autowired protected WorldHandler worldHandler;

  protected Chunk chunk;
  protected World world;
  protected Graph<AbstractLocation> map;
  protected DebugActionFactory daf;

  public BaseWorldTest() {
    generateFakeConfig();
  }

  @AfterEach
  void tearDown() {
    map = null;
    worldHandler = null;
    world = null;
    daf = null;
  }

  protected abstract void locationComponentIsInitialised(MockedStatic<LocationBuilder> mocked);

  protected <T extends AbstractLocation> void debug(
      List<Vertex<T>> vertices, Graph<AbstractLocation> map) {
    worldHandler.logDisconnectedVertices(map);
    var connectivityResult = worldHandler.evaluateConnectivity(map);
    System.out.println("Unvisited vertices: " + connectivityResult.unvisitedVertices().size());
    debugSet(vertices, connectivityResult.unvisitedVertices());
    System.out.println("Visited vertices: " + connectivityResult.visitedVertices().size());
    debugSet(vertices, connectivityResult.visitedVertices());
    try {
      daf.printPlane(world, map);
    } catch (Exception e) {
      System.out.printf(
          FMT.RED_BRIGHT
              + "Error: Could not print plane - this happens usually because 1) the setUp()/tearDown() does not reset all fields correctly or 2) you never call setCurrentChunk().%n"
              + FMT.RESET);
    }
    daf.printConnectivity();
  }

  protected <T extends AbstractLocation> void debugSet(
      List<Vertex<T>> vertices, Set<Vertex<AbstractLocation>> vertexSet) {
    vertexSet.forEach(
        v -> {
          System.out.println(v.getLocation().getBriefSummary());
          v.getLocation()
              .getNeighbours()
              .forEach(n -> System.out.println("- neighbour of: " + n.getName()));
          vertices.forEach(
              vOther ->
                  System.out.printf(
                      "- distance to %s: %s%n",
                      vOther.getLocation().getName(),
                      vOther.getLocation().distanceTo(v.getLocation())));
        });
  }

  private void generateFakeConfig() {
    SettlementConfig xs = new SettlementConfig();
    SettlementConfig s = new SettlementConfig();
    SettlementConfig m = new SettlementConfig();
    SettlementConfig l = new SettlementConfig();
    SettlementConfig xl = new SettlementConfig();

    xs.setInhabitants(XS_INHABITANTS);
    s.setInhabitants(S_INHABITANTS);
    m.setInhabitants(M_INHABITANTS);
    l.setInhabitants(L_INHABITANTS);
    xl.setInhabitants(XL_INHABITANTS);

    xs.setArea(XS_AREA);
    s.setArea(S_AREA);
    m.setArea(M_AREA);
    l.setArea(L_AREA);
    xl.setArea(XL_AREA);

    EnumMap<Type, Bounds> xsAmenities = new EnumMap<>(Type.class);
    EnumMap<Type, Bounds> sAmenities = new EnumMap<>(Type.class);
    EnumMap<Type, Bounds> mAmenities = new EnumMap<>(Type.class);
    EnumMap<Type, Bounds> lAmenities = new EnumMap<>(Type.class);
    EnumMap<Type, Bounds> xlAmenities = new EnumMap<>(Type.class);

    xsAmenities.put(Type.ENTRANCE, XS_AMENITIES_ENTRANCE);
    sAmenities.put(Type.ENTRANCE, S_AMENITIES_ENTRANCE);
    mAmenities.put(Type.ENTRANCE, M_AMENITIES_ENTRANCE);
    lAmenities.put(Type.ENTRANCE, L_AMENITIES_ENTRANCE);
    xlAmenities.put(Type.ENTRANCE, XL_AMENITIES_ENTRANCE);

    xsAmenities.put(Type.MAIN_SQUARE, AMENITIES_MAIN_SQUARE);
    sAmenities.put(Type.MAIN_SQUARE, AMENITIES_MAIN_SQUARE);
    mAmenities.put(Type.MAIN_SQUARE, AMENITIES_MAIN_SQUARE);
    lAmenities.put(Type.MAIN_SQUARE, AMENITIES_MAIN_SQUARE);
    xlAmenities.put(Type.MAIN_SQUARE, AMENITIES_MAIN_SQUARE);

    xsAmenities.put(Type.SHOP, XS_AMENITIES_SHOP);
    sAmenities.put(Type.SHOP, S_AMENITIES_SHOP);
    mAmenities.put(Type.SHOP, M_AMENITIES_SHOP);
    lAmenities.put(Type.SHOP, L_AMENITIES_SHOP);
    xlAmenities.put(Type.SHOP, XL_AMENITIES_SHOP);

    xsAmenities.put(Type.QUEST_LOCATION, XS_AMENITIES_QUEST_LOCATION);
    sAmenities.put(Type.QUEST_LOCATION, S_AMENITIES_QUEST_LOCATION);
    mAmenities.put(Type.QUEST_LOCATION, M_AMENITIES_QUEST_LOCATION);
    lAmenities.put(Type.QUEST_LOCATION, L_AMENITIES_QUEST_LOCATION);
    xlAmenities.put(Type.QUEST_LOCATION, XL_AMENITIES_QUEST_LOCATION);

    xsAmenities.put(Type.DUNGEON, XL_AMENITIES_DUNGEON);
    sAmenities.put(Type.DUNGEON, S_AMENITIES_DUNGEON);
    mAmenities.put(Type.DUNGEON, M_AMENITIES_DUNGEON);
    lAmenities.put(Type.DUNGEON, L_AMENITIES_DUNGEON);
    xlAmenities.put(Type.DUNGEON, XL_AMENITIES_DUNGEON);

    xs.setAmenities(xsAmenities);
    s.setAmenities(sAmenities);
    m.setAmenities(mAmenities);
    l.setAmenities(lAmenities);
    xl.setAmenities(xlAmenities);

    fakeConfig.put(Size.XS, xs);
    fakeConfig.put(Size.S, s);
    fakeConfig.put(Size.M, m);
    fakeConfig.put(Size.L, l);
    fakeConfig.put(Size.XL, xl);
  }
}
