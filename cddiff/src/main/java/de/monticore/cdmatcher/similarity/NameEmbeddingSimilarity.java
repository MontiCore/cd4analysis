/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.similarity;

import java.util.function.Function;

public class NameEmbeddingSimilarity<T> extends CDEmbeddingSimilarity<T> {

  Function<T, String> nameExtractor;

  public NameEmbeddingSimilarity(Function<T, String> nameExtractor) {
    this.nameExtractor = nameExtractor;
  }

  @Override
  public Double computeWeight(T srcElem, T tgtElem) {
    return matchNameWithEmbedding(srcElem, tgtElem, nameExtractor);
  }

}
