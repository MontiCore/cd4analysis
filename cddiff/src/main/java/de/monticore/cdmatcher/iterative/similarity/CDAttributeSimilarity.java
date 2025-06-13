package de.monticore.cdmatcher.iterative.similarity;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdmatcher.CDSimilarity;

public class CDAttributeSimilarity implements CDSimilarity<ASTCDAttribute> {
  @Override
  public Double computeWeight(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    if (srcElem.getName().equals(tgtElem.getName())) {
      return 1.0;
    }
    return 0.0;
  }
}
