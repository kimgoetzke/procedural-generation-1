package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.item.Consumable;
import com.hindsight.king_of_castrop_rauxel.location.Shop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsvReader {

  private static final String FILE_EXTENSION = ".csv";
  private final String folder;

  public CsvReader(String folder) {
    this.folder = folder;
  }

  public List<Consumable> readConsumables() {
    var fileName = Consumable.class.getSimpleName().toLowerCase();
    var uri = folder + fileName + FILE_EXTENSION;
    var inputStream = getClass().getClassLoader().getResourceAsStream(uri);
    if (inputStream != null) {
      try (var reader = new BufferedReader(new InputStreamReader(inputStream))) {
        return reader.lines().skip(1).map(CsvReader::toConsumable).toList();
      } catch (IOException e) {
        log.error("File '{}' exists but there was an error reading it", uri, e);
      }
    }
    log.warn("File '{}' does not exist", uri);
    return new ArrayList<>();
  }

  private static Consumable toConsumable(String line) {
    var data = line.split(",");
    log.debug("Creating consumable from " + Arrays.toString(data));
    return Consumable.builder()
        .id(Long.parseLong(data[0]))
        .name(data[1])
        .tier(Integer.parseInt(data[2]))
        .basePrice(Integer.parseInt(data[3]))
        .sellerType(Shop.Type.valueOf(data[4]))
        .effectHealth(Integer.parseInt(data[5]))
        .effectMaxHealth(Integer.parseInt(data[6]))
        .build();
  }
}
