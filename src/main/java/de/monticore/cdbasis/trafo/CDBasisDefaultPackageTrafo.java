/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.trafo;

import de.monticore.cd.cocos.CoCoHelper;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CDBasisDefaultPackageTrafo implements CDBasisVisitor2 {

  List<String> artifactPackageParts;

  @Override
  public void visit(ASTCDCompilationUnit node) {
    artifactPackageParts = new ArrayList<>();

    // set artifact package parts to the default package
    if (node.isPresentMCPackageDeclaration()) {
      artifactPackageParts.addAll(node.getMCPackageDeclaration().getMCQualifiedName().getPartsList());
    }
  }

  @Override
  public void visit(ASTCDDefinition node) {
    // add cd name (lower case) to the default package
    artifactPackageParts.add(node.getName().toLowerCase());

    // create the default package
    ASTMCQualifiedName qualName = CDBasisMill.mCQualifiedNameBuilder()
      .addAllParts(artifactPackageParts)
      .build();
    ASTCDPackage defPkg = CDBasisMill.cDPackageBuilder()
      .setMCQualifiedName(qualName)
      .build();

    // add elements (that are not packages themselves) to the default package
    for (ASTCDElement e : node.getCDElementList()) {
      if(!(e instanceof ASTCDPackage)) {
        defPkg.addCDElement(e);
      }
    }

    // remove these cd elements from cd definition
    node.removeAllCDElements(defPkg.getCDElementList());

    // the remaining direct elements of the cd definition are all packages
    // extend the packge with the prefix of the default package
    for (ASTCDElement e : node.getCDElementList()) {
      ASTCDPackage pkg = (ASTCDPackage) e;
      pkg.getMCQualifiedName().getPartsList().addAll(0, defPkg.getMCQualifiedName().getPartsList());
    }

    // add default package to cd elements of the diagram and
    // explicitly set the link towards the default package
    node.addCDElement(0, defPkg);
    node.setDefaultPackage(defPkg);

  }
}
