/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.mctype;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

public class MCTypeMatchingStrategy {
  
  private final String underspecifiedTypeName;
  private BooleanMatchingStrategy<ASTCDType> typeMatcher;
  
  public MCTypeMatchingStrategy(String underspecifiedTypeName,
      BooleanMatchingStrategy<ASTCDType> typeMatcher) {
    this.underspecifiedTypeName = underspecifiedTypeName;
    this.typeMatcher = typeMatcher;
  }
  
  /**
   * Two types are matched if one of the following holds:
   * <ol>
   * <li>Both are no CDType and have exactly the same name</li>
   * <li>Both are CDTypes and the concrete type is an incarnation of the reference type
   * (note: any CDType is considered an incarnation if the reference type is underspecified!)
   * </li>
   * </ol>
   *
   * @param conType the concrete type
   * @param refType the reference type
   * @return true if the types are matched, false otherwise
   */
  public boolean isMatched(ASTMCType conType, ASTMCType refType) {
    if (MCTypeUtil.isUnderspecified(underspecifiedTypeName, refType)) {
      if (MCTypeUtil.isUnderspecified(underspecifiedTypeName, conType)) {
        Log.error("The underspecified placeholder type is not allowed as a concrete type.");
        return false;
      }
      // every type is allowed if the reference type is underspecified
      return true;
    }
    if (conType.getDefiningSymbol().isPresent() && refType.getDefiningSymbol().isPresent()) {
      ISymbol conTypeSymbol = conType.getDefiningSymbol().get();
      ISymbol refTypeSymbol = refType.getDefiningSymbol().get();
      if (conTypeSymbol instanceof CDTypeSymbol && refTypeSymbol instanceof CDTypeSymbol) {
        CDTypeSymbol conCDType = (CDTypeSymbol) conTypeSymbol;
        CDTypeSymbol refCDType = (CDTypeSymbol) refTypeSymbol;
        if (conCDType.isPresentAstNode() && refCDType.isPresentAstNode()) {
          // the concrete type is an incarnation of the reference type
          return typeMatcher.isMatched(conCDType.getAstNode(), refCDType.getAstNode());
        }
        else {
          Log.warn("The concrete type or the reference type does not have an AST node. "
              + "Incarnation mapping is only defined for CDTypeSymbol with AST nodes.");
        }
      }
      else if (conTypeSymbol.getFullName().equals(refTypeSymbol.getFullName())) {
        /*
         * ONLY if the types are NO CDTypeSymbols, we allow the same types to match!
         * CDTypes may not be a match even if their name is exactly the same, as the reference
         * type not exist in the concrete CD!
         */
        return true;
      }
    }
    /*
     * we need this as fallback for all types that are not CDTypes,  e.g., primitives, String etc.
     */
    return conType.deepEquals(refType);
  }
  
  public void setTypeMatcher(BooleanMatchingStrategy<ASTCDType> typeMatcher) {
    this.typeMatcher = typeMatcher;
  }
  
}
