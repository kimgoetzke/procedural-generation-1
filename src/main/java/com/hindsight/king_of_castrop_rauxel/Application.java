package com.hindsight.king_of_castrop_rauxel;

import com.hindsight.king_of_castrop_rauxel.cli.CliGame;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.fusesource.jansi.AnsiConsole;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class Application {

  public static void main(String[] args) {
    var context = SpringApplication.run(Application.class, args);
    if (Boolean.TRUE.equals(AppProperties.getIsRunningAsJar())) {
      AnsiConsole.systemInstall();
    }
    context.getBean(CliGame.class).play();
  }
}
