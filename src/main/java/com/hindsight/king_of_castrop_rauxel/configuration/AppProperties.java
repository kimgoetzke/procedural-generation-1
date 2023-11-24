package com.hindsight.king_of_castrop_rauxel.configuration;

import com.hindsight.king_of_castrop_rauxel.character.Enemy;
import com.hindsight.king_of_castrop_rauxel.encounter.Damage;
import com.hindsight.king_of_castrop_rauxel.encounter.DungeonHandler;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import com.hindsight.king_of_castrop_rauxel.location.Size;
import com.hindsight.king_of_castrop_rauxel.world.Bounds;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = "settings")
public class AppProperties {

  private GeneralProperties generalProperties;
  private GameProperties gameProperties;
  private WorldProperties worldProperties;
  private ChunkProperties chunkProperties;
  private SettlementProperties settlementProperties;
  private DungeonProperties dungeonProperties;
  private EnemyProperties enemyProperties;
  private PlayerProperties playerProperties;

  @Getter private static Boolean isRunningAsJar;

  static {
    determineRuntimeEnvironment();
  }

  private static void determineRuntimeEnvironment() {
    var protocol = AppProperties.class.getResource(AppProperties.class.getSimpleName() + ".class");
    if (protocol == null) {
      throwInvalidRuntime(null);
    }
    switch (protocol.getProtocol()) {
      case "jar" -> isRunningAsJar = true;
      case "file" -> isRunningAsJar = false;
      default -> throwInvalidRuntime(protocol.getProtocol());
    }
    log.info("Running " + (Boolean.TRUE.equals(isRunningAsJar) ? "as JAR" : "inside IDE"));
  }

  private static void throwInvalidRuntime(String env) {
    throw new IllegalStateException(
        "Runtime environment is %s but must be JAR or IDE ".formatted(env));
  }

  public record GeneralProperties(boolean autoUnload, boolean useConsoleUi, boolean clearConsole) {}

  public record GameProperties(long delayInMs, float speedModifier, int levelToTierDivider) {}

  public record WorldProperties(int size, int centre, int retentionZone) {}

  public record ChunkProperties(
      int size,
      int minPlacementDistance,
      int maxGuaranteedNeighbourDistance,
      int generationTriggerZone,
      Bounds density) {}

  public record SettlementProperties(
      SettlementConfig xs,
      SettlementConfig s,
      SettlementConfig m,
      SettlementConfig l,
      SettlementConfig xl) {

    public SettlementConfig get(Size size) {
      return switch (size) {
        case XS -> xs;
        case S -> s;
        case M -> m;
        case L -> l;
        case XL -> xl;
      };
    }
  }

  @Getter
  @Setter
  public static class SettlementConfig {
    private Bounds area;
    private Bounds inhabitants;
    private Map<PointOfInterest.Type, Bounds> amenities;

    @Override
    public String toString() {
      return "{area=" + area + ", inhabitants=" + inhabitants + ", amenities=" + amenities + '}';
    }
  }

  public record DungeonProperties(
      Bounds encountersPerDungeon,
      Bounds enemiesPerEncounter,
      List<Enemy.Type> t1Types,
      List<Enemy.Type> t2Types,
      List<Enemy.Type> t3Types,
      List<Enemy.Type> t4Types,
      List<Enemy.Type> t5Types,
      List<Enemy.Type> t6Types) {

    @SuppressWarnings("DuplicatedCode")
    public List<Enemy.Type> getType(int tier) {
      return switch (tier) {
        case 1 -> t1Types;
        case 2 -> t2Types;
        case 3 -> t3Types;
        case 4 -> t4Types;
        case 5 -> t5Types;
        case 6 -> t6Types;
        default -> throw new IllegalArgumentException("Tier %s does not exist".formatted(tier));
      };
    }
  }

  public record EnemyProperties(
      DungeonHandler.EnemyConfig t1,
      DungeonHandler.EnemyConfig t2,
      DungeonHandler.EnemyConfig t3,
      DungeonHandler.EnemyConfig t4,
      DungeonHandler.EnemyConfig t5,
      DungeonHandler.EnemyConfig t6) {

    @SuppressWarnings("DuplicatedCode")
    public DungeonHandler.EnemyConfig get(int tier) {
      return switch (tier) {
        case 1 -> t1;
        case 2 -> t2;
        case 3 -> t3;
        case 4 -> t4;
        case 5 -> t5;
        case 6 -> t6;
        default -> throw new IllegalArgumentException("Tier %s does not exist".formatted(tier));
      };
    }
  }

  public record PlayerProperties(
      int startingGold, int startingMaxHealth, int experienceToLevelUp, Damage startingDamage) {}
}
