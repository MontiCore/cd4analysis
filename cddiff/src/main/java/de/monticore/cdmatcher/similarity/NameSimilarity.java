/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.similarity;

import de.monticore.cdmatcher.CDSimilarity;

import java.util.function.Function;

public class NameSimilarity<T> implements CDSimilarity<T> {

  Function<T, String> nameExtractor;

  public NameSimilarity(Function<T, String> nameExtractor) {
    this.nameExtractor = nameExtractor;
  }

  @Override
  public Double computeWeight(T srcElem, T tgtElem) {
    return nameExtractor.apply(srcElem).equals(nameExtractor.apply(tgtElem)) ? 1.0 : 0.0;
  }

}
