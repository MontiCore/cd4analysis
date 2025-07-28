/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.mctype;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public interface MCTypeMatchingStrategy extends BooleanMatchingStrategy<ASTMCType> {
  
  /**
   * Returns if the given concrete type is an incarnation of the given reference type.
   *
   * @param conType the concrete type
   * @param refType the reference type
   * @return true if the types are matched, false otherwise
   */
  boolean isMatched(ASTMCType conType, ASTMCType refType);
  
  default void setCDTypeMatcher(BooleanMatchingStrategy<ASTCDType> cdTypeMatcher) {
    // Can be overridden if implementation needs access to the matching of CDTypes
  }
}
