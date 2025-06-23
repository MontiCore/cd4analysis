/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.similarity;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDAssociationTOP;
import de.monticore.cdmatcher.iterative.MutablePair;

import java.util.ArrayList;
import java.util.List;

public class CDAssocEmbeddingSimilarity extends CDEmbeddingSimilarity<ASTCDAssociation> {
  
  @Override
  public Double computeWeight(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    // MutablePair<weight, value> in order: nameSimilarity, leftNameSimilarity, rightNameSimilarity, typeSimilarity, directionSimilarity
    List<MutablePair<Double, Double>> weightsValues = new ArrayList<>(List.of(new MutablePair<>(0.4,
        -1.0), new MutablePair<>(0.05, -1.0), new MutablePair<>(0.05, -1.0), new MutablePair<>(0.3,
            -1.0), new MutablePair<>(0.2, -1.0)));
    
    if (srcElem.isPresentName() && tgtElem.isPresentName()) {
      weightsValues.get(0).setB(matchNameWithEmbedding(srcElem, tgtElem,
          ASTCDAssociationTOP::getName));
    }
    if (srcElem.getLeft().isPresentCDRole() && tgtElem.getLeft().isPresentCDRole()) {
      weightsValues.get(1).setB(matchNameWithEmbedding(srcElem, tgtElem, assoc -> assoc.getLeft()
          .getName()));
    }
    if (srcElem.getRight().isPresentCDRole() && tgtElem.getRight().isPresentCDRole()) {
      weightsValues.get(2).setB(matchNameWithEmbedding(srcElem, tgtElem, assoc -> assoc.getRight()
          .getName()));
    }
    weightsValues.get(3).setB(srcElem.getCDAssocDir().getClass().equals(tgtElem.getCDAssocDir()
        .getClass()) ? 1.0 : 0.0); //ASTCDAssocDir does not implement equals
    weightsValues.get(4).setB(srcElem.getCDAssocType().getClass().equals(tgtElem.getCDAssocType()
        .getClass()) ? 1.0 : 0.0); //ASTCDAssocType does not implement equals
    
    // preserves the relative weights even when some values cannot be calculated because names are not present
    MutablePair<Double, Double> weightsValuesSum = weightsValues.stream().collect(
        () -> new MutablePair<>(0.0, 0.0), this::mergePair, this::mergePair);
    
    return weightsValuesSum.getA() > 0 ? weightsValuesSum.getB() / weightsValuesSum.getA() : 0.0;
  }
  
  private void mergePair(MutablePair<Double, Double> first, MutablePair<Double, Double> second) {
    if (second.getB() >= 0.0) {
      first.setB(first.getB() < 0.0 ? second.getB() : first.getB() + second.getB());
      first.setA(first.getB() < 0.0 ? second.getA() : first.getA() + second.getA());
    }
  }
  
}
