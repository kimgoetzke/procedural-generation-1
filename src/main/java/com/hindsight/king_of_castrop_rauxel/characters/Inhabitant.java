package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import com.hindsight.king_of_castrop_rauxel.utils.EventGenerator;
import com.hindsight.king_of_castrop_rauxel.utils.NameGenerator;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(exclude = {"id", "firstName", "lastName", "nameGenerator", "eventGenerator", "home"})
@EqualsAndHashCode
public class Inhabitant implements Npc {

  private final NameGenerator nameGenerator;
  private final EventGenerator eventGenerator;

  @Getter private String id;
  @Getter private String firstName;
  @Getter private String lastName;
  @Getter private String fullName;
  @Getter private PointOfInterest home;
  @Getter private Event event;

  public Inhabitant(NameGenerator nameGenerator, EventGenerator eventGenerator) {
    this.nameGenerator = nameGenerator;
    this.eventGenerator = eventGenerator;
    generate();
    logResult();
  }

  @Override
  public String getName() {
    return firstName + " " + lastName;
  }

  @Override
  public void setHome(PointOfInterest home) {
    this.home = home;
    log.info("Set home of '{}' to: {}", this.fullName, home);
  }

  @Override
  public void generate() {
    id = UUID.randomUUID().toString();
    firstName = nameGenerator.npcFirstNameFrom(Inhabitant.class);
    lastName = nameGenerator.npcLastNameFrom(Inhabitant.class);
    fullName = firstName + " " + lastName;
    event = eventGenerator.multiStepDialogue(this);
  }

  @Override
  public void logResult() {
    log.debug("Generated: {}", this);
  }
}
