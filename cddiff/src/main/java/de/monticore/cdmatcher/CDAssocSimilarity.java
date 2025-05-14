package de.monticore.cdmatcher;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import org.antlr.v4.runtime.misc.Pair;

import java.util.Map;

public class CDAssocSimilarity implements CDSimilarity<ASTCDAssociation>{

  protected Map<Pair<ASTCDType, ASTCDType>, Double> typeSimilarityMap;

  public CDAssocSimilarity(Map<Pair<ASTCDType, ASTCDType>, Double> typeSimilarityMap){
    this.typeSimilarityMap = typeSimilarityMap;
  }

  @Override
  public Double computeWeight(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    // todo: implement
    assert false;
    return 0.0;
  }
}
