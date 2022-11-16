/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cd._symboltable.OOTypeHelper;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks that overridden attributes are of the same kind. This coco is optional and should be used
 * when the class diagram is used by a generator.
 */
public class CDAttributeOverridden implements CDBasisASTCDAttributeCoCo {

  /** @see de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo#check(ASTCDAttribute) */
  @Override
  public void check(ASTCDAttribute node) {
    FieldSymbol attrSym = node.getSymbol();
    OOTypeSymbol subClassSym = (OOTypeSymbol) node.getEnclosingScope().getSpanningSymbol();
    Collection<VariableSymbol> superAttrs = new ArrayList<>();
    subClassSym
        .getSuperClassesOnly()
        // Add all attributes of the super types and all inherited attributes of the super types
        .forEach(
            sT -> {
              superAttrs.addAll(sT.getTypeInfo().getVariableList());
              superAttrs.addAll(OOTypeHelper.getAllVariablesOfSuperTypes(sT.getTypeInfo()));
            });
    List<VariableSymbol> overriddenSymbols =
        superAttrs.stream()
            // same name
            .filter(sA -> sA.getName().equals(attrSym.getName()))
            .collect(Collectors.toList());

    if (!overriddenSymbols.isEmpty()) {
      VariableSymbol anOverriddenSym = overriddenSymbols.get(0);
      Log.error(
          String.format(
              "0xCDC04: Class %s overrides the attribute %s of class %s which is not allowed.",
              subClassSym.getName(),
              anOverriddenSym.getName(),
              anOverriddenSym.getEnclosingScope().getName()),
          node.get_SourcePositionStart());
    }
  }
}
