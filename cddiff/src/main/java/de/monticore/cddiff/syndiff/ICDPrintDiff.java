package de.monticore.cddiff.syndiff;

import java.util.List;

public interface ICDPrintDiff {
  String insertSpaceBetweenStrings(List<String> stringList);

  String insertSpaceBetweenStringsAndGreen(List<String> stringList);

  String insertSpaceBetweenStringsAndRed(List<String> stringList);

  String insertSpaceBetweenStringsAndPurple(List<String> stringList);
}
