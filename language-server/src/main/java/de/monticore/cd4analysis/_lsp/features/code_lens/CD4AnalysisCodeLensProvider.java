package de.monticore.cd4analysis._lsp.features.code_lens;

import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.features.code_lens.CodeLensStrategy;
import de.mclsg.lsp.features.reference.CommonReferencesProvider;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.TextDocumentItem;

public class CD4AnalysisCodeLensProvider extends CD4AnalysisCodeLensProviderTOP {
  private final DocumentManager documentManager;

  public CD4AnalysisCodeLensProvider(DocumentManager documentManager) {
    super(documentManager);
    throw new IllegalStateException();
  }

  public CD4AnalysisCodeLensProvider(
      CommonReferencesProvider referencesProvider,
      DocumentManager documentManager,
      ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
    super(documentManager);
    this.documentManager = documentManager;
    this.strategyManager.registerCodeLensStrategy(
        new AssociationCodeLensStrategy(
            referencesProvider, documentManager, symbolUsageResolutionProvider));
  }

  public void registerCodeLensStrategy(CodeLensStrategy codeLensStrategy) {
    strategyManager.registerCodeLensStrategy(codeLensStrategy);
  }

  @Override
  public List<? extends CodeLens> codeLens(TextDocumentItem document) {
    Optional<DocumentInformation> documentInformation =
        documentManager.getDocumentInformation(document);
    if (documentInformation.isEmpty()) return List.of();

    return documentInformation.get().getMatchedNameTokens().stream()
        .map(
            matchedToken ->
                strategyManager.getCodeLensStrategies(matchedToken).stream()
                    .map(codeLensStrategy -> codeLensStrategy.apply(matchedToken))
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList()))
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }
}
