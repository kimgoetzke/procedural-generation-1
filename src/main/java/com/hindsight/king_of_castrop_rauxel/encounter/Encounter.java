package com.hindsight.king_of_castrop_rauxel.encounter;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;
import static com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver.*;
import static java.lang.System.out;

import com.hindsight.king_of_castrop_rauxel.character.Combatant;
import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver;
import com.hindsight.king_of_castrop_rauxel.encounter.web.EncounterSummaryDto;
import com.hindsight.king_of_castrop_rauxel.event.DefeatEvent;
import com.hindsight.king_of_castrop_rauxel.event.Loot;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.Getter;

public class Encounter {

  @Getter private final Loot loot = new Loot();
  @Getter private final EncounterSummaryDto summaryData = new EncounterSummaryDto();
  @Getter private final List<Combatant> attackers = new ArrayList<>();
  @Getter private final List<Combatant> defenders = new ArrayList<>();
  private final Random random = new Random();
  private final List<Combatant> initialAllies;
  private final List<Combatant> initialEnemies;
  private final long delayInMs;
  private final Environment environment;
  private Player player;
  private boolean isOver;
  private boolean isAttacker;

  public Encounter(List<Combatant> allies, List<Combatant> enemies, AppProperties appProperties) {
    this.delayInMs = appProperties.getGameProperties().delayInMs();
    this.initialAllies = allies;
    this.initialEnemies = enemies;
    this.environment = appProperties.getEnvironment();
  }

  public void execute(Player player, boolean isAttacker) {
    this.player = player;
    this.isAttacker = isAttacker;
    initialise();
    complete();
    recordOrPrintWrapUp();
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
    printOrRecordKickOff();
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
      delayIfCli();
      getTarget(attacker, defendingGroup);
      if (isOver) {
        return;
      }
      var damage = attacker.attack();
      recordOrPrintAttack(attacker, attacker.getTarget(), damage);
      printAttack(attacker, attacker.getTarget(), damage);
      evaluateAttack(attacker.getTarget(), defendingGroup);
    }
  }

  private void recordOrPrintAttack(Combatant attacker, Combatant target, int damage) {
    switch (environment) {
      case CLI -> printAttack(attacker, target, damage);
      case WEB -> recordAttack(attacker, target, damage);
    }
  }

  private void recordAttack(Combatant attacker, Combatant target, int damage) {
    summaryData.addRecord(
        attacker.getName(), damage, target.getName(), target.getHealth(), target.isAlive());
  }

  private void delayIfCli() {
    if (environment.equals(EnvironmentResolver.Environment.WEB)) {
      return;
    }
    try {
      Thread.sleep(delayInMs);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void evaluateAttack(Combatant target, List<Combatant> defendingGroup) {
    if (target.isAlive()) {
      return;
    }
    var droppedLoot = lootTarget(target);
    printDeathIfCli(target, droppedLoot);
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

  private void printOrRecordKickOff() {
    switch (environment) {
      case CLI -> printKickOff();
      case WEB -> recordKickOff();
    }
  }

  private void recordKickOff() {
    summaryData.setPlayerHadInitiative(isAttacker);
    summaryData.setAttackers(attackers.stream().map(Combatant::toDto).toList());
    summaryData.setDefenders(defenders.stream().map(Combatant::toDto).toList());
  }

  private void printKickOff() {
    out.printf("%nYou %s%n%n", isAttacker ? "have the initiative." : "are being surprised.");
    printCombatants(attackers, "Attacker(s)");
    printCombatants(defenders, "Defender(s)");
    out.printf("%nThe fight has started.%n%n");
    awaitEnterKeyPress();
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
        "- %s%s%s attacks %s%s%s  %s-%d%s -> %s%d%s HP%n",
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

  private void printDeathIfCli(Combatant combatant, Loot loot) {
    if (environment.equals(EnvironmentResolver.Environment.WEB)) {
      return;
    }
    var colour = isPlayer(combatant) ? FMT.GREEN_BOLD : FMT.MAGENTA_BOLD;
    out.printf(
        "- %s%s%s has died, dropping %s%n",
        colour, combatant.getName().toUpperCase(), FMT.RESET, loot);
  }

  private void recordOrPrintWrapUp() {
    switch (environment) {
      case CLI -> printWrapUp();
      case WEB -> recordWrapUp();
    }
  }

  private void recordWrapUp() {
    summaryData.setPlayerHasWon(player.isAlive());
    summaryData.setEnemiesDefeated(initialEnemies.stream().map(Combatant::toDto).toList());
  }

  private void printWrapUp() {
    out.printf("%nThe fight is over!%n%n");
    if (player.isAlive()) {
      out.print(bold("You have won!") + " You have defeated: ");
      printCombatants(initialEnemies);
      out.printf("You have gained: %s. You have %s HP left.%n", loot, health(player.getHealth()));
    } else {
      out.printf("%n%s Game over. %nThanks for playing!%n%n", bold("You have died!"));
      System.exit(0);
    }
  }
}
