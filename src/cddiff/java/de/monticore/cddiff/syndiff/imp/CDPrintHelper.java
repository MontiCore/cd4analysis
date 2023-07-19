package de.monticore.cddiff.syndiff.imp;

import java.util.List;

public class CDPrintHelper {
  public String buildStrings(List<String> stringList) {
    StringBuilder output = new StringBuilder();

    for (String field : stringList) {
      if (!(field == null)) {
        output.append(field).append(" ");
      }
    }

    if (!stringList.isEmpty()) {
      return output.substring(0, output.length() - 1);
    }

    return output.toString();
  }
}
