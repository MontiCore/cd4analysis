/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation.cocos.ebnf;

import de.monticore.cd.cocos.CoCoHelper;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDAssociationTOP;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks that association names are unique in the namespace.
 */
public class CDAssociationNameUnique implements CDBasisASTCDDefinitionCoCo {

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @Override
  public void check(ASTCDDefinition a) {
    final List<ASTCDAssociation> astcdAssociation = a.streamCDElements()
        .filter(e -> e instanceof ASTCDAssociation)
        .map(e -> (ASTCDAssociation) e)
        .filter(ASTCDAssociationTOP::isPresentName)
        .collect(Collectors.toList());

    CoCoHelper.findDuplicatesBy(astcdAssociation, ASTCDAssociationTOP::getName)
        .forEach(
            e -> Log.error(
                String.format("0xCDC64: Association name (%s) conflicts with other association (%s).",
                    e.getName(), astcdAssociation.stream()
                        .filter(oa -> oa.getName().equals(e.getName())).findFirst().get()),
                e.get_SourcePositionStart()));

  }
}
