package de.monticore.cdconformance.inc.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;

/**
 * Specific {@link MatchingStrategy} interface for CD attributes. A strategy for attributes always
 * needs to know a reference type against whose attributes it matches the concrete attributes.
 */
public interface CDAttributeMatchingStrategy extends MatchingStrategy<ASTCDAttribute> {
  void setReferenceType(ASTCDType refType);
}
