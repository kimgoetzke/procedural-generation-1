package com.hindsight.king_of_castrop_rauxel.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TxtProcessor {

  private static final String FILE_EXTENSION = ".txt";
  private final String folder;

  public TxtProcessor(String folder) {
    this.folder = folder;
  }

  List<String> read(String fileName) {
    var uri = folder + fileName + FILE_EXTENSION;
    var inputStream = getClass().getClassLoader().getResourceAsStream(uri);
    if (inputStream != null) {
      try (var reader = new BufferedReader(new InputStreamReader(inputStream))) {
        return reader.lines().map(String::trim).toList();
      } catch (IOException e) {
        log.error("File '{}' exists but there was an error reading it", fileName, e);
        return new ArrayList<>();
      }
    }
    return new ArrayList<>();
  }

  String getRandom(List<String> lines, Random random) {
    int randomIndex = random.nextInt(lines.size());
    return lines.get(randomIndex);
  }
}
