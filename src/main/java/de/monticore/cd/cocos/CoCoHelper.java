/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cocos;

import java.util.*;
import java.util.stream.Collectors;

public class CoCoHelper {
  public static <T> List<T> findDuplicates(Collection<T> list) {
    Set<T> uniques = new HashSet<>();

    return list.stream().filter(e -> !uniques.add(e)).collect(Collectors.toList());
  }

  public static <T, Inner> List<T> findDuplicatesBy(Collection<T> list, java.util.function.Function<T, Inner> function) {
    Set<Inner> existingElements = new HashSet<>();
    List<T> duplicates = new ArrayList<>();

    for (T elem : list) {
      if (existingElements.contains(function.apply(elem))) {
        duplicates.add(elem);
      }
      else {
        existingElements.add(function.apply(elem));
      }
    }

    return duplicates;
  }
}
