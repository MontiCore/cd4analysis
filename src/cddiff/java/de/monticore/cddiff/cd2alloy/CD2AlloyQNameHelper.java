package de.monticore.cddiff.cd2alloy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CD2AlloyQNameHelper {

  /**
   * This helper functions processes parts of a name such that they can be used in Alloy
   *
   * @return The processed name
   */
  public static String partHandler(List<String> parts, boolean toRoleName) {
    StringBuilder completeName = new StringBuilder();

    // Process to role name only
    if (toRoleName) {
      return parts.get(parts.size()-1).toLowerCase();
    }

    // Combine all parts using "_" as separator instead of "."
    for (String part : parts) {
      completeName.append(part).append("_");
    }
    // Remove last "_"
    completeName = new StringBuilder(completeName.substring(0, completeName.length() - 1));


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
