package com.hindsight.king_of_castrop_rauxel.items;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "ITEMS_CONSUMABLES")
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Consumable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;
  private int tier;
  private int effectHealth;
  private int basePrice;
}
