package de.monticore.cdmatcher.iterative.matching.cdtype;

import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdmatcher.CDSimilarity;
import de.monticore.cdmatcher.MultipleMatchingStrategy;

import java.util.HashSet;

public class MatchCDEnum extends MultipleMatchingStrategy<ASTCDEnum, ASTCDEnumConstant> {

  CDSimilarity<ASTCDEnum> enumNameSimilarity;
  CDSimilarity<ASTCDEnumConstant> enumConstantNameSimilarity;

  public MatchCDEnum(CDSimilarity<ASTCDEnum> enumNameSimilarity,
                     CDSimilarity<ASTCDEnumConstant> enumConstantNameSimilarity) {
    this.enumNameSimilarity = enumNameSimilarity;
    this.enumConstantNameSimilarity = enumConstantNameSimilarity;
  }


  @Override
  public double getScore(ASTCDEnum srcElem, ASTCDEnum tgtElem) {
    double nameScore = enumNameSimilarity.computeWeight(srcElem, tgtElem);
    double constantScore = getBestMatchingScore(srcElem, tgtElem, (elem) -> new HashSet<>(elem.getCDEnumConstantList()), enumConstantNameSimilarity::computeWeight);
    return nameScore * 0.25 + constantScore * 0.75;
  }
}
