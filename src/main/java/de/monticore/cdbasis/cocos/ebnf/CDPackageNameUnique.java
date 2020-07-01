/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis.cocos.ebnf;

import de.monticore.cd.cocos.CoCoHelper;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

public class CDPackageNameUnique
    implements CDBasisASTCDDefinitionCoCo {
  @Override
  public void check(ASTCDDefinition node) {
    final List<ASTCDPackage> packages = node
        .getCDElementList()
        .stream()
        .filter(e -> e instanceof ASTCDPackage)
        .map(e -> (ASTCDPackage) e)
        .collect(Collectors.toList());

    CoCoHelper.findDuplicates(packages).forEach(e ->
        Log.error(
            String
                .format(
                    "0xCDC0E: The package name \"%s\" is used several times. Packages need to have a unique name.",
                    e.getMCQualifiedName().getQName()),
            e.get_SourcePositionStart())
    );
  }
}
