/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdconformance.inc.CDIncarnationMapping;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

/**
 * Conformance strategy for CD attributes. An attribute conforms to the reference attribute if the
 * type conforms to the reference type.<br>
 * See {@link de.monticore.cdconformance.inc.mctype.MCTypeMatchingStrategy}.
 */
public class BasicAttributeConfStrategy extends CDAttributeChecker {
  
  public BasicAttributeConfStrategy(CDIncarnationMapping incMapping) {
    super(incMapping);
  }
  
  @Override
  public boolean checkConformance(ASTCDAttribute concrete, ASTCDAttribute ref) {
    ASTMCType conType = concrete.getMCType();
    ASTMCType refType = ref.getMCType();
    if (incMapping.isIncarnation(concrete.getSymbol(), conType, refType)) {
      return true;
    }
    else {
      // For precise error logs, we check if the concrete type an incarnation of ref type
      // (ignoring bindings)
      if (incMapping.isIncarnation(conType, refType)) {
        Log.error("The incarnation '" + conType.printType() + "' of type '" + refType.printType()
            + "' is not allowed in this scope", conType.get_SourcePositionStart());
      }
      else {
        Log.error("The type '" + conType.printType() + "' is no incarnation of type '" + refType
            .printType() + "'", conType.get_SourcePositionStart());
      }
      return false;
    }
  }
  
}
