/*

 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.ebnf;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that overridden attributes are of the same kind.
 *
 * @author Robert Heim
 */
public class AttributeOverriddenTypeMatch
    implements CD4AnalysisASTCDAttributeCoCo {
  
  /**
   * @see de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAttributeCoCo#check(de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute)
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
      Log.error(
          String
              .format(
                  "0xC4A13 Class %s overrides the attribute %s (type: %s) of class %s with the different type %s.",
                  subClassSym.getName(),
                  anOverriddenSym.getName(),
                  anOverriddenSym.getType().getName(),
                  anOverriddenSym.getEnclosingScope().getName().get(),
                  attrSym.getType().getName()),
          node.get_SourcePositionStart());
    }
  }
  
}
