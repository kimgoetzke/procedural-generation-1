package com.hindsight.king_of_castrop_rauxel.item;

import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.location.Shop;
import jakarta.persistence.*;
import lombok.*;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "ITEMS_CONSUMABLES")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Consumable implements Buyable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Getter private String name;
  @Getter private int tier;

  @Enumerated(EnumType.STRING)
  private Shop.Type sellerType;

  @Getter private int basePrice;
  private int effectHealth;
  private int effectMaxHealth;

  @Override
  public String getDescription() {
    var description = new StringBuilder();
    description.append(CliComponent.FMT.RESET);
    var restoresHealth = "restores " + CliComponent.health(effectHealth) + " HP, ";
    description.append(effectHealth > 0 ? restoresHealth : "");
    var increasesMaxHealth = "increases max HP by " + CliComponent.health(effectMaxHealth) + ", ";
    description.append(effectMaxHealth > 0 ? increasesMaxHealth : "");
    description.setLength(description.length() - 2);
    return description.toString();
  }

  @Override
  public boolean isBoughtBy(Player player) {
    if (player.getGold() < basePrice) {
      return false;
    }
    player.changeGoldBy(-basePrice);
    player.changeHealthBy(effectHealth);
    player.changeMaxHealthBy(effectMaxHealth);
    return true;
  }
}
