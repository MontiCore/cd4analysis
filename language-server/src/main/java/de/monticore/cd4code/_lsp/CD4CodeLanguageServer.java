/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code._lsp;

import de.mclsg.lsp.modelpath.multiproject.ProjectLayout;
import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cd4code._lsp.features.code_action.CD4CodeCodeActionProvider;
import de.monticore.cd4analysis._lsp.features.code_action.CollapseHierarchyCodeActionStrategy;
import de.monticore.cd4analysis._lsp.features.code_action.ExtractSuperClassCodeActionStrategy;
import de.monticore.cd4analysis._lsp.features.code_action.PullUpFieldCodeActionStrategy;
import de.monticore.cd4analysis._lsp.features.code_action.UnfoldByAttributesStrategy;
import de.monticore.cd4code._lsp.language_access.CD4CodeScopeManager;

public class CD4CodeLanguageServer extends CD4CodeLanguageServerTOP {
  
  public CD4CodeLanguageServer(DocumentManager documentManager, ProjectLayout layout,
      CD4CodeScopeManager scopeManager,
      ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
    super(documentManager, layout, scopeManager, symbolUsageResolutionProvider);
    
    CD4CodeCodeActionProvider codeActionProvider = new CD4CodeCodeActionProvider(documentManager,
        languageAccess.getPrettyPrinter().orElseThrow());
    codeActionProvider.addCodeActionStrategy(new PullUpFieldCodeActionStrategy(documentManager,
        symbolUsageResolutionProvider, languageAccess.getPrettyPrinter().orElseThrow()));
    codeActionProvider.addCodeActionStrategy(new ExtractSuperClassCodeActionStrategy(
        documentManager, languageAccess.getPrettyPrinter().orElseThrow()));
    codeActionProvider.addCodeActionStrategy(new CollapseHierarchyCodeActionStrategy(
        documentManager, languageAccess.getPrettyPrinter().orElseThrow(),
        symbolUsageResolutionProvider));
    codeActionProvider.addCodeActionStrategy(new UnfoldByAttributesStrategy(documentManager,
        languageAccess.getPrettyPrinter().orElseThrow()));
    textDocumentService.register(codeActionProvider);
  }
  
}
