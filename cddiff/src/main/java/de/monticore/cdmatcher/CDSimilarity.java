package de.monticore.cdmatcher;

import de.monticore.cdbasis._ast.ASTCDType;

public interface CDSimilarity<T> {
  public Double computeWeight(T srcElem, T tgtElem);
}
