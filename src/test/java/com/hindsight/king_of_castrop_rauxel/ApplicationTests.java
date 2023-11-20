package com.hindsight.king_of_castrop_rauxel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

  @Test
  void contextLoads() {
    assertThat(SpringApplication.run(Application.class)).isNotNull();
  }
}
