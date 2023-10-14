package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Slf4j
public class FileReader {

  static final String BASE_FOLDER = "events";
  static final String SINGLE_STEP_FOLDER = "single-step";
  private static final String MULTI_STEP_FOLDER = "multi-step";
  private static final String REACH_FOLDER = "reach";

  @Getter public String fileSeparator;
  private Map<Event.Type, List<String>> eventFilePaths;

  public FileReader() {
    fileSeparator = CliComponent.getFileSeparator();
    loadEventFilesMap();
  }

  public String getRandomEventPath(Event.Type type, Random random) {
    var paths = eventFilePaths.get(type);
    var randomIndex = random.nextInt(0, paths.size());
    return paths.get(randomIndex);
  }

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  private void loadEventFilesMap() {
    eventFilePaths = new EnumMap<>(Event.Type.class);
    for (var t : Event.Type.values()) {
      var subFolder =
          switch (t) {
            case REACH -> REACH_FOLDER + fileSeparator;
            default -> MULTI_STEP_FOLDER + fileSeparator;
          };
      var folder = BASE_FOLDER + fileSeparator + subFolder;
      var result = getAllFileNamesInside(folder);
      eventFilePaths.put(t, result);
    }
    log.info("Loaded event files:");
    for (var e : eventFilePaths.entrySet()) {
      log.info("> Type: " + e.getKey());
      e.getValue().forEach(x -> log.info("  - " + x));
    }
  }

  @SuppressWarnings("CallToPrintStackTrace")
  private List<String> getAllFileNamesInside(String folder) {
    if (Boolean.TRUE.equals(CliComponent.getIsRunningAsJar())) {
      return readFromJar(folder);
    } else {
      try {
        return readFromResourceFolder(folder);
      } catch (URISyntaxException e) {
        e.printStackTrace();
        throw new IllegalStateException("Couldn't read files from resource folder", e);
      }
    }
  }

  @SuppressWarnings("CallToPrintStackTrace")
  private static List<String> readFromJar(String folder) {
    try {
      var resolver = new PathMatchingResourcePatternResolver();
      var resources = resolver.getResources("classpath*:%s*.yml".formatted(folder));
      return Arrays.stream(resources)
        .filter(resource -> resource != null && resource.getFilename() != null)
        .map(resource -> folder.concat(resource.getFilename()))
        .toList();
    } catch (IOException e) {
      e.printStackTrace();
    }
    throw new IllegalArgumentException("Resource folder '%s' not found".formatted(folder));
  }

  @SuppressWarnings("CallToPrintStackTrace")
  private List<String> readFromResourceFolder(String folder) throws URISyntaxException {
    var resourceUrl = getClass().getClassLoader().getResource(folder);
    if (resourceUrl != null) {
      var startPath = Paths.get(resourceUrl.toURI());
      try (var stream = Files.walk(startPath)) {
        return stream
            .filter(Files::isRegularFile)
            .map(a -> folder.concat(a.getFileName().toString()))
            .toList();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    throw new IllegalArgumentException("Folder '%s' not found in JAR".formatted(folder));
  }
}
