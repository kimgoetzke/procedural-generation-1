package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.*;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.items.Buyable;
import lombok.Getter;
import lombok.Setter;

/** This action exchange the player's gold for a Buyable such as a Consumable. */
@Getter
public class BuyAction implements Action {

  @Setter private int index;
  @Setter private String name;
  private final Buyable item;

  public BuyAction(int index, Buyable item) {
    this.index = index;
    this.name = CliComponent.buyable(item);
    this.item = item;
  }

  @Override
  public void execute(Player player) {
    item.boughtBy(player);
    nextState(player);
  }

  @Override
  public State getNextState() {
    return State.AT_POI;
  }
}
