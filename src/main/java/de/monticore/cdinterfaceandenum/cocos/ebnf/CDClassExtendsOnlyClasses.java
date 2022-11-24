/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdinterfaceandenum.cocos.ebnf;

import de.monticore.cd.CDMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._cocos.CDBasisASTCDClassCoCo;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 * Checks that classes do only extend other classes.
 */
public class CDClassExtendsOnlyClasses implements CDBasisASTCDClassCoCo {

  // TODO SVa: provide printer for the types,
  //  so that a user can provide their own printer

  @Override
  public void check(ASTCDClass clazz) {
    OOTypeSymbol symbol = clazz.getSymbol();

    if(!clazz.isPresentCDExtendUsage()) {
      return;
    }
    final List<ASTMCObjectType> superclassList = clazz.getCDExtendUsage().getSuperclassList();
    superclassList.stream()
      .map(
        s ->
          symbol
            .getEnclosingScope()
            .resolveOOType(s.printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter())))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .filter(e -> !e.isIsClass())
      .forEach(
        e ->
          Log.error(
            String.format(
              "0xCDC08: Class %s cannot extend %s %s. A class may only extend classes.",
              clazz.getName(), CDMill.cDTypeKindPrinter().print(e), e.getName()),
            clazz.get_SourcePositionStart()));
  }
}
