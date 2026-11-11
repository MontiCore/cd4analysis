/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.cd;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

public class ImportsCompleter extends AbstractCDCompleter {
  
  @Override
  public void complete(ASTCDCompilationUnit ccd, ASTCDCompilationUnit rcd,
      CDCompletionContext context) throws CompletionException {
    for (ASTMCImportStatement importStatement : rcd.getMCImportStatementList()) {
      boolean alreadyExists = false;
      for (ASTMCImportStatement existingImport : ccd.getMCImportStatementList()) {
        if (existingImport.getQName().equals(importStatement.getQName()) && existingImport.isStar()
            == importStatement.isStar()) {
          alreadyExists = true;
          break;
        }
      }
      if (!alreadyExists) {
        ccd.getMCImportStatementList().add(importStatement);
      }
    }
    super.complete(ccd, rcd, context);
  }
  
}
