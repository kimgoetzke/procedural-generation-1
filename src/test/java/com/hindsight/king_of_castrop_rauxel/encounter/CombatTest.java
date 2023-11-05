package com.hindsight.king_of_castrop_rauxel.encounter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.hindsight.king_of_castrop_rauxel.characters.Combatant;
import com.hindsight.king_of_castrop_rauxel.characters.Enemy;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.combat.Encounter;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.utils.DataServices;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.Pair;

@SpringBootTest
class CombatTest {

  @Autowired private AppProperties appProperties;
  @Autowired protected Generators generators;
  @Autowired protected DataServices dataServices;
  @Autowired protected ApplicationContext ctx;
  private Encounter underTest;
  private Player player;
  private List<Enemy> enemies;

  @BeforeEach
  void setUp() {
    var dungeonHandler = new DungeonHandler(appProperties);
    var random = new Random(1L);
    var details = dungeonHandler.getEncounterDetails(random, 1, Enemy.Type.IMP);
    var encounterSequence = new EncounterSequence(appProperties, null, null);
    var startLocation =
        new Settlement(Pair.of(0, 0), Pair.of(0, 0), generators, dataServices, appProperties);
    player = new Player("Name", startLocation, appProperties);
    enemies = new ArrayList<>();
    underTest = new Encounter(null, null, appProperties);
  }

  @Test
  void test() {
    assertThat(player).isNotNull();
  }

  private List<Combatant> createEnemies(int count) {
    var enemies = new ArrayList<Combatant>();
    for (int i = 0; i < count; i++) {
      enemies.add(mock(Combatant.class));
    }
    return enemies;
  }
}
