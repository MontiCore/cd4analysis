package de.monticore.cd4analysis._lsp.features.completion.strategy;

import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.features.completion.ExpectedToken;
import de.mclsg.lsp.features.completion.strategy.CompletionStrategy;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symboltable.ISymbol;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CD4AnalysisAssociationCompletionStrategy implements CompletionStrategy {

  private final String TOKEN_PATH_REGEX =
      ".*cDAssociation\\.cDAssoc(.*)Side\\.mCQualifiedType\\.mCQualifiedName";
  private final DocumentManager documentManager;

  public CD4AnalysisAssociationCompletionStrategy(
      ISymbolUsageResolutionProvider symbolUsageResolutionProvider,
      DocumentManager documentManager) {
    this.documentManager = documentManager;
  }

  @Override
  public List<? extends ISymbol> getSymbols(
      ExpectedToken token, DocumentInformation documentInformation) {

    List list =
        documentManager
            .getAllDocumentInformation(x -> x.uri.equals(documentInformation.uri))
            .stream()
            .map(x -> x.symbols)
            .flatMap(Collection::stream)
            .filter(x -> (x instanceof CDTypeSymbol) && (((CDTypeSymbol) x).isIsClass()))
            .collect(Collectors.toList());
    return list;
  }

  @Override
  public boolean matches(ExpectedToken expectedToken) {
    return expectedToken.tokenPathMatches(TOKEN_PATH_REGEX);
  }
}
