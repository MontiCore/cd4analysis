/* (c) https://github.com/MontiCore/monticore */
package de.monticore.stdefinition._symboltable;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

public class STDefinitionScopesGenitor extends STDefinitionScopesGenitorTOP {

  @Override
  public ISTDefinitionArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    ISTDefinitionArtifactScope as = super.createFromAST(rootNode);

    // set package
    if (rootNode.isPresentMCPackageDeclaration()) {
      as.setPackageName(rootNode.getMCPackageDeclaration().getMCQualifiedName().getQName());
    }
    // add imports
    for (ASTMCImportStatement i : rootNode.getMCImportStatementList()) {
      as.addImports(new ImportStatement(i.getQName(), i.isStar()));
    }

    return as;
  }
}
