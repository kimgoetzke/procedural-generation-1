package com.hindsight.king_of_castrop_rauxel.items;

import com.hindsight.king_of_castrop_rauxel.location.Shop;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsumablesRepository extends CrudRepository<Consumable, Long> {
  List<Consumable> findBySellerType(Shop.Type type);
}
