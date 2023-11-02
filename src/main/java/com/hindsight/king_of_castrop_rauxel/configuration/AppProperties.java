package com.hindsight.king_of_castrop_rauxel.configuration;

import com.hindsight.king_of_castrop_rauxel.encounter.Damage;
import com.hindsight.king_of_castrop_rauxel.encounter.DungeonDetails;
import com.hindsight.king_of_castrop_rauxel.location.LocationBuilder;
import com.hindsight.king_of_castrop_rauxel.world.Bounds;
import com.hindsight.king_of_castrop_rauxel.world.Range;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = "settings")
public class AppProperties {

  private AutoUnload autoUnload;
  private Environment environment;
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

  public record AutoUnload(boolean world) {}

  public record Environment(boolean useConsoleUi, boolean clearConsole) {}

  public record GameProperties(long delayInMs, float speedModifier) {}

  public record WorldProperties(int size, int centre, int retentionZone) {}

  public record ChunkProperties(
      int size,
      int minPlacementDistance,
      int maxGuaranteedNeighbourDistance,
      int generationTriggerZone,
      Bounds density) {}

  public record SettlementProperties(
      LocationBuilder.SettlementConfig xs,
      LocationBuilder.SettlementConfig s,
      LocationBuilder.SettlementConfig m,
      LocationBuilder.SettlementConfig l,
      LocationBuilder.SettlementConfig xl) {}

  public record EnemyProperties(
      Range t1HpXpGold,
      Range t2HpXpGold,
      Range t3HpXpGold,
      Range t4HpXpGold,
      Range t5HpXpGold,
      Range t1Damage,
      Range t2Damage,
      Range t3Damage,
      Range t4Damage,
      Range t5Damage) {}

  public record DungeonProperties(
      Bounds encountersPerDungeon,
      Bounds enemiesPerEncounter,
      int levelToTierDivider,
      List<DungeonDetails.Type> t1Types,
      List<DungeonDetails.Type> t2Types,
      List<DungeonDetails.Type> t3Types,
      List<DungeonDetails.Type> t4Types,
      List<DungeonDetails.Type> t5Types) {}

  public record PlayerProperties(
      int startingGold, int startingMaxHealth, int experienceToLevelUp, Damage startingDamage) {}
}
