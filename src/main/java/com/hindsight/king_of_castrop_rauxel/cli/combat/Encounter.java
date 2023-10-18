package com.hindsight.king_of_castrop_rauxel.cli.combat;

import com.hindsight.king_of_castrop_rauxel.characters.Combatant;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.event.Reward;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;

public class Encounter {

  private final Random random = new Random();
  private Player player;
  List<Combatant> attackers;
  List<Combatant> defenders;
  List<Reward> loot;
  private boolean isOver = false;

  public void execute(
      Player player, List<Combatant> allies, List<Combatant> enemies, boolean isPlayerAttacker) {
    initiate(player, allies, enemies, isPlayerAttacker);
    complete();
    wrapUp();
  }

  private void initiate(
      Player player, List<Combatant> allies, List<Combatant> enemies, boolean isPlayerAttacker) {
    this.player = player;
    var alliedCombatants = new ArrayList<Combatant>();
    alliedCombatants.add(player);
    if (allies != null) {
      alliedCombatants.addAll(allies);
    }
    if (isPlayerAttacker) {
      attackers = alliedCombatants;
      defenders = enemies;
      System.out.println("You have the initiative.");
    } else {
      attackers = enemies;
      defenders = alliedCombatants;
      System.out.println("You are being surprised.");
    }
    System.out.println("A fight has started:");
  }

  private void complete() {
    while (!isOver) {
      for (var attacker : attackers) {
        getTarget(attacker, defenders);
        var aDamage = attacker.attack();
        printAttack(attacker, attacker.getTarget(), aDamage);
      }
      evaluate(defenders);

      for (var defender : defenders) {
        getTarget(defender, attackers);
        var dDamage = defender.attack();
        printAttack(defender, defender.getTarget(), dDamage);
      }
      evaluate(attackers);
    }
  }

  private void wrapUp() {
    System.out.println("The fight is over!");
    if (player.isAlive()) {
      System.out.println("You have won!");
      System.out.println("You have gained:");
      System.out.println(loot);
      // Apply loot
    } else {
      System.out.println("You have died!");
    }
  }

  private void printAttack(Combatant attacker, Combatant target, int damage) {
    var attackerColour = isPlayer(attacker) ? FMT.GREEN_BOLD : FMT.MAGENTA_BOLD;
    var targetColour = isPlayer(target) ? FMT.GREEN_BOLD : FMT.MAGENTA_BOLD;
    System.out.printf(
        "- %s%s%s is attacked by %s%s%s, taking %s%d%s damage%n (%s%d%s health remaining)%n",
        targetColour,
        target.getName(),
        FMT.RESET,
        attackerColour,
        attacker.getName(),
        FMT.RESET,
        FMT.RED,
        damage,
        FMT.RESET,
        FMT.RED,
        target.getHealth(),
        FMT.RESET);
  }

  private void evaluate(List<Combatant> combatants) {
    for (var combatant : combatants) {
      if (combatant.isAlive()) {
        continue;
      }
      var droppedLoot = combatant.getReward();
      this.loot.addAll(droppedLoot);
      printDeath(combatant, droppedLoot);
    }
  }

  private void printDeath(Combatant combatant, List<Reward> loot) {
    var colour = isPlayer(combatant) ? FMT.GREEN_BOLD : FMT.MAGENTA_BOLD;
    System.out.printf(
        "- %s%s%s has died, dropping %s%n", colour, combatant.getName(), FMT.RESET, loot);
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
}
