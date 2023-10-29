package com.hindsight.king_of_castrop_rauxel.items;

import com.hindsight.king_of_castrop_rauxel.location.Shop;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsumableService {

  private final ConsumablesRepository consumablesRepository;

  public ConsumableService(ConsumablesRepository consumablesRepository) {
    this.consumablesRepository = consumablesRepository;
    log.info("Loaded {} consumables from database", getAllConsumables().size());
  }

  public List<Consumable> getAllConsumables() {
    return (List<Consumable>) consumablesRepository.findAll();
  }

  public List<Consumable> getConsumablesByType(Shop.Type type) {
    return consumablesRepository.findBySellerType(type);
  }
}
