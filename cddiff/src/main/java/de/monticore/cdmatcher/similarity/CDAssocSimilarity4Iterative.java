/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.similarity;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdmatcher.CDSimilarity;
import de.monticore.cdmatcher.iterative.MutablePair;

import java.util.ArrayList;
import java.util.List;

public class CDAssocSimilarity4Iterative implements CDSimilarity<ASTCDAssociation> {

  @Override
  public Double computeWeight(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    // MutablePair<weight, value> in order: nameSimilarity, leftNameSimilarity, rightNameSimilarity, leftCardinalitySimilarity, rightCardinalitySimilarity, typeSimilarity, directionSimilarity
    List<MutablePair<Double, Double>> weightsValues = new ArrayList<>(List.of(new MutablePair<>(0.3,
      -1.0), new MutablePair<>(0.05, -1.0), new MutablePair<>(0.05, -1.0), new MutablePair<>(0.1, -1.0), new MutablePair<>(0.1, -1.0), new MutablePair<>(0.2,
      -1.0), new MutablePair<>(0.2, -1.0)));

    // association name
    weightsValues.get(0).setB(srcElem.isPresentName() && tgtElem.isPresentName() && srcElem.getName()
        .equals(tgtElem.getName()) ? 1.0 : 0.0);

    // role names
    weightsValues.get(1).setB(srcElem.getLeft().getName().equals(tgtElem.getLeft().getName())
        ? 1.0 : 0.0);
    weightsValues.get(2).setB(srcElem.getRight().getName().equals(tgtElem.getRight().getName())
        ? 1.0 : 0.0);

    // cardinalities
    CDCardinalitySimilarity cardinalitySimilarity = new CDCardinalitySimilarity();
    if(srcElem.getLeft().isPresentCDCardinality() && tgtElem.getLeft().isPresentCDCardinality()) {
      weightsValues.get(3).setB(cardinalitySimilarity.computeWeight(srcElem.getLeft().getCDCardinality(), tgtElem.getLeft().getCDCardinality()));
    }
    if(srcElem.getRight().isPresentCDCardinality() && tgtElem.getRight().isPresentCDCardinality()) {
      weightsValues.get(4).setB(cardinalitySimilarity.computeWeight(srcElem.getRight().getCDCardinality(), tgtElem.getRight().getCDCardinality()));
    }

    // direction and type

    weightsValues.get(5).setB(srcElem.getCDAssocDir().getClass().equals(tgtElem.getCDAssocDir()
        .getClass()) ? 1.0 : 0.0); //ASTCDAssocDir does not implement equals
    weightsValues.get(6).setB(srcElem.getCDAssocType().getClass().equals(tgtElem.getCDAssocType()
        .getClass()) ? 1.0 : 0.0); //ASTCDAssocType does not implement equals

    MutablePair<Double, Double> weightsValuesSum = weightsValues.stream().collect(
      () -> new MutablePair<>(0.0, 0.0), this::mergePair, this::mergePair);

    return weightsValuesSum.getA() > 0 ? weightsValuesSum.getB() / weightsValuesSum.getA() : 0.0;
  }

  private void mergePair(MutablePair<Double, Double> first, MutablePair<Double, Double> second) {
    if (second.getB() >= 0.0) {
      first.setB(first.getB() < 0.0 ? second.getB() * second.getA() : first.getB() + second.getB() * second.getA());
      first.setA(first.getB() < 0.0 ? second.getA() : first.getA() + second.getA());
    }
  }

}
