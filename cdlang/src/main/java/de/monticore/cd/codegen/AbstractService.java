/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import com.google.common.collect.Lists;
import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import java.util.*;

public class AbstractService {
  
  public static List<CDTypeSymbol> getAllSuperClassesTransitive(CDTypeSymbol cdTypeSymbol) {
    List<CDTypeSymbol> superSymbolList = new ArrayList<>();
    if (cdTypeSymbol.isPresentSuperClass()) {
      TypeSymbol superSymbol = cdTypeSymbol.getSuperClass().getTypeInfo();
      superSymbolList.add((CDTypeSymbol) superSymbol);
      superSymbolList.addAll(getAllSuperClassesTransitive((CDTypeSymbol) superSymbol));
    }
    return superSymbolList;
  }
  
  public static List<CDTypeSymbol> getAllSuperInterfacesTransitive(CDTypeSymbol cdTypeSymbol) {
    List<CDTypeSymbol> superSymbolList = Lists.newArrayList();
    for (SymTypeExpression s : cdTypeSymbol.getSuperTypesList()) {
      if (isInterface(s)) {
        TypeSymbol typeSymbol = s.getTypeInfo();
        superSymbolList.add((CDTypeSymbol) typeSymbol);
      }
    }
    List<CDTypeSymbol> result = new ArrayList<>();
    for (CDTypeSymbol superInterface : superSymbolList) {
      result.add(superInterface);
      result.addAll(getAllSuperInterfacesTransitive(superInterface));
    }
    return result;
  }
  
  protected static boolean isInterface(SymTypeExpression s) {
    return s.getTypeInfo().getAstNode() instanceof ASTCDInterface;
  }
  
}
