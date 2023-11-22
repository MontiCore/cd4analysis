package de.monticore.cd4analysis._lsp.features.completion.strategy;

import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.features.completion.ExpectedToken;
import de.mclsg.lsp.features.completion.strategy.CompletionStrategy;
import de.monticore.symboltable.ISymbol;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.lsp4j.CompletionItem;

public class CD4AnalysisAssociationCardinalityCompletionStrategy implements CompletionStrategy {

  private final String TOKEN_PATH_LEFT_REGEX =
      "cDCompilationUnit.cDDefinition.cDElement.cDAssociation.cDAssocLeftSide";
  private final String TOKEN_PATH_RIGHT_REGEX =
      "cDCompilationUnit.cDDefinition.cDElement.cDAssociation";

  private final DocumentManager documentManager;

  public CD4AnalysisAssociationCardinalityCompletionStrategy(
      ISymbolUsageResolutionProvider symbolUsageResolutionProvider,
      DocumentManager documentManager) {
    this.documentManager = documentManager;
  }

  @Override
  public List<? extends ISymbol> getSymbols(
      ExpectedToken token, DocumentInformation documentInformation) {
    return new ArrayList<>();
  }

  @Override
  public List<CompletionItem> getAdditionalCompletions(
      ExpectedToken token, DocumentInformation documentInformation, String contentUntilCompletion) {
    List<CompletionItem> itemList =
        CompletionStrategy.super.getAdditionalCompletions(
            token, documentInformation, contentUntilCompletion);
    itemList.add(new CompletionItem("[]"));
    itemList.add(new CompletionItem("[*]"));
    itemList.add(new CompletionItem("[1..*]"));
    itemList.add(new CompletionItem("[ .. ]"));

    return itemList;
  }

  @Override
  public boolean matches(ExpectedToken expectedToken) {
    return expectedToken.tokenPathMatches(TOKEN_PATH_LEFT_REGEX)
        || expectedToken.tokenPathMatches(TOKEN_PATH_RIGHT_REGEX);
  }
}
