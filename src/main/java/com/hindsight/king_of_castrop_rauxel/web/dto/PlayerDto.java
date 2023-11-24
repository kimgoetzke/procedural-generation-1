package com.hindsight.king_of_castrop_rauxel.web.dto;

import com.hindsight.king_of_castrop_rauxel.character.Player;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("JpaDataSourceORMInspection")
@Data
@Table(name = "PLAYER")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDto {

  @Id String id;
  private String name;
  private int x;
  private int y;
  private int gold;
  private int minDamage;
  private int maxDamage;
  private int health;
  private int maxHealth;
  private int experience;
  private int level;

  @Enumerated(EnumType.STRING)
  private Player.State previousState;

  @Enumerated(EnumType.STRING)
  private Player.State currentState;

  public static PlayerDto from(Player player) {
    var dto = new PlayerDto();
    dto.id = player.getId();
    dto.x = player.getCoordinates().gX();
    dto.y = player.getCoordinates().gY();
    dto.name = player.getName();
    dto.gold = player.getGold();
    dto.minDamage = player.getDamage().getMin();
    dto.maxDamage = player.getDamage().getMax();
    dto.health = player.getHealth();
    dto.maxHealth = player.getMaxHealth();
    dto.experience = player.getExperience();
    dto.level = player.getLevel();
    dto.previousState = player.getPreviousState();
    dto.currentState = player.getState();
    return dto;
  }
}
