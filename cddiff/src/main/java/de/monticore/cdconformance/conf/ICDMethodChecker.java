package de.monticore.cdconformance.conf;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;

public interface ICDMethodChecker
    extends MatchingStrategy<ASTCDMethod>, ConformanceStrategy<ASTCDMethod> {
  @Override
  default boolean checkConformance(ASTCDMethod concrete) {
    return getMatchedElements(concrete).stream().allMatch(ref -> checkConformance(concrete, ref));
  }

  boolean checkConformance(ASTCDMethod concrete, ASTCDMethod ref);

  ASTCDType getReferenceType();

  void setReferenceType(ASTCDType refType);

  ASTCDType getConcreteType();

  void setConcreteType(ASTCDType conType);
}
