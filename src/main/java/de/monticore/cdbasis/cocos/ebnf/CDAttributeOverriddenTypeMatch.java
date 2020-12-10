/*
 * (c) https://github.com/MontiCore/monticore
 */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cd._symboltable.OOTypeHelper;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks that overridden attributes are of the same kind.
 */
public class CDAttributeOverriddenTypeMatch
    implements CDBasisASTCDAttributeCoCo {

  /**
   * @see de.monticore.cdbasis._cocos.CDBasisASTCDAttributeCoCo#check(ASTCDAttribute)
   */
  @Override
  public void check(ASTCDAttribute node) {
    FieldSymbol attrSym = node.getSymbol();
    OOTypeSymbol subClassSym = (OOTypeSymbol) node.getEnclosingScope()
        .getSpanningSymbol();
    Collection<VariableSymbol> superAttrs = new ArrayList<>();
    subClassSym.getSuperClassesOnly()
        // Add all attributes of the super types and all inherited attributes of the super types
        .forEach(sT -> {
          superAttrs.addAll(sT.getTypeInfo().getVariableList());
          superAttrs.addAll(OOTypeHelper.getAllVariablesOfSuperTypes(sT.getTypeInfo()));
        });
    List<VariableSymbol> overriddenSymbols = superAttrs.stream()
        // same name
        .filter(sA -> sA.getName().equals(attrSym.getName()))
        // different type
        .filter(sA -> !sA.getType().print().equals(attrSym.getType().print()))
        // not a subclass
        .filter(sA -> TypeCheck.isSubtypeOf(attrSym.getType(), sA.getType()))
        .collect(Collectors.toList());

    if (!overriddenSymbols.isEmpty()) {
      VariableSymbol anOverriddenSym = overriddenSymbols.get(0);
      Log.error(
          String
              .format(
                  "0xCDC04: Class %s overrides the attribute %s (type: %s) of class %s with the different type %s which is no subtype.",
                  subClassSym.getName(),
                  anOverriddenSym.getName(),
                  anOverriddenSym.getType().print(),
                  anOverriddenSym.getEnclosingScope().getName(),
                  attrSym.getType().print()),
          node.get_SourcePositionStart());
    }
  }

}
