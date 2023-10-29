package com.hindsight.king_of_castrop_rauxel.items;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "ITEMS_CONSUMABLES")
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Consumable implements Buyable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Getter private String name;
  private int tier;
  private int effectHealth;
  @Getter private int basePrice;

  @Override
  public String getDescription() {
    return "Restores %s HP".formatted(CliComponent.health(effectHealth));
  }

  @Override
  public void boughtBy(Player player) {
    player.addGold(-basePrice);
    player.setHealth(player.getHealth() + effectHealth);
  }
}
