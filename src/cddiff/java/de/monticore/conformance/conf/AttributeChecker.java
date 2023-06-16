package de.monticore.conformance.conf;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.matcher.MatchingStrategy;

public interface AttributeChecker
    extends MatchingStrategy<ASTCDAttribute>, ConformanceStrategy<ASTCDAttribute> {
  @Override
  default boolean checkConformance(ASTCDAttribute concrete) {
    return getMatchedElements(concrete).stream()
        .allMatch(ref -> ref.getMCType().deepEquals(concrete.getMCType()));
  }

  ASTCDType getReferenceType();

  void setReferenceType(ASTCDType refType);

  ASTCDType getConcreteType();

  void setConcreteType(ASTCDType conType);
}
