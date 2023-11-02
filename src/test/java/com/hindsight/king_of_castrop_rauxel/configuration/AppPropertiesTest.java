package com.hindsight.king_of_castrop_rauxel.configuration;

import static com.hindsight.king_of_castrop_rauxel.encounter.DungeonDetails.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AppPropertiesTest {

  @Autowired private AppProperties appProperties;

  @Test
  void loadsPropertiesFromAllIncludedProfiles() {
    assertThat(appProperties.getAutoUnload().world()).isTrue();
    assertThat(appProperties.getEnvironment().useConsoleUi()).isFalse();
    assertThat(appProperties.getWorldProperties().size()).isEqualTo(50);
  }

  @Test
  void loadsComplexPropertyTypes() {
    assertThat(appProperties.getPlayerProperties().startingDamage().getMin()).isEqualTo(1);
    assertThat(appProperties.getPlayerProperties().startingDamage().getMax()).isEqualTo(4);
    assertThat(appProperties.getChunkProperties().density().getLower()).isEqualTo(5);
    assertThat(appProperties.getChunkProperties().density().getUpper()).isEqualTo(10);
    assertThat(appProperties.getGameProperties().delayInMs()).isEqualTo(175L);
    assertThat(appProperties.getGameProperties().speedModifier()).isEqualTo(0.1F);
    assertThat(appProperties.getEnemyProperties().t3HpXpGold().getMultiplier()).isEqualTo(6);
    assertThat(appProperties.getEnemyProperties().t3HpXpGold().getMinMod()).isEqualTo(0.8F);
    assertThat(appProperties.getEnemyProperties().t3HpXpGold().getMaxMod()).isEqualTo(1.2F);
    assertThat(appProperties.getEnemyProperties().t5Damage().getMultiplier()).isEqualTo(1);
    assertThat(appProperties.getEnemyProperties().t5Damage().getMinMod()).isEqualTo(1.2F);
    assertThat(appProperties.getEnemyProperties().t5Damage().getMaxMod()).isEqualTo(2F);
    var expectedList = List.of(Type.SKELETON, Type.UNDEAD, Type.DEMON);
    assertThat(appProperties.getDungeonProperties().t2Types()).containsAll(expectedList);
    var xsSettlement = appProperties.getSettlementProperties().xs();
    assertThat(xsSettlement.getArea().getLower()).isEqualTo(1);
    assertThat(xsSettlement.getInhabitants().getUpper()).isEqualTo(10);
    assertThat(xsSettlement.getAmenities().get(PointOfInterest.Type.ENTRANCE).getUpper()).isZero();
    var mSettlementAmenities = appProperties.getSettlementProperties().m().getAmenities();
    assertThat(mSettlementAmenities.get(PointOfInterest.Type.SHOP).getUpper()).isEqualTo(5);
  }
}
