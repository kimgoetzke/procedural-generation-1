package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.action.DialogueAction;
import com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;

public class YamlEventConstructor extends Constructor {

  private final EnvironmentResolver.Environment environment;

  public YamlEventConstructor(
      Class<?> clazz, LoaderOptions options, EnvironmentResolver.Environment environment) {
    super(clazz, options);
    this.environment = environment;
  }

  @Override
  protected Object constructObject(Node node) {
    Object obj = super.constructObject(node);
    if (obj instanceof DialogueAction dialogueAction) {
      dialogueAction.setEnvironment(environment);
    }
    return obj;
  }
}
