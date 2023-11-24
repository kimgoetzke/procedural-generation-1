package com.hindsight.king_of_castrop_rauxel.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.hindsight.king_of_castrop_rauxel.action.CombatAction;
import com.hindsight.king_of_castrop_rauxel.character.Enemy;
import com.hindsight.king_of_castrop_rauxel.character.Npc;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.utils.DataServices;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

@SpringBootTest
class DungeonTest {

  @Autowired private AppProperties appProperties;
  @Autowired protected Generators generators;
  @Autowired protected DataServices dataServices;

  private static final Pair<Integer, Integer> COORDS = Pair.of(0, 0); // -> Tier 6 dungeon

  @Test
  void givenCoords_returnTier6DungeonInExpectedState() {
    // Given
    var npc = mock(Npc.class);
    var location = new Settlement(COORDS, COORDS, generators, dataServices, appProperties);
    Dungeon underTest = new Dungeon(appProperties, PointOfInterest.Type.DUNGEON, npc, location);

    // Then
    assertThat(underTest.isLoaded()).isTrue();
    assertThat(underTest.getDungeonDetails().tier()).isEqualTo(6);
    var enemyType = underTest.getDungeonDetails().encounterDetails().get(0).get(0).type();
    assertThat(enemyType).isEqualTo(Enemy.Type.DRAGON);
    var enemyCount = underTest.getDungeonDetails().encounterDetails().get(0).size();
    assertThat(enemyCount).isPositive();
    assertThat(underTest.getDescription()).contains("filled with treasure");
    var combatActions =
        underTest.getAvailableActions().stream()
            .filter(a -> a instanceof CombatAction)
            .map(a -> (CombatAction) a)
            .toList();
    assertThat(combatActions).hasSize(1);
  }
}
