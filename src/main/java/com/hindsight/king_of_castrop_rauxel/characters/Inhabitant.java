package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(exclude = {"id", "firstName", "lastName", "stringGenerator", "home"})
@EqualsAndHashCode
public class Inhabitant implements Npc {

  private final StringGenerator stringGenerator;

  @Getter private String id;
  @Getter private String firstName;
  @Getter private String lastName;
  @Getter private String fullName;
  @Getter private PointOfInterest home;

  public Inhabitant(StringGenerator stringGenerator) {
    this.stringGenerator = stringGenerator;
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
    firstName = stringGenerator.npcFirstNameFrom(Inhabitant.class);
    lastName = stringGenerator.npcLastNameFrom(Inhabitant.class);
    fullName = firstName + " " + lastName;
  }

  @Override
  public void logResult() {
    log.debug("Generated: {}", this);
  }
}
