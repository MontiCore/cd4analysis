/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDField;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo;
import de.monticore.cd.cd4analysis._symboltable.CDFieldSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks that overridden attributes are of the same kind.
 *
 * @author Robert Heim
 */
public class AttributeOverriddenTypeMatch
    implements CD4AnalysisASTCDAttributeCoCo {
  
  /**
   * @see de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDAttributeCoCo#check(ASTCDAttribute)
   */
  @Override
  public void check(ASTCDAttribute node) {
    CDFieldSymbol attrSym = (CDFieldSymbol) node.getSymbol();
    CDTypeSymbol subClassSym = (CDTypeSymbol) node.getEnclosingScope()
        .getSpanningSymbol().get();
    Collection<CDFieldSymbol> superAttrs = new ArrayList<>();
    subClassSym.getSuperTypes().stream()
        // Add all attributes of the super types and all inherited attributes of the super types
        .forEach(sT -> { superAttrs.addAll(sT.getFields()); superAttrs.addAll(sT.getAllVisibleFieldsOfSuperTypes());});
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
