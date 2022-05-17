package de.monticore.cd2alloy.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CD2AlloyQNameHelper {

  /**
   * This helper functions processes parts of a name such that they can be used in Alloy
   *
   * @return The processed name
   */
  public static String partHandler(List<String> parts, boolean lowercase) {
    StringBuilder completeName = new StringBuilder();

    // Combine all parts using "_" as separator instead of "."
    for (String part : parts) {
      completeName.append(part).append("_");
    }
    // Remove last "_"
    completeName = new StringBuilder(completeName.substring(0, completeName.length() - 1));

    // Process to lowercase only
    if (lowercase) {
      completeName = new StringBuilder(completeName.toString().toLowerCase());
    }

    return completeName.toString();
  }

  public static String processQName(String qname) {
    List<String> nameList = new ArrayList<>();
    Collections.addAll(nameList, qname.split("\\."));
    return partHandler(nameList, false);
  }

  public static String processQName2RoleName(String qname) {
    List<String> nameList = new ArrayList<>();
    Collections.addAll(nameList, qname.split("\\."));
    return partHandler(nameList, true);
  }

}
