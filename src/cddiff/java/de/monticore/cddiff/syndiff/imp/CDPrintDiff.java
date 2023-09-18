package de.monticore.cddiff.syndiff.imp;

import de.monticore.cddiff.syndiff.interfaces.ICDPrintDiff;

import java.util.List;

// TODO: Write comments
public class CDPrintDiff implements ICDPrintDiff {

  protected static final String COLOR_DELETE = "\033[1;31m";

  protected static final String COLOR_ADD = "\033[1;32m";

  protected static final String COLOR_CHANGE = "\033[1;33m";

  protected static final String RESET = "\033[0m";

  @Override
  public String insertSpaceBetweenStrings(List<String> stringList) {
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

  @Override
  public String insertSpaceBetweenStringsAndGreen(List<String> stringList){
    StringBuilder output = new StringBuilder();

    for (String field : stringList) {
      if (!(field == null)) {
        output.append(COLOR_ADD).append(field).append(" ");
      }
    }
    if (!stringList.isEmpty()) {
      return output.substring(0, output.length() - 1);
    }
    return output.toString();
  }
  @Override
  public String insertSpaceBetweenStringsAndRed(List<String> stringList){
    StringBuilder output = new StringBuilder();

    for (String field : stringList) {
      if (!(field == null)) {
        output.append(COLOR_DELETE).append(field).append(" ");
      }
    }
    if (!stringList.isEmpty()) {
      return output.substring(0, output.length() - 1);
    }
    return output.toString();
  }

  static String getColorCode(CDNodeDiff<?,?> diff) {
    if (diff.getAction().isPresent()) {
      if (diff.getAction().get().equals(Actions.REMOVED)) {
        return COLOR_DELETE;
      } else if (diff.getAction().get().equals(Actions.ADDED)) {
        return COLOR_ADD;
      } else if (diff.getAction().get().equals(Actions.CHANGED)) {
        return COLOR_CHANGE;
      }
    }
    return "";
  }
}
