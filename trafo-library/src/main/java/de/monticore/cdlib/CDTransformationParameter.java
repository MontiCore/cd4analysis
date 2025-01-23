package de.monticore.cdlib;

import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

public class CDTransformationParameter {
  
  private final List<String> value;
  
  public CDTransformationParameter(String value) {
    this.value = List.of(value);
  }
  
  public CDTransformationParameter(List<String> value) {
    this.value = value;
  }
  
  public static CDTransformationParameter fromObject(Object obj) {
    if (obj instanceof List) {
      List<String> values =
          ((List<?>) obj).stream().map(Object::toString).collect(Collectors.toList());
      return new CDTransformationParameter(values);
    }
    else {
      return new CDTransformationParameter(obj.toString());
    }
  }
  
  public String asString() {
    if (this.value.size() != 1) {
      Log.error("0x4A520: Type string is not applicable for parameter value.");
      return "";
    }
    return this.value.get(0);
  }
  
  public List<String> asList() {
    return this.value;
  }
}
