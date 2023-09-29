package com.hindsight.king_of_castrop_rauxel.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileProcessor {

  private final String fileExtension;
  private final String folder;

  public FileProcessor(String folder, String fileExtension) {
    this.folder = folder;
    this.fileExtension = fileExtension;
  }

  List<String> readWordsFromFile(String fileName) {
    return readLinesFromFile(fileName);
  }

  List<String> readLinesFromFile(String fileName) {
    var inputStream =
        FileProcessor.class.getClassLoader().getResourceAsStream(folder + fileName + fileExtension);
    if (inputStream != null) {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        return reader.lines().map(String::trim).toList();
      } catch (IOException e) {
        log.warn("File '{}' not found", fileName);
      }
    }
    return new ArrayList<>();
  }

  String getRandomWord(List<String> lines, Random random) {
    return getRandomLine(lines, random);
  }

  String getRandomLine(List<String> lines, Random random) {
    int randomIndex = random.nextInt(lines.size());
    return lines.get(randomIndex);
  }
}
