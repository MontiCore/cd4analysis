/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._symboltable;

import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.ImportStatement;

public  class CD4CodeScopesGenitor extends CD4CodeScopesGenitorTOP {

  @Override
  public ICD4CodeArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    ICD4CodeArtifactScope as = super.createFromAST(rootNode);

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
