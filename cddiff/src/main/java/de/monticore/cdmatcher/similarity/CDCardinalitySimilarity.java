package de.monticore.cdmatcher.similarity;

import de.monticore.cdassociation._ast.ASTCDCardOther;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdmatcher.CDSimilarity;

import static com.google.common.math.DoubleMath.mean;

public class CDCardinalitySimilarity implements CDSimilarity<ASTCDCardinality> {

  @Override
  public Double computeWeight(ASTCDCardinality srcElem, ASTCDCardinality tgtElem) {
    if(!(srcElem instanceof ASTCDCardOther) && !(tgtElem instanceof ASTCDCardOther)) {
      // special cardinalities are only similar with themselves
      if(srcElem.getLowerBound() == tgtElem.getLowerBound()
          && srcElem.getUpperBound() == tgtElem.getUpperBound()) {
        return 1.0;
      } else {
        return 0.0;
      }
    } else {
      double lowerScore;
      double upperScore;

      if(srcElem.getLowerBound() == tgtElem.getLowerBound()) {
        lowerScore = 1.0;
      }
      // 0 is a special bound, it makes a big difference an element is optional or mandatory
      else if(srcElem.getLowerBound() == 0 || tgtElem.getLowerBound() == 0) {
        lowerScore = 0.0;
      } else {
        // consider the relative difference between the bounds, when the bounds are large a specific difference is less important than with small bounds
        // a difference of more than 20% results in a score of 0, falling linearly
        lowerScore = 1.0 - 5 * ((Math.abs(srcElem.getLowerBound() - tgtElem.getLowerBound()) /
            (double)Math.max(srcElem.getLowerBound(), tgtElem.getLowerBound())));
        lowerScore = Math.max(0.0, lowerScore);
      }

      if(srcElem.getUpperBound() == tgtElem.getUpperBound()) {
        upperScore = 1.0;
      } else if(srcElem.getUpperBound() == 0 || tgtElem.getUpperBound() == 0) {
        upperScore = 0.0;
      } else {
        upperScore = 1.0 - 5 * ((Math.abs(srcElem.getUpperBound() - tgtElem.getUpperBound()) /
            (double)Math.max(srcElem.getUpperBound(), tgtElem.getUpperBound())));
        upperScore = Math.max(0.0, upperScore);
      }

      return mean(lowerScore, upperScore);
    }
  }
}
