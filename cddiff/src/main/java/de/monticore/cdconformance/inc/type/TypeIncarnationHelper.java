package de.monticore.cdconformance.inc.type;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

// TODO This is a workaround because we do not have a "context" object like in the concretization tool yet
// TODO maybe even refactor conformance checker and concretization tool to share the same context object
public class TypeIncarnationHelper {

  private final String underspecifiedTypeName;
  private final MatchingStrategy<ASTCDType> typeMatcher;

  public TypeIncarnationHelper(String underspecifiedTypeName, MatchingStrategy<ASTCDType> typeMatcher) {
    this.underspecifiedTypeName = underspecifiedTypeName;
    this.typeMatcher = typeMatcher;
  }

  public boolean isVoidType(ASTMCType type) {
    return type.printType().equals("void");
  }

  public boolean isVoidType(ASTMCReturnType type) {
    return type.printType().equals("void");
  }

  public boolean isUnderspecified(ASTMCType type) {
    return type.printType().equals(underspecifiedTypeName);
  }

  /**
   * Checks if the return type is underspecified.<br>
   * Use this as {@link #isUnderspecified(ASTMCType)} throws an exception if the type is 'void'.
   */
  public boolean isUnderspecified(ASTMCReturnType type) {
    return type.printType().equals(underspecifiedTypeName);
  }

  /**
   * Two types are matched if one of the following holds:
   * - the concrete type has the exact same type as the reference type
   * - the concrete type is an incarnation of the reference type
   * - the reference type is underspecified
   *
   * @param conType the concrete type
   * @param refType the reference type
   * @return true if the types are matched, false otherwise
   */
  public boolean isMCTypeMatched(ASTMCType conType, ASTMCType refType) {
    if (isUnderspecified(refType)) {
      if (isUnderspecified(conType)) {
        Log.error("The underspecified placeholder type is not allowed as a concrete type.");
        return false;
      }
      // every type is allowed if the reference type is underspecified
      return true;
    }

    if (conType.getDefiningSymbol().isPresent() && refType.getDefiningSymbol().isPresent()) {
      ISymbol conTypeSymbol = conType.getDefiningSymbol().get();
      ISymbol refTypeSymbol = refType.getDefiningSymbol().get();
      if (conTypeSymbol.getFullName().equals(refTypeSymbol.getFullName())) {
        // the types are exactly the same
        return true;
      }
      if (isTypeIncarnation(conTypeSymbol, refTypeSymbol)) {
        // the concrete type is an incarnation of the reference type
        return true;
      }
    }
    return conType.deepEquals(refType);
  }

  /**
   * Returns if the concrete type is an incarnation of the reference type. If one of the types is
   * not a CD type, the method returns false since incarnation mapping is only defined between
   * concrete and reference CD elements.
   */
  private boolean isTypeIncarnation(ISymbol conType, ISymbol refType) {
    if (conType instanceof CDTypeSymbol && refType instanceof CDTypeSymbol) {
      CDTypeSymbol conCDType = (CDTypeSymbol) conType;
      CDTypeSymbol refCDType = (CDTypeSymbol) refType;
      if (conCDType.isPresentAstNode()) {
        return typeMatcher.getMatchedElements(conCDType.getAstNode()).stream()
                .anyMatch(r -> r.getSymbol().getFullName().equals(refCDType.getFullName()));
      }
    }
    return false;
  }
}
