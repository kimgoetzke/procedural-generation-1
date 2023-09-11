package com.hindsight.king_of_castrop_rauxel.cli;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired), access = AccessLevel.PRIVATE)
public class NewGame {

  private final StringGenerator stringGenerator;

  private Player player;

  public void start() {
    var name = "Player";
    var startLocation = new Settlement(stringGenerator);
    this.player = new Player(name, startLocation);
    play();
  }

  private void play() {
    System.out.printf("%nWelcome to King of Castrop-Rauxel, %s!%n%n", player.getName());
    System.out.printf(
      "STATS: [ Gold: %s | Level: %s | Age: %s | Activity points left: %s ]%n",
      player.getGold(), player.getLevel(), player.getAge(), player.getActivityPoints());
    Location currentLocation = player.getCurrentLocation();
    System.out.printf("CURRENT LOCATION: %s%n%n", currentLocation.getSummary());
    System.out.printf("What do you want to do?%n");
    player
      .getCurrentLocation()
      .getAvailableActions()
      .forEach(action -> System.out.printf("%s%n", action.getName()));
  }
}
