package com.hindsight.king_of_castrop_rauxel.settings;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SeedComponent {

  public static final long SEED = 123456789L;
}
