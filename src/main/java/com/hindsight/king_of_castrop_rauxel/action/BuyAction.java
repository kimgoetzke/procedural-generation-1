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
    this.name = "Buy: " + CliComponent.buyable(item);
    this.item = item;
  }

  @Override
  public void execute(Player player) {
    var isBought = item.isBoughtBy(player);
    var errorMessage = "Not enough gold to buy: '%s'.%n".formatted(item.getName());
    if (!isBought) {
      System.out.printf(CliComponent.error(errorMessage));
      CliComponent.awaitEnterKeyPress();
    }
    nextState(player);
  }

  @Override
  public State getNextState() {
    return State.AT_POI;
  }
}
