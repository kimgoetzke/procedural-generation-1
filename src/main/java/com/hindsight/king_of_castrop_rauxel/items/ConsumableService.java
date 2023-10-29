package com.hindsight.king_of_castrop_rauxel.items;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ConsumableService {

  private final ConsumablesRepository consumablesRepository;

  @Getter private final List<Consumable> consumables;

  public ConsumableService(ConsumablesRepository consumablesRepository) {
    this.consumablesRepository = consumablesRepository;
    this.consumables = getAllConsumables();
    log.info("Loaded {} consumables from database", consumables.size());
  }

  public List<Consumable> getAllConsumables() {
    return (List<Consumable>) consumablesRepository.findAll();
  }
}
