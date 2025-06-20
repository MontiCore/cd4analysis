package de.monticore.cdmatcher.similarity;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDAttributeTOP;

public class CDAttributeEmbeddingSimilarity extends CDEmbeddingSimilarity<ASTCDAttribute> {

  @Override
  public Double computeWeight(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    return matchNameWithEmbedding(
      srcElem,
      tgtElem,
      ASTCDAttributeTOP::getName
    );
  }
}
