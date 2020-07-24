/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cd.cocos.CoCoHelper;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Checks uniqueness among the names of classes, interfaces, and enums.
 */
public class CDDefinitionUniqueCDTypeNames
    implements CDBasisASTCDDefinitionCoCo {

  @Override
  public void check(ASTCDDefinition cdDefinition) {
    Collection<CDTypeSymbol> types = cdDefinition.streamCDElements()
        .filter(e -> e instanceof ASTCDType)
        .map(e -> ((ASTCDType) e).getSymbol()).collect(Collectors.toList());

    CoCoHelper.findDuplicates(types).forEach(e ->
        Log.error(
            String
                .format(
                    "0xCDC0D: The name %s is used several times. Classes, interfaces and enumerations may not use the same names.",
                    e.getName()),
            e.getAstNode().get_SourcePositionStart())
    );
  }
}
