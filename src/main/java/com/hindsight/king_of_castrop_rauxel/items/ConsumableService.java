package com.hindsight.king_of_castrop_rauxel.items;

import com.hindsight.king_of_castrop_rauxel.location.Shop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import com.hindsight.king_of_castrop_rauxel.utils.FolderReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsumableService {

  private final ConsumablesRepository consumablesRepository;
  private final FolderReader folderReader;

  public ConsumableService(ConsumablesRepository consumablesRepository) {
    this.consumablesRepository = consumablesRepository;
    this.folderReader = new FolderReader();
    preLoadDataFromCsv();
    log.info("Loaded {} consumables from database", getAllConsumables().size());
  }

  // TODO: Move to TxtReader and then rename TxtReader
  private void preLoadDataFromCsv() {
    var uri = folderReader.getContentFolder() + "consumables.csv";
    var inputStream = getClass().getClassLoader().getResourceAsStream(uri);
    if (inputStream != null) {
      try (var reader = new BufferedReader(new InputStreamReader(inputStream))) {
        var items = reader.lines().skip(1).map(ConsumableService::createConsumable).toList();
        consumablesRepository.saveAll(items);
      } catch (IOException e) {
        log.error("File '{}' exists but there was an error reading it", uri, e);
      }
    }
  }

  private static Consumable createConsumable(String line) {
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

  public List<Consumable> getAllConsumables() {
    return (List<Consumable>) consumablesRepository.findAll();
  }

  public List<Consumable> getConsumablesByType(Shop.Type type) {
    return consumablesRepository.findBySellerType(type);
  }
}
