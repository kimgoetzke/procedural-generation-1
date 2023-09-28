package com.hindsight.king_of_castrop_rauxel;

import com.hindsight.king_of_castrop_rauxel.cli.NewCliGame;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class Application {

  public static void main(String[] args) {
    var context = SpringApplication.run(Application.class, args);
    var newGame = context.getBean(NewCliGame.class);
    newGame.play();
  }
}
