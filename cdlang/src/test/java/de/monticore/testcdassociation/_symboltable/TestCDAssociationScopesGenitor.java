/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation._symboltable;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.ImportStatement;

public class TestCDAssociationScopesGenitor extends TestCDAssociationScopesGenitorTOP {

  @Override
  public ITestCDAssociationArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    ITestCDAssociationArtifactScope as = super.createFromAST(rootNode);

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
