/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cocos;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CoCoHelper {
  public static <T> List<T> findDuplicates(Collection<T> list) {
    Set<T> uniques = new HashSet<>();

    return list.stream().filter(e -> !uniques.add(e)).collect(Collectors.toList());
  }
}
