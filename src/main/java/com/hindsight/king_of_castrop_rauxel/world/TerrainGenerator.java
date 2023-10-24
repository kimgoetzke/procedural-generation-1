package com.hindsight.king_of_castrop_rauxel.world;

import com.hindsight.king_of_castrop_rauxel.utils.Generator;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TerrainGenerator implements Generator {

  @Override
  public void initialise(Random random) {
    log.info("Initialising terrain generator");
  }
}
