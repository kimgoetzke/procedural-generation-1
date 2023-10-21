package com.hindsight.king_of_castrop_rauxel.cli.combat;

import com.hindsight.king_of_castrop_rauxel.characters.Combatant;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.event.Reward;
import com.hindsight.king_of_castrop_rauxel.event.Rewards;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;
import static java.lang.System.out;

public class Encounter {

  public static final long DELAY_IN_MS = 175;
  private final Random random = new Random();
  private Player player;
  List<Combatant> baseAllies;
  List<Combatant> baseEnemies;
  Rewards loot = new Rewards();
  private boolean isOver = false;

  public Encounter(List<Combatant> allies, List<Combatant> enemies) {
    this.baseAllies = allies;
    this.baseEnemies = enemies;
  }

  public void execute(Player player, boolean hasTheInitiative) {
    this.player = player;
    var attackers = new ArrayList<Combatant>();
    var defenders = new ArrayList<Combatant>();
    initialise(hasTheInitiative, attackers, defenders);
    complete(attackers, defenders);
    wrapUp();
  }

  private void initialise(
      boolean hasTheInitiative, ArrayList<Combatant> attackers, ArrayList<Combatant> defenders) {
    if (hasTheInitiative) {
      attackers.add(player);
      defenders.addAll(baseEnemies);
      addAlliesTo(attackers);
    } else {
      defenders.add(player);
      attackers.addAll(baseEnemies);
      addAlliesTo(defenders);
    }
    out.printf(
        "%nA fight has started. You %s%n%n",
        hasTheInitiative ? "have the initiative." : "are being surprised.");
    CliComponent.awaitEnterKeyPress();
  }

  private void addAlliesTo(ArrayList<Combatant> combatants) {
    if (baseAllies != null) {
      combatants.addAll(baseAllies);
    }
  }

  private void complete(ArrayList<Combatant> attackers, ArrayList<Combatant> defenders) {
    while (!isOver) {
      attackAndEvaluate(attackers, defenders);
      attackAndEvaluate(defenders, attackers);
    }
  }

  private void attackAndEvaluate(
      ArrayList<Combatant> attackingGroup, ArrayList<Combatant> defendingGroup) {
    for (var attacker : attackingGroup) {
      try {
        Thread.sleep(DELAY_IN_MS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      getTarget(attacker, defendingGroup);
      var damage = attacker.attack();
      printAttack(attacker, attacker.getTarget(), damage);
      evaluateAttack(attacker.getTarget(), defendingGroup);
    }
  }

  private void wrapUp() {
    out.println("The fight is over!");
    if (player.isAlive()) {
      out.println("You have won! You have gained:");
      loot.print();
      loot.give(player);
    } else {
      out.printf("You have died!%nGame over. Thanks for playing!");
      System.exit(0);
    }
    out.println();
    CliComponent.awaitEnterKeyPress();
  }

  private void printAttack(Combatant attacker, Combatant target, int damage) {
    var attackerColour = isPlayer(attacker) ? FMT.GREEN_BOLD : FMT.MAGENTA_BOLD;
    var targetColour = isPlayer(target) ? FMT.GREEN_BOLD : FMT.MAGENTA_BOLD;
    if (isEnemy(attacker)) {
      out.printf(
          "- %s%s%s is attacked by %s%s%s  %s-%d%s -> %s%d%s health%n",
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
        "- %s%s%s attacks %s%s%s  %s-%d%s -> %s%d%s health%n",
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

  private void evaluateAttack(Combatant target, ArrayList<Combatant> defendingGroup) {
    if (target.isAlive()) {
      return;
    }
    var droppedLoot = target.getReward();
    loot.addAll(droppedLoot);
    printDeath(target, droppedLoot);
    if (isPlayer(target)) {
      isOver = true;
      return;
    }
    defendingGroup.remove(target);
  }

  private void printDeath(Combatant combatant, List<Reward> loot) {
    var colour = isPlayer(combatant) ? FMT.GREEN_BOLD : FMT.MAGENTA_BOLD;
    out.printf(
        "- %s%s%s has died, dropping %s%n",
        colour, combatant.getName().toUpperCase(), FMT.RESET, loot);
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
    return combatant.getId().equals(player.getId());
  }

  private boolean isEnemy(Combatant combatant) {
    return baseEnemies.contains(combatant);
  }
}
