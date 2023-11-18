package com.hindsight.king_of_castrop_rauxel.cli.combat;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;
import static java.lang.System.out;

import com.hindsight.king_of_castrop_rauxel.characters.Combatant;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.event.DefeatEvent;
import com.hindsight.king_of_castrop_rauxel.event.Loot;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Encounter {

  private final Random random = new Random();
  private final List<Combatant> initialAllies;
  private final List<Combatant> initialEnemies;
  private final List<Combatant> attackers = new ArrayList<>();
  private final List<Combatant> defenders = new ArrayList<>();
  private final Loot loot = new Loot();
  private final long delayInMs;
  private Player player;
  private boolean isOver;
  private boolean isAttacker;

  public Encounter(List<Combatant> allies, List<Combatant> enemies, AppProperties appProperties) {
    this.delayInMs = appProperties.getGameProperties().delayInMs();
    this.initialAllies = allies;
    this.initialEnemies = enemies;
  }

  public void execute(Player player, boolean isAttacker) {
    this.player = player;
    this.isAttacker = isAttacker;
    initialise();
    complete();
    printWrapUp();
    loot.give(player);
  }

  private void initialise() {
    if (isAttacker) {
      attackers.add(player);
      defenders.addAll(initialEnemies);
      addAlliesTo(attackers);
    } else {
      defenders.add(player);
      attackers.addAll(initialEnemies);
      addAlliesTo(defenders);
    }
    printKickOff();
  }

  private void addAlliesTo(List<Combatant> combatants) {
    if (initialAllies != null) {
      combatants.addAll(initialAllies);
    }
  }

  private void complete() {
    while (!isOver) {
      attackAndEvaluate(attackers, defenders);
      attackAndEvaluate(defenders, attackers);
    }
  }

  private void attackAndEvaluate(List<Combatant> attackingGroup, List<Combatant> defendingGroup) {
    for (var attacker : attackingGroup) {
      try {
        Thread.sleep(delayInMs);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      getTarget(attacker, defendingGroup);
      if (isOver) {
        return;
      }
      var damage = attacker.attack();
      printAttack(attacker, attacker.getTarget(), damage);
      evaluateAttack(attacker.getTarget(), defendingGroup);
    }
  }

  private void evaluateAttack(Combatant target, List<Combatant> defendingGroup) {
    if (target.isAlive()) {
      return;
    }
    var droppedLoot = lootTarget(target);
    printDeath(target, droppedLoot);
    incrementKillCountOfMatchingActiveEvents(target);
    if (isPlayer(target)) {
      isOver = true;
      return;
    }
    defendingGroup.remove(target);
  }

  private Loot lootTarget(Combatant target) {
    var droppedLoot = target.getLoot();
    loot.add(droppedLoot);
    return droppedLoot;
  }

  private void incrementKillCountOfMatchingActiveEvents(Combatant target) {
    player.getActiveEvents().stream()
        .filter(DefeatEvent.class::isInstance)
        .map(e -> (DefeatEvent) e)
        .forEach(e -> e.incrementDefeated(target.getType()));
  }

  private void getTarget(Combatant combatant, List<Combatant> opposingCombatants) {
    if (combatant.hasTarget() && combatant.getTarget().isAlive()) {
      return;
    }
    var possibleTargets = new ArrayList<>(opposingCombatants);
    while (!combatant.hasTarget() && !possibleTargets.isEmpty()) {
      var target = selectNewTarget(possibleTargets);
      if (target.isAlive()) {
        setNewTarget(combatant, target);
        return;
      }
      possibleTargets.remove(target);
    }
    isOver = true;
    combatant.setTarget(null);
  }

  private Combatant selectNewTarget(List<Combatant> possibleTargets) {
    return possibleTargets.get(random.nextInt(possibleTargets.size()));
  }

  private void setNewTarget(Combatant combatant, Combatant target) {
    combatant.setTarget(target);
    if (!target.hasTarget()) {
      target.setTarget(combatant);
    }
  }

  private boolean isPlayer(Combatant combatant) {
    return player.getId().equals(combatant.getId());
  }

  private boolean isEnemy(Combatant combatant) {
    return initialEnemies.contains(combatant);
  }

  private void printKickOff() {
    out.printf("%nYou %s%n%n", isAttacker ? "have the initiative." : "are being surprised.");
    printCombatants(attackers, "Attacker(s)");
    printCombatants(defenders, "Defender(s)");
    out.printf("%nThe fight has started.%n%n");
    CliComponent.awaitEnterKeyPress();
  }

  private void printAttack(Combatant attacker, Combatant target, int damage) {
    var attackerColour = isPlayer(attacker) ? FMT.GREEN_BOLD : FMT.MAGENTA_BOLD;
    var targetColour = isPlayer(target) ? FMT.GREEN_BOLD : FMT.MAGENTA_BOLD;
    if (isEnemy(attacker)) {
      out.printf(
          "- %s%s%s is attacked by %s%s%s  %s-%d%s -> %s%d%s HP%n",
          targetColour,
          target.getName().toUpperCase(),
          FMT.RESET,
          attackerColour,
          attacker.getName().toUpperCase(),
          FMT.RESET,
          FMT.RED,
          damage,
          FMT.RESET,
          FMT.GREEN,
          target.getHealth(),
          FMT.RESET);
      return;
    }
    out.printf(
        "- %s%s%s attacks %s%s%s         %s-%d%s -> %s%d%s HP%n",
        attackerColour,
        attacker.getName().toUpperCase(),
        FMT.RESET,
        targetColour,
        target.getName().toUpperCase(),
        FMT.RESET,
        FMT.GREEN,
        damage,
        FMT.RESET,
        FMT.RED,
        target.getHealth(),
        FMT.RESET);
  }

  private void printCombatants(List<Combatant> combatants) {
    printCombatants(combatants, "");
  }

  private void printCombatants(List<Combatant> combatants, String groupName) {
    var stringBuilder = new StringBuilder();
    if (!groupName.isEmpty()) {
      stringBuilder.append(groupName).append(": ");
    }
    for (int i = 0; i < combatants.size(); i++) {
      stringBuilder.append(combatants.get(i).combatantToString());
      if (i < combatants.size() - 1) {
        stringBuilder.append(", ");
      }
    }
    out.println(stringBuilder);
  }

  private void printDeath(Combatant combatant, Loot loot) {
    var colour = isPlayer(combatant) ? FMT.GREEN_BOLD : FMT.MAGENTA_BOLD;
    out.printf(
        "- %s%s%s has died, dropping %s%n",
        colour, combatant.getName().toUpperCase(), FMT.RESET, loot);
  }

  private void printWrapUp() {
    out.printf("%nThe fight is over!%n%n");
    if (player.isAlive()) {
      out.print(CliComponent.bold("You have won!") + " You have defeated: ");
      printCombatants(initialEnemies);
      out.printf(
          "You have gained: %s. You have %s HP left.%n",
          loot, CliComponent.health(player.getHealth()));
    } else {
      out.printf("%n%s Game over. %nThanks for playing!%n%n", CliComponent.bold("You have died!"));
      System.exit(0);
    }
  }
}
