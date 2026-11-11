/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.stereotype;

import java.util.Collections;
import java.util.Set;

public class BindingValue {
  
  private final String referenceName;
  private final Set<String> incarnationNames;
  
  public BindingValue(String referenceName, Set<String> incarnationNames) {
    this.referenceName = referenceName;
    this.incarnationNames = Collections.unmodifiableSet(incarnationNames);
  }
  
  public String getReferenceName() { return referenceName; }
  
  public Set<String> getIncarnationNames() { return incarnationNames; }
  
  public String print() {
    return referenceName + "=" + String.join(",", incarnationNames);
  }
  
  /**
   * Parses a string representation of an IncarnationBinding. The expected format is
   * "referenceName=incarnationName1,incarnationName2,...".
   *
   * @param bindingString
   * @return
   */
  public static BindingValue parseFromString(String bindingString) {
    String[] parts = bindingString.split("=");
    if (parts.length != 2) {
      throw new IllegalArgumentException("Invalid binding string: " + bindingString);
    }
    String referenceName = parts[0].trim();
    Set<String> incarnationNames = Set.of(parts[1].trim().split(","));
    return new BindingValue(referenceName, incarnationNames);
  }
  
}
