package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.action.DialogueAction;
import com.hindsight.king_of_castrop_rauxel.event.*;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

@Slf4j
public class YamlReader {

  public EventDto read(String fileName) {
    var inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
    var options = new LoaderOptions();

    // Skip unknown parameters when parsing to a Java object
    var representer = new Representer(new DumperOptions());
    representer.getPropertyUtils().setSkipMissingProperties(true);

    // Set custom tags in the Yaml file that ensure parsing to the correct class
    var constructor = new Constructor(EventDto.class, options);
    constructor.addTypeDescription(new TypeDescription(Dialogue.class, "!dialogue"));
    constructor.addTypeDescription(new TypeDescription(DialogueAction.class, "!action"));

    var yaml = new Yaml(constructor, representer);
    var data = (EventDto) yaml.load(inputStream);
    if (data.eventDetails == null) {
      return new EventDto(new EventDetails(), data.participantData);
    }
    return data;
  }
}
