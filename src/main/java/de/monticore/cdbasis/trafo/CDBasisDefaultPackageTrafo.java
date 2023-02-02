/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.trafo;

import de.monticore.cd.cocos.CoCoHelper;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import java.util.List;
import java.util.stream.Collectors;

public class CDBasisDefaultPackageTrafo implements CDBasisVisitor2 {

  @Override
  public void visit(ASTCDDefinition node) {
    combinePackagesWithSameName(node);
  }

  protected void combinePackagesWithSameName(ASTCDDefinition node) {
    final List<ASTCDPackage> packages =
        node.streamCDElements()
            .filter(e -> e instanceof ASTCDPackage)
            .map(e -> (ASTCDPackage) e)
            .collect(Collectors.toList());

    final List<ASTCDPackage> duplicates =
        CoCoHelper.findDuplicatesBy(packages, (e) -> e.getMCQualifiedName().getQName());
    node.getCDElementList().removeAll(duplicates);
    duplicates.forEach(
        e ->
            packages.stream()
                .filter(
                    p ->
                        p.getMCQualifiedName().getQName().equals(e.getMCQualifiedName().getQName()))
                .findFirst()
                .ifPresent(pa -> pa.addAllCDElements(e.getCDElementList())));
  }
}
