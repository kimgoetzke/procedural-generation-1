package com.hindsight.king_of_castrop_rauxel;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplicationTests {

  @Test
  void contextLoads() {
    assertThat(SpringApplication.run(Application.class)).isNotNull();
  }
}
