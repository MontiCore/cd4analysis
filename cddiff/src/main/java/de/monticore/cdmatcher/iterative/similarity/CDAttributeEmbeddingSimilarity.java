/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.similarity;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDAttributeTOP;

public class CDAttributeEmbeddingSimilarity extends CDEmbeddingSimilarity<ASTCDAttribute> {
  
  @Override
  public Double computeWeight(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    return matchNameWithEmbedding(srcElem, tgtElem, ASTCDAttributeTOP::getName);
  }
  
}
