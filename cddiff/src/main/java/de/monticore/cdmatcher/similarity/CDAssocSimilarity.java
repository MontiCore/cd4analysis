package de.monticore.cdmatcher.similarity;

import de.monticore.cdassociation._ast.ASTCDAssociation;

public class CDAssocSimilarity implements CDSimilarity<ASTCDAssociation> {
  @Override
  public Double computeWeight(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    double nameSimilarity = srcElem.isPresentName()
      && tgtElem.isPresentName() &&
      srcElem.getName().equals(tgtElem.getName()) ? 1.0 : 0.0;
    double directionSimilarity = srcElem.getCDAssocDir().equals(tgtElem.getCDAssocDir()) ? 1.0 : 0.0;
    double typeSimilarity = srcElem.getCDAssocType().equals(tgtElem.getCDAssocType()) ? 1.0 : 0.0;

    return nameSimilarity * 0.7
      + typeSimilarity * 0.2
      + directionSimilarity * 0.1;

  }
}
