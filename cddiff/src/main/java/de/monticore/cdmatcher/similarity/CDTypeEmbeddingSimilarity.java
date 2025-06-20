package de.monticore.cdmatcher.similarity;

import de.monticore.cdbasis._ast.ASTCDType;

public class CDTypeEmbeddingSimilarity extends CDEmbeddingSimilarity<ASTCDType> {


  @Override
  public Double computeWeight(ASTCDType srcElem, ASTCDType tgtElem) {
    return matchNameWithEmbedding(
      srcElem,
      tgtElem,
      type -> type.getSymbol().getInternalQualifiedName()
    );
  }
}
