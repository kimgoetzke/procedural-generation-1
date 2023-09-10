package com.hindsight.king_of_castrop_rauxel;

import com.hindsight.king_of_castrop_rauxel.cli.NewGame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        var newGame = new NewGame();
        newGame.start();
    }

}
