package de.cd4analysis.cocos.permutations;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiFunction;

public class Permuter {
  
  public static <E, V> Set<E> permute(BiFunction<E, V, E> reducer, Collection<E> elements,
      Collection<V> values) {
    
    Set<E> permutations = new LinkedHashSet<>();
    for (E element : elements) {
      for (V value : values) {
        permutations.add(reducer.apply(element, value));
      }
    }
    return permutations;
  }
  
}
