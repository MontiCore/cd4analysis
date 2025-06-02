/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.util;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.symbols.basicsymbols._ast.ASTType;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._ast.ASTField;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.ISymbol;

public class SymbolUtil {
  
  private SymbolUtil() {}
  
  public static TypeSymbol getDeclaringTypeSymbol(FieldSymbol fieldSymbol) {
    IScope enclosingScope = fieldSymbol.getEnclosingScope();
    if (!enclosingScope.isPresentSpanningSymbol()) {
      throw new IllegalStateException("FieldSymbol without enclosing scope: " + fieldSymbol
          .getFullName());
    }
    IScopeSpanningSymbol symbol = enclosingScope.getSpanningSymbol();
    if (symbol instanceof TypeSymbol) {
      return (TypeSymbol) symbol;
    }
    else {
      throw new IllegalStateException("FieldSymbol not enclosed in a type symbol: " + fieldSymbol
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
  
}
