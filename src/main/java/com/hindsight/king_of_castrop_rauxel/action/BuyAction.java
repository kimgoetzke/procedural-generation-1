package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.character.Player.*;
import static com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver.*;

import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.item.Buyable;
import com.hindsight.king_of_castrop_rauxel.web.exception.InGameException;
import lombok.Getter;
import lombok.Setter;

/** This action exchange the player's gold for a Buyable such as a Consumable. */
@Getter
public class BuyAction implements Action {

  @Setter private Environment environment;
  @Setter private int index;
  @Setter private String name;
  private final Buyable item;

  public BuyAction(int index, Buyable item, Environment environment) {
    this.environment = environment;
    this.index = index;
    this.name = "Buy: " + CliComponent.buyable(item);
    this.item = item;
  }

  @Override
  public void execute(Player player) {
    switch (environment) {
      case CLI -> executeCli(player);
      case WEB -> executeWeb(player);
    }
  }

  private void executeCli(Player player) {
    var isBought = item.isBoughtBy(player);
    var errorMessage = "Not enough gold to buy: '%s'.%n".formatted(item.getName());
    if (!isBought) {
      System.out.printf(CliComponent.error(errorMessage));
      CliComponent.awaitEnterKeyPress();
    }
    nextState(player);
  }

  private void executeWeb(Player player) {
    var isBought = item.isBoughtBy(player);
    var errorMessage = "Not enough gold to buy: '%s'.".formatted(item.getName());
    if (!isBought) {
      nextState(player);
      throw new InGameException(errorMessage);
    }
  }

  @Override
  public State getNextState() {
    return State.AT_POI;
  }
}
