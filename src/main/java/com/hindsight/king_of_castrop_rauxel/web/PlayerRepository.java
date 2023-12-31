package com.hindsight.king_of_castrop_rauxel.web;

import com.hindsight.king_of_castrop_rauxel.web.dto.PlayerDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<PlayerDto, String> {

  PlayerDto findByName(String name);
}
