package com.hindsight.king_of_castrop_rauxel.encounter;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EncounterBuilderTest {

  @BeforeEach
  void setUp() {
    new EncounterBuilder();
  }

  @ParameterizedTest
  @CsvSource({"1,1", "2,1", "9,1", "10,2", "16,2", "24,3", "25,3", "36,4", "49,5", "50,6"})
  void returnDungeonTierAsExpected(int level, int expectedTier) {
    var actualTier = EncounterBuilder.getDungeonTier(level);
    assertThat(actualTier).isEqualTo(expectedTier);
  }

  @ParameterizedTest
  @MethodSource("tierToTypeList")
  void givenAnyTier_returnValidTierDungeonType(int tier, List<DungeonDetails.Type> expectedTypes) {
    var random = new Random(1L);
    var result = EncounterBuilder.getDungeonType(random, tier);
    assertThat(result).isIn(expectedTypes);
  }

  @ParameterizedTest
  @CsvSource({"1,IMP", "14,SKELETON"})
  void getEncounterDetails(int level, String type) {
    var random = new Random(1L);
    var dungeonType = DungeonDetails.Type.from(type);
    var result = EncounterBuilder.getEncounterDetails(random, level, dungeonType);
    assertThat(result)
        .isNotEmpty()
        .hasSizeGreaterThanOrEqualTo(ENCOUNTERS_PER_DUNGEON.getLower())
        .hasSizeLessThanOrEqualTo(ENCOUNTERS_PER_DUNGEON.getUpper());
    assertThat(result.get(0))
        .isNotEmpty()
        .hasSizeGreaterThanOrEqualTo(ENEMIES_PER_ENCOUNTER.getLower())
        .hasSizeLessThanOrEqualTo(ENEMIES_PER_ENCOUNTER.getUpper());
    var enemyDetails = result.get(0).get(0);
    assertThat(enemyDetails.level()).isEqualTo(level);
    assertThat(enemyDetails.type()).isEqualTo(dungeonType);
  }

  private static Stream<Arguments> tierToTypeList() {
    return Stream.of(
        arguments(1, DUNGEON_TYPES_T1),
        arguments(2, DUNGEON_TYPES_T2),
        arguments(3, DUNGEON_TYPES_T3),
        arguments(4, DUNGEON_TYPES_T4),
        arguments(5, DUNGEON_TYPES_T5));
  }
}
