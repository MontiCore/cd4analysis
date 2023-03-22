/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis._symboltable;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.ImportStatement;

public  class CD4AnalysisScopesGenitor extends CD4AnalysisScopesGenitorTOP {

  @Override
  public ICD4AnalysisArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    ICD4AnalysisArtifactScope as = super.createFromAST(rootNode);

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
