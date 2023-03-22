/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis._symboltable;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.ImportStatement;
import de.monticore.testcd4codebasis._symboltable.ITestCD4CodeBasisArtifactScope;

public  class TestCDBasisScopesGenitor extends TestCDBasisScopesGenitorTOP {

  @Override
  public ITestCDBasisArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    ITestCDBasisArtifactScope as = super.createFromAST(rootNode);

    // set package
    if (rootNode.isPresentMCPackageDeclaration()) {
      as.setPackageName(rootNode.getMCPackageDeclaration().getMCQualifiedName().getQName());
    }
    // add imports
    rootNode.getMCImportStatementList()
      .forEach(i -> as.addImports(new ImportStatement(i.getQName(), i.isStar())));

    return as;
  }
}
