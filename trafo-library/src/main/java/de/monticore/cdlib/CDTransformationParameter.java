package de.monticore.cdlib;

import de.se_rwth.commons.logging.Log;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CDTransformationParameter<T> {
  private final T value;

  public CDTransformationParameter(T value) {
    this.value = value;
  }

  public static CDTransformationParameter<?> fromObject(Object obj) {
    if (obj instanceof List) {
      List<String> values = ((List<?>) obj)
        .stream().map(Object::toString).collect(Collectors.toList());
      return new CDTransformationParameter<>(values);
    } else {
      return new CDTransformationParameter<>(obj.toString());
    }
  }

  public String asString() {
    if (this.value instanceof String) {
      return (String) value;
    }
    Log.error("0x4A520: Type string is not applicable for parameter value.");
    return "";
  }

  public List<String> asList() {
    if (this.value instanceof List) {
      if ((((List<?>) this.value).isEmpty() || ((List<?>) this.value).get(0) instanceof String)) {
        return (List<String>) value;
      }
    }
    Log.error("0x4A521: Type list is not applicable for parameter value.");
    return Collections.emptyList();
  }
}
