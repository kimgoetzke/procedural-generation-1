package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@ToString(exclude = {"firstName", "lastName", "stringGenerator"})
@EqualsAndHashCode
public class Inhabitant implements Npc {

  private String id;
  private String firstName;
  private String lastName;
  private String fullName;

  private final StringGenerator stringGenerator;

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
  public String getFirstName() {
    return firstName;
  }

  @Override
  public String getLastName() {
    return lastName;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void generate() {
    generate(null);
  }

  @Override
  public void generate(String parentName) {
    id = UUID.randomUUID().toString();
    firstName = stringGenerator.npcFirstNameFrom(Inhabitant.class);
    lastName = stringGenerator.npcLastNameFrom(Inhabitant.class);
    fullName = firstName + " " + lastName;
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
