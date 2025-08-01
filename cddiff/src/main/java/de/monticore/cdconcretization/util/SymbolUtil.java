/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.util;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.symbols.basicsymbols._ast.ASTType;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._ast.ASTField;
import de.monticore.symbols.oosymbols._ast.ASTMethod;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.*;

public class SymbolUtil {
  
  private SymbolUtil() {}
  
  public static TypeSymbol getDeclaringTypeSymbol(ISymbol iSymbol) {
    IScope enclosingScope = iSymbol.getEnclosingScope();
    if (!enclosingScope.isPresentSpanningSymbol()) {
      throw new IllegalStateException("Symbol without enclosing scope: " + iSymbol
          .getFullName());
    }
    IScopeSpanningSymbol symbol = enclosingScope.getSpanningSymbol();
    if (symbol instanceof TypeSymbol) {
      return (TypeSymbol) symbol;
    }
    else {
      throw new IllegalStateException("Symbol not enclosed in a type symbol: " + iSymbol
          .getFullName());
    }
  }

  public static String getFullNameWithoutCD(ISymbol symbol) {
    return NameUtil.removeFirstQualifierPart(symbol.getFullName());
  }
  
  public static ASTCDType cdTypeFromTypeSymbol(TypeSymbol typeSymbol) {
    ASTType astType = typeSymbol.getAstNode();
    if (astType instanceof ASTCDType) {
      return (ASTCDType) astType;
    }
    else {
      throw new IllegalStateException("TypeSymbol is not representing a CDType: " + typeSymbol
          .getFullName());
    }
  }
  
  public static ASTCDAttribute cdAttributeFromFieldSymbol(FieldSymbol fieldSymbol) {
    ASTField astField = fieldSymbol.getAstNode();
    if (astField instanceof ASTCDAttribute) {
      return (ASTCDAttribute) astField;
    }
    else {
      throw new IllegalStateException("FieldSymbol is not representing a CDAttribute: "
          + fieldSymbol.getFullName());
    }
  }
  
  public static ASTCDMethod cdMethodFromMethodSymbol(MethodSymbol methodSymbol) {
    ASTMethod astMethod = methodSymbol.getAstNode();
    if (astMethod instanceof ASTCDMethod) {
      return (ASTCDMethod) astMethod;
    }
    else {
      throw new IllegalStateException("MethodSymbol is not representing a CDMethod: " + methodSymbol
          .getFullName());
    }
  }
  
  public static ICDBasisScope getArtifactScope(ICDBasisScope scope) {
    if (scope instanceof IArtifactScope) {
      return scope;
    }
    else if (scope instanceof IGlobalScope) {
      throw new IllegalArgumentException("Cannot get artifact scope from global scope: " + scope
          .getName());
    }
    else {
      return getArtifactScope(scope.getEnclosingScope());
    }
  }
  
}
