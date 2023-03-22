/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis._symboltable;

import de.monticore.cd4analysis._symboltable.CD4AnalysisScopesGenitorTOP;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.ImportStatement;

public  class TestCD4CodeBasisScopesGenitor extends TestCD4CodeBasisScopesGenitorTOP {

  @Override
  public ITestCD4CodeBasisArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    ITestCD4CodeBasisArtifactScope as = super.createFromAST(rootNode);

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
