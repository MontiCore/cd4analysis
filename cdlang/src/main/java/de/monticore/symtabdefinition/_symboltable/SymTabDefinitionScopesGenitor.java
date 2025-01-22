/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symtabdefinition._symboltable;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

public class SymTabDefinitionScopesGenitor extends SymTabDefinitionScopesGenitorTOP {

  @Override
  public ISymTabDefinitionArtifactScope createFromAST(ASTCDCompilationUnit rootNode) {
    ISymTabDefinitionArtifactScope as = super.createFromAST(rootNode);

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
