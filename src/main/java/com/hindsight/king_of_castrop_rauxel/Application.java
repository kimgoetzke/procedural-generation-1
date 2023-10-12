package com.hindsight.king_of_castrop_rauxel;

import com.hindsight.king_of_castrop_rauxel.cli.CliGame;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import jline.TerminalFactory;
import lombok.extern.slf4j.Slf4j;
import org.fusesource.jansi.AnsiConsole;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;
import java.util.HashMap;

import static org.fusesource.jansi.Ansi.ansi;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class Application {

  public static void main(String[] args) {
    var context = SpringApplication.run(Application.class, args);
    AnsiConsole.systemInstall();
    var newGame = context.getBean(CliGame.class);
    System.out.print("123");
    System.out.print((char) 8);
    System.out.println(4);
    System.out.println("");
    newGame.play();
  }
}
