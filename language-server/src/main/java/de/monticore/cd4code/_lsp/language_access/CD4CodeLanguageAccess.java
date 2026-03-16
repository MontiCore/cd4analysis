/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._lsp.language_access;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.AstPrettyPrinter;
import java.util.Optional;

public class CD4CodeLanguageAccess extends CD4CodeLanguageAccessTOP {
  
  private final CD4CodeAstPrettyPrinter prettyPrinter;
  
  public CD4CodeLanguageAccess(DocumentManager documentManager, CD4CodeScopeManager scopeManager) {
    super(documentManager, scopeManager);
    this.prettyPrinter = new CD4CodeAstPrettyPrinter();
  }
  
  @Override
  public Optional<AstPrettyPrinter<ASTCDCompilationUnit>> getPrettyPrinter() {
    return Optional.of(prettyPrinter);
  }
  
}
