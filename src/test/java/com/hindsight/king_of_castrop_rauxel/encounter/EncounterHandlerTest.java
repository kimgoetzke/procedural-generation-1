package com.hindsight.king_of_castrop_rauxel.encounter;

import static org.assertj.core.api.Assertions.assertThat;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class EncounterHandlerTest {

  @Autowired private AppProperties appProperties;

  private EncounterHandler underTest;

  @BeforeEach
  void setUp() {
    underTest = new EncounterHandler(appProperties);
  }

  @ParameterizedTest
  @CsvSource({"1,1", "2,1", "9,1", "10,2", "16,2", "24,3", "25,3", "36,4", "49,5", "50,6"})
  void returnDungeonTierAsExpected(int level, int expectedTier) {
    var actualTier = underTest.getDungeonTier(level);
    assertThat(actualTier).isEqualTo(expectedTier);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5})
  void givenAnyTier_returnValidTierDungeonType(int tier) {
    var expectedTypes =
        switch (tier) {
          case 1 -> appProperties.getDungeonProperties().t1Types();
          case 2 -> appProperties.getDungeonProperties().t2Types();
          case 3 -> appProperties.getDungeonProperties().t3Types();
          case 4 -> appProperties.getDungeonProperties().t4Types();
          case 5 -> appProperties.getDungeonProperties().t5Types();
          default -> throw new IllegalStateException("Unexpected value: " + tier);
        };
    var random = new Random(1L);
    var result = underTest.getDungeonType(random, tier);
    assertThat(result).isIn(expectedTypes);
  }

  @ParameterizedTest
  @CsvSource({"1,IMP", "14,SKELETON"})
  void getEncounterDetails(int level, String type) {
    var random = new Random(1L);
    var dungeonType = DungeonDetails.Type.from(type);
    var result = underTest.getEncounterDetails(random, level, dungeonType);
    var encountersPerDungeon = appProperties.getDungeonProperties().encountersPerDungeon();
    var enemiesPerEncounter = appProperties.getDungeonProperties().enemiesPerEncounter();
    assertThat(result)
        .isNotEmpty()
        .hasSizeGreaterThanOrEqualTo(encountersPerDungeon.getLower())
        .hasSizeLessThanOrEqualTo(encountersPerDungeon.getUpper());
    assertThat(result.get(0))
        .isNotEmpty()
        .hasSizeGreaterThanOrEqualTo(enemiesPerEncounter.getLower())
        .hasSizeLessThanOrEqualTo(enemiesPerEncounter.getUpper());
    var enemyDetails = result.get(0).get(0);
    assertThat(enemyDetails.level()).isEqualTo(level);
    assertThat(enemyDetails.type()).isEqualTo(dungeonType);
  }
}
