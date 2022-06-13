/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd._symboltable;

import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation._symboltable.CDRoleSymbolBuilder;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbolBuilder;
import de.monticore.symbols.oosymbols._symboltable.*;
import de.monticore.umlmodifier._ast.ASTModifier;

public class ModifierHandler {
  public void handle(ASTModifier modifier, CDTypeSymbolBuilder typeSymbol) {
    typeSymbol.setIsPublic(modifier.isPublic());
    typeSymbol.setIsPrivate(modifier.isPrivate());
    typeSymbol.setIsProtected(modifier.isProtected());
    typeSymbol.setIsStatic(modifier.isStatic());
    typeSymbol.setIsAbstract(modifier.isAbstract());
    typeSymbol.setIsDerived(modifier.isDerived());
  }

  public void handle(ASTModifier modifier, FieldSymbolBuilder fieldSymbol) {
    fieldSymbol.setIsPublic(modifier.isPublic());
    fieldSymbol.setIsPrivate(modifier.isPrivate());
    fieldSymbol.setIsProtected(modifier.isProtected());
    fieldSymbol.setIsStatic(modifier.isStatic());
    fieldSymbol.setIsFinal(modifier.isFinal());
    fieldSymbol.setIsDerived(modifier.isDerived());
  }

  public void handle(ASTModifier modifier, CDRoleSymbolBuilder roleSymbol) {
    roleSymbol.setIsPublic(modifier.isPublic());
    roleSymbol.setIsPrivate(modifier.isPrivate());
    roleSymbol.setIsProtected(modifier.isProtected());
    roleSymbol.setIsStatic(modifier.isStatic());
    roleSymbol.setIsFinal(modifier.isFinal());
    roleSymbol.setIsDerived(modifier.isDerived());
  }

  public void handle(ASTModifier modifier, MethodSymbolBuilder methodSymbol) {
    methodSymbol.setIsPublic(modifier.isPublic());
    methodSymbol.setIsPrivate(modifier.isPrivate());
    methodSymbol.setIsProtected(modifier.isProtected());
    methodSymbol.setIsStatic(modifier.isStatic());
  }

  /**
   * @param modifier
   * @param typeSymbol
   */
  public void handle(ASTModifier modifier, CDTypeSymbol typeSymbol) {
    typeSymbol.setIsPublic(modifier.isPublic());
    typeSymbol.setIsPrivate(modifier.isPrivate());
    typeSymbol.setIsProtected(modifier.isProtected());
    typeSymbol.setIsStatic(modifier.isStatic());
    typeSymbol.setIsAbstract(modifier.isAbstract());
    typeSymbol.setIsDerived(modifier.isDerived());
  }

  /**
   * @param modifier
   * @param fieldSymbol
   */
  public void handle(ASTModifier modifier, FieldSymbol fieldSymbol) {
    fieldSymbol.setIsPublic(modifier.isPublic());
    fieldSymbol.setIsPrivate(modifier.isPrivate());
    fieldSymbol.setIsProtected(modifier.isProtected());
    fieldSymbol.setIsStatic(modifier.isStatic());
    fieldSymbol.setIsFinal(modifier.isFinal());
    fieldSymbol.setIsDerived(modifier.isDerived());
  }

  /**
   * @param modifier
   * @param roleSymbol
   */
  public void handle(ASTModifier modifier, CDRoleSymbol roleSymbol) {
    roleSymbol.setIsPublic(modifier.isPublic());
    roleSymbol.setIsPrivate(modifier.isPrivate());
    roleSymbol.setIsProtected(modifier.isProtected());
    roleSymbol.setIsStatic(modifier.isStatic());
    roleSymbol.setIsFinal(modifier.isFinal());
    roleSymbol.setIsDerived(modifier.isDerived());
  }

  /**
   * @param modifier
   * @param methodSymbol
   */
  public void handle(ASTModifier modifier, MethodSymbol methodSymbol) {
    methodSymbol.setIsPublic(modifier.isPublic());
    methodSymbol.setIsPrivate(modifier.isPrivate());
    methodSymbol.setIsProtected(modifier.isProtected());
    methodSymbol.setIsStatic(modifier.isStatic());
  }
}
