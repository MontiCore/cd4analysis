package de.monticore.cdconformance.conf;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.matching.booleanMatchingStrategy.ExternalCandidatesMatchingStrategy;

public interface CDAttributeChecker
    extends ExternalCandidatesMatchingStrategy<ASTCDAttribute>, ConformanceStrategy<ASTCDAttribute> {
  @Override
  default boolean checkConformance(ASTCDAttribute concrete) {
    return getMatchedElements(concrete).stream().allMatch(ref -> checkConformance(concrete, ref));
  }

  boolean checkConformance(ASTCDAttribute concrete, ASTCDAttribute ref);

  ASTCDType getReferenceType();

  void setReferenceType(ASTCDType refType);

  ASTCDType getConcreteType();

  void setConcreteType(ASTCDType conType);
}
