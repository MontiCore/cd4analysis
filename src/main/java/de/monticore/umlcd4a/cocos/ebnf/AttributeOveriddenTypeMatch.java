/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.monticore.cocos.CoCoLog;
import de.monticore.umlcd4a._ast.ASTCDAttribute;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

/**
 * Checks that overridden attributes are of the same kind.
 *
 * @author Robert Heim
 */
public class AttributeOveriddenTypeMatch
    implements CD4AnalysisASTCDAttributeCoCo {
  
  public static final String ERROR_CODE = "0xC4A13";
  
  public static final String ERROR_MSG_FORMAT = "Class %s overrides the attribute %s (type: %s) of class %s with the different type %s.";
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAttributeCoCo#check(de.monticore.umlcd4a._ast.ASTCDAttribute)
   */
  @Override
  public void check(ASTCDAttribute node) {
    CDFieldSymbol attrSym = (CDFieldSymbol) node.getSymbol().get();
    CDTypeSymbol subClassSym = (CDTypeSymbol) node.getEnclosingScope().get()
        .getSpanningSymbol().get();
    Collection<CDFieldSymbol> superAttrs = subClassSym.getAllVisibleFieldsOfSuperTypes();
    List<CDFieldSymbol> overriddenSymbols = superAttrs.stream()
        // same name
        .filter(sA -> sA.getName().equals(attrSym.getName()))
        // different type
        .filter(sA -> !sA.getType().getName().equals(attrSym.getType().getName()))
        .collect(Collectors.toList());
    
    if (!overriddenSymbols.isEmpty()) {
      CDFieldSymbol anOverriddenSym = overriddenSymbols.get(0);
      // String typeName = TypesPrinter.printType(node.getType());
      // see TypeChecker javadoc for more information
      CoCoLog.error(ERROR_CODE,
          String.format(ERROR_MSG_FORMAT,
              subClassSym.getName(),
              anOverriddenSym.getName(),
              anOverriddenSym.getType().getName(),
              anOverriddenSym.getEnclosingScope().getName(),
              attrSym.getType().getName()),
          node.get_SourcePositionStart());
    }
  }
  
}
