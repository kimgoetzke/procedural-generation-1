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
  List<Combatant> allies;
  List<Combatant> enemies;
  List<Reward> loot = new ArrayList<>();
  private boolean isOver = false;

  public Encounter(List<Combatant> allies, List<Combatant> enemies) {
    this.allies = allies;
    this.enemies = enemies;
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
      addAlliesTo(attackers);
      defenders.addAll(enemies);
    } else {
      attackers.addAll(enemies);
      defenders.add(player);
      addAlliesTo(defenders);
    }
    System.out.print("A fight has started. ");
    System.out.printf(
        "You %s%n", hasTheInitiative ? "have the initiative." : "are being surprised.");
  }

  private void addAlliesTo(ArrayList<Combatant> combatants) {
    if (allies != null) {
      combatants.addAll(allies);
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
      getTarget(attacker, defendingGroup);
      var damage = attacker.attack();
      printAttack(attacker, attacker.getTarget(), damage);
    }
    evaluateAttack(defendingGroup);
  }

  private void wrapUp() {
    System.out.println("The fight is over!");
    if (player.isAlive()) {
      System.out.println("You have won! You have gained:");
      System.out.println(loot);
      loot.forEach(reward -> reward.give(player));
    } else {
      System.out.printf("You have died!%nGame over. Thanks for playing!%n");
      System.exit(0);
    }
  }

  private void printAttack(Combatant attacker, Combatant target, int damage) {
    var attackerColour = isPlayer(attacker) ? FMT.GREEN_BOLD : FMT.MAGENTA_BOLD;
    var targetColour = isPlayer(target) ? FMT.GREEN_BOLD : FMT.MAGENTA_BOLD;
    if (isEnemy(attacker)) {
      System.out.printf(
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
    System.out.printf(
        "- %s%s%s attacks %s%s%s  %s+%d%s -> %s%d%s health%n",
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

  private void evaluateAttack(List<Combatant> combatants) {
    for (var combatant : combatants) {
      if (combatant.isAlive()) {
        continue;
      }
      var droppedLoot = combatant.getReward();
      loot.addAll(droppedLoot);
      printDeath(combatant, droppedLoot);
      combatants.remove(combatant);
    }
  }

  private void printDeath(Combatant combatant, List<Reward> loot) {
    var colour = isPlayer(combatant) ? FMT.GREEN_BOLD : FMT.MAGENTA_BOLD;
    System.out.printf(
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
    return enemies.contains(combatant);
  }
}
