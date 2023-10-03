package com.hindsight.king_of_castrop_rauxel.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TxtReader {

  private static final String FILE_EXTENSION = ".txt";
  private final String folder;

  public TxtReader(String folder) {
    this.folder = folder;
  }

  List<String> read(String fileName) {
    var inputStream =
        this.getClass().getClassLoader().getResourceAsStream(folder + fileName + FILE_EXTENSION);
    if (inputStream != null) {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        return reader.lines().map(String::trim).toList();
      } catch (IOException e) {
        log.warn("File '{}' not found", fileName);
      }
    }
    return new ArrayList<>();
  }

  String getRandom(List<String> lines, Random random) {
    int randomIndex = random.nextInt(lines.size());
    return lines.get(randomIndex);
  }
}
