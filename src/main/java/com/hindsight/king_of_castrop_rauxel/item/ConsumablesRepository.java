package com.hindsight.king_of_castrop_rauxel.item;

import com.hindsight.king_of_castrop_rauxel.location.Shop;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsumablesRepository extends CrudRepository<Consumable, Long> {
  List<Consumable> findBySellerType(Shop.Type type);
  List<Consumable> findBySellerTypeAndTier(Shop.Type type, int tier);
}
