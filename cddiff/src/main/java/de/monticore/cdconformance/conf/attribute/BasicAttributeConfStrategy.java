/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdconformance.inc.CDIncarnationMapping;
import de.monticore.cdconformance.inc.type.MCTypeMatcher;

/**
 * Conformance strategy for CD attributes. An attribute conforms to the reference attribute if the
 * type conforms to the reference type. See {@link MCTypeMatcher#isMCTypeMatched} for the definition
 * of type conformance.
 */
public class BasicAttributeConfStrategy extends CDAttributeChecker {
  
  private final MCTypeMatcher typeMatcher;
  
  public BasicAttributeConfStrategy(CDIncarnationMapping incMapping, MCTypeMatcher typeMatcher) {
    super(incMapping);
    this.typeMatcher = typeMatcher;
  }
  
  @Override
  public boolean checkConformance(ASTCDAttribute concrete, ASTCDAttribute ref) {
    return typeMatcher.isMCTypeMatched(concrete.getMCType(), ref.getMCType());
  }
  
}
