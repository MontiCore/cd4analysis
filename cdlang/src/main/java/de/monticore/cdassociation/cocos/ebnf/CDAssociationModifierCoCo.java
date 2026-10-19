/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation.cocos.ebnf;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.cdassociation._symboltable.ICDAssociationScope;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.function.Predicate;

public class CDAssociationModifierCoCo implements CDAssociationASTCDAssociationCoCo {
  
  @Override
  public void check(ASTCDAssociation a) {
    
    String assocName = a.isPresentName() ? a.getName() : "unnamed";
    
    // Check for conflicting visibility modifiers
    boolean hasMain = hasVisibility(a.getModifier());
    boolean hasRoleVisibility = hasVisibility(a.getLeft().getModifier()) || hasVisibility(a
        .getRight().getModifier());
    
    if (hasMain && hasRoleVisibility) {
      Log.warn(String.format(
          "The association '%s' cannot combine a main visibility modifier with individual role visibility modifiers.",
          assocName), a.get_SourcePositionStart());
    }
    
    //----------------------------------check the rest modifiers-----------------------------------------------------------
    
    //// Ensure that the association does not connect static classes
    ICDAssociationScope scope = a.getEnclosingScope();
    
    boolean staticClassLeft = isStaticType(scope.resolveType(a.getLeft().getName()));
    boolean staticClassRight = isStaticType(scope.resolveType(a.getRight().getName()));
    
    if (staticClassLeft || staticClassRight) {
      Log.error(String.format("0xCDA66: The association '%s' cannot connect static classes.",
          assocName), a.get_SourcePositionStart());
    }
    
    //// Ensure that neither the association nor its roles use forbidden modifiers (static, abstract, local)
    checkForbiddenModifier(a, ASTModifier::isStatic, "0xCDA61", String.format(
        "The association '%s' (or its roles) must not use static modifiers.", assocName));
    checkForbiddenModifier(a, ASTModifier::isAbstract, "0xCDA67", String.format(
        "The association '%s' (or its roles) must not use an abstract modifier.", assocName));
    checkForbiddenModifier(a, ASTModifier::isLocal, "0xCDA68", String.format(
        "The association '%s' (or its roles) must not use a local modifier.", assocName));
    
  }
  
  /**
   * Checks whether the given modifier specifies an explicit visibility
   * (i.e., public, protected, or private).
   *
   * @param modifier the AST modifier to check (can be null)
   * @return true if a public, protected, or private modifier is present, false otherwise
   */
  private boolean hasVisibility(ASTModifier modifier) {
    return modifier != null && (modifier.isPublic() || modifier.isProtected() || modifier
        .isPrivate());
  }
  
  /**
   * Safely checks a boolean flag on an ASTModifier, handling null values.
   *
   * @param modifier the AST modifier to check (can be null)
   * @param check a predicate representing the flag to check
   * @return true if the modifier is present and the check evaluates to true, false otherwise
   */
  private boolean hasModifier(ASTModifier modifier, Predicate<ASTModifier> check) {
    return modifier != null && check.test(modifier);
  }
  
  /**
   * Checks whether the given type symbol represents a static class.
   *
   * @param optSymbol an optional type symbol resolved from the scope
   * @return true if the symbol is a static class, false otherwise
   */
  private boolean isStaticType(Optional<TypeSymbol> optSymbol) {
    if (optSymbol.isPresent() && optSymbol.get() instanceof CDTypeSymbol typeSymbol) {
      return typeSymbol.isPresentAstNode() && typeSymbol.getAstNode().getModifier().isStatic();
    }
    return false;
  }
  
  /**
   * Checks whether a forbidden modifier is present on the association itself or its roles,
   * and logs an error if the check evaluates to true.
   *
   * @param a the association AST node to check
   * @param check a predicate representing the modifier flag to check
   * @param errorCode the error code to log
   * @param errorMessage the error message to log
   */
  private void checkForbiddenModifier(ASTCDAssociation a, Predicate<ASTModifier> check,
      String errorCode, String errorMessage) {
    boolean main = hasModifier(a.getModifier(), check);
    boolean left = hasModifier(a.getLeft().getModifier(), check);
    boolean right = hasModifier(a.getRight().getModifier(), check);
    if (main || left || right) {
      Log.error(String.format("%s: %s", errorCode, errorMessage), a.get_SourcePositionStart());
    }
  }
  
}
