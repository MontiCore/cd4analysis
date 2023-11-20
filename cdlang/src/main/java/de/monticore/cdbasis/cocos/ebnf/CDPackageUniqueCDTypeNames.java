/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cd.cocos.CoCoHelper;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._cocos.CDBasisASTCDPackageCoCo;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;
import java.util.Collection;
import java.util.stream.Collectors;

/** Checks uniqueness among the names of classes, interfaces, and enums. */
public class CDPackageUniqueCDTypeNames implements CDBasisASTCDPackageCoCo {

  @Override
  public void check(ASTCDPackage cdPackage) {
    Collection<CDTypeSymbol> types =
        cdPackage
            .streamCDElements()
            .filter(e -> e instanceof ASTCDType)
            .map(e -> ((ASTCDType) e).getSymbol())
            .collect(Collectors.toList());

    CoCoHelper.findDuplicatesBy(types, TypeSymbol::getName)
        .forEach(
            e ->
                Log.error(
                    String.format(
                        "0xCDC0F: The name %s is used several times. Classes, interfaces and enumerations may not use the same names.",
                        e.getName()),
                    e.getAstNode().get_SourcePositionStart()));
  }
}
