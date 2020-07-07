/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.modifier;

import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbol;
import de.monticore.umlmodifier._ast.ASTModifier;

public class ModifierHandler {
  public void handle(ASTModifier modifier, OOTypeSymbol typeSymbol) {
    typeSymbol.setIsPublic(modifier.isPublic());
    typeSymbol.setIsPrivate(modifier.isPrivate());
    typeSymbol.setIsProtected(modifier.isProtected());
    typeSymbol.setIsStatic(modifier.isStatic());
    typeSymbol.setIsAbstract(modifier.isAbstract());
  }

  public void handle(ASTModifier modifier, FieldSymbol fieldSymbol) {
    fieldSymbol.setIsPublic(modifier.isPublic());
    fieldSymbol.setIsPrivate(modifier.isPrivate());
    fieldSymbol.setIsProtected(modifier.isProtected());
    fieldSymbol.setIsStatic(modifier.isStatic());
    fieldSymbol.setIsFinal(modifier.isFinal());
  }

  public void handle(ASTModifier modifier, MethodSymbol fieldSymbol) {
    fieldSymbol.setIsPublic(modifier.isPublic());
    fieldSymbol.setIsPrivate(modifier.isPrivate());
    fieldSymbol.setIsProtected(modifier.isProtected());
    fieldSymbol.setIsStatic(modifier.isStatic());
  }
}
