/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.ow2cw;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class CDAttributeHelper {
  
  public static ASTCDType resolveClass(ASTCDAttribute attribute) {
    Optional<TypeSymbol> typeSymbol = attribute.getEnclosingScope().resolveType(attribute
        .getSymbol().getType().getTypeInfo().getName());
    if (typeSymbol.isPresent() && typeSymbol.get().isPresentAstNode() && typeSymbol.get()
        .getAstNode() instanceof ASTCDType) {
      return (ASTCDType) typeSymbol.get().getAstNode();
    }
    return null;
  }
  
  public static Set<ASTCDAttribute> getAttributes(ASTCDType cdType) {
    return new LinkedHashSet<>(cdType.getCDAttributeList());
  }
  
}
