/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.similarity;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdmatcher.CDSimilarity;

public class CDAssocSimilarity4Iterative implements CDSimilarity<ASTCDAssociation> {
  
  @Override
  public Double computeWeight(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    double nameSimilarity = srcElem.isPresentName() && tgtElem.isPresentName() && srcElem.getName()
        .equals(tgtElem.getName()) ? 1.0 : 0.0;
    double leftNameSimilarity = srcElem.getLeft().getName().equals(tgtElem.getLeft().getName())
        ? 1.0 : 0.0;
    double rightNameSimilarity = srcElem.getRight().getName().equals(tgtElem.getRight().getName())
        ? 1.0 : 0.0;
    double directionSimilarity = srcElem.getCDAssocDir().getClass().equals(tgtElem.getCDAssocDir()
        .getClass()) ? 1.0 : 0.0; //ASTCDAssocDir does not implement equals
    double typeSimilarity = srcElem.getCDAssocType().getClass().equals(tgtElem.getCDAssocType()
        .getClass()) ? 1.0 : 0.0; //ASTCDAssocType does not implement equals
    
    return nameSimilarity * 0.4 + leftNameSimilarity * 0.05 + rightNameSimilarity * 0.05
        + typeSimilarity * 0.3 + directionSimilarity * 0.2;
    
  }
  
}
