/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.cocos.mcg2ebnf;

import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._cocos.CDBasisASTCDPackageCoCo;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.stream.Collectors;

/** Checks that there are no packages inside of packages. */
public class CDPackageNotContainingCDPackage implements CDBasisASTCDPackageCoCo {

  /** @param node package to check. */
  @Override
  public void check(ASTCDPackage node) {
    List<ASTCDPackage> subPackages =
        node.streamCDElements()
            .filter(e -> e instanceof ASTCDPackage)
            .map(e -> (ASTCDPackage) e)
            .collect(Collectors.toList());

    if (!subPackages.isEmpty()) {
      Log.error(
          String.format(
              "0xCDC20: The package \"%s\" has subpackages (%s). Packages can not have subpackages.",
              node.getMCQualifiedName().getQName(),
              subPackages.stream()
                  .map(p -> p.getMCQualifiedName().getQName())
                  .collect(Collectors.joining(","))),
          node.get_SourcePositionStart());
    }
  }
}
