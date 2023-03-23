/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdinterfaceandenum._symboltable;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.ImportStatement;

public class TestCDInterfaceAndEnumScopesGenitor extends TestCDInterfaceAndEnumScopesGenitorTOP {

  @Override
  public ITestCDInterfaceAndEnumArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    ITestCDInterfaceAndEnumArtifactScope as = super.createFromAST(rootNode);

    // set package
    if (rootNode.isPresentMCPackageDeclaration()) {
      as.setPackageName(rootNode.getMCPackageDeclaration().getMCQualifiedName().getQName());
    }
    // add imports
    rootNode
        .getMCImportStatementList()
        .forEach(i -> as.addImports(new ImportStatement(i.getQName(), i.isStar())));

    return as;
  }
}
