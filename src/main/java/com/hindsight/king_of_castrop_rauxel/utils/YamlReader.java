package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.action.DialogueAction;
import com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver;
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

  public static final String DIALOGUE_TAG = "!dialogue";
  public static final String ACTION_TAG = "!action";
  private final EnvironmentResolver.Environment environment;

  public YamlReader(EnvironmentResolver.Environment environment) {
    this.environment = environment;
  }

  public EventDto read(String fileName) {
    var inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
    var options = new LoaderOptions();
    var representer = getRepresenter();
    var constructor = getConstructor(options);
    var yaml = new Yaml(constructor, representer);
    var data = (EventDto) yaml.load(inputStream);
    var eventDetails = data.eventDetails == null ? new EventDetails() : data.eventDetails;
    var defeatDetails = data.defeatDetails == null ? new DefeatEventDetails() : data.defeatDetails;
    return new EventDto(eventDetails, defeatDetails, data.participantData);
  }

  /** Skip unknown parameters when parsing to a Java object */
  public Representer getRepresenter() {
    var representer = new Representer(new DumperOptions());
    representer.getPropertyUtils().setSkipMissingProperties(true);
    return representer;
  }

  /** Set custom tags in the Yaml file that ensure parsing to the correct class */
  public Constructor getConstructor(LoaderOptions options) {
    var constructor = new YamlEventConstructor(EventDto.class, options, environment);
    constructor.addTypeDescription(new TypeDescription(Dialogue.class, DIALOGUE_TAG));
    constructor.addTypeDescription(new TypeDescription(DialogueAction.class, ACTION_TAG));
    return constructor;
  }
}
