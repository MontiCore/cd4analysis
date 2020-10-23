/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import de.monticore.symbols.oosymbols._symboltable.*;
import de.monticore.umlmodifier._ast.ASTModifier;

public class ModifierHandler {
  public void handle(ASTModifier modifier, OOTypeSymbolBuilder typeSymbol) {
    typeSymbol.setIsPublic(modifier.isPublic());
    typeSymbol.setIsPrivate(modifier.isPrivate());
    typeSymbol.setIsProtected(modifier.isProtected());
    typeSymbol.setIsStatic(modifier.isStatic());
    typeSymbol.setIsAbstract(modifier.isAbstract());
  }

  public void handle(ASTModifier modifier, FieldSymbolBuilder fieldSymbol) {
    fieldSymbol.setIsPublic(modifier.isPublic());
    fieldSymbol.setIsPrivate(modifier.isPrivate());
    fieldSymbol.setIsProtected(modifier.isProtected());
    fieldSymbol.setIsStatic(modifier.isStatic());
    fieldSymbol.setIsFinal(modifier.isFinal());
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
  public void handle(ASTModifier modifier, OOTypeSymbol typeSymbol) {
    typeSymbol.setIsPublic(modifier.isPublic());
    typeSymbol.setIsPrivate(modifier.isPrivate());
    typeSymbol.setIsProtected(modifier.isProtected());
    typeSymbol.setIsStatic(modifier.isStatic());
    typeSymbol.setIsAbstract(modifier.isAbstract());
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
