package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.utils.BasicStringGenerator;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@ToString(exclude = {"firstName", "lastName"})
@EqualsAndHashCode
public class Inhabitant implements Npc {

  private String id;
  private String firstName;
  private String lastName;
  private String fullName;

  public Inhabitant() {
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
    firstName = BasicStringGenerator.firstNameFrom(Inhabitant.class);
    lastName = BasicStringGenerator.lastNameFrom(Inhabitant.class);
    fullName = firstName + " " + lastName;
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
