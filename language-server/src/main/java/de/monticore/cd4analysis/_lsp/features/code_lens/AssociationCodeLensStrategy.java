package de.monticore.cd4analysis._lsp.features.code_lens;

import de.mclsg.PositionUtils;
import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.features.code_lens.CodeLensStrategy;
import de.mclsg.lsp.features.reference.CommonReferencesProvider;
import de.mclsg.parser.MatchedToken;
import de.monticore.cd4analysis._lsp.code_lens.CD4AnalysisServerCommandCodeLens;
import de.monticore.cd4analysis._parser.CD4AnalysisParserInfo;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.lsp4j.CodeLens;

public class AssociationCodeLensStrategy implements CodeLensStrategy {
  private final CommonReferencesProvider referencesProvider;
  private final DocumentManager documentManager;
  private final ISymbolUsageResolutionProvider symbolUsageResolutionProvider;

  public AssociationCodeLensStrategy(
      CommonReferencesProvider referencesProvider,
      DocumentManager documentManager,
      ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
    this.referencesProvider = referencesProvider;
    this.documentManager = documentManager;
    this.symbolUsageResolutionProvider = symbolUsageResolutionProvider;
  }

  @Override
  public boolean matches(MatchedToken matchedToken) {
    return CD4AnalysisParserInfo.stateDefinesName(matchedToken.parserState)
        && matchedToken.tokenPathMatches(".*.cDType.cDClass");
  }

  @Override
  public Optional<CodeLens> apply(MatchedToken matchedToken) {
    Optional<DocumentInformation> documentInformation =
        documentManager.getDocumentInformation(matchedToken.uri);
    if (documentInformation.isEmpty()) return Optional.empty();

    return symbolUsageResolutionProvider
        .getSymbols(documentInformation.get(), matchedToken)
        .stream()
        .filter(CDTypeSymbol.class::isInstance)
        .map(CDTypeSymbol.class::cast)
        .map(
            symbol -> {
              List<MatchedToken> associationTokens =
                  Stream.concat(
                          getSuperTypeMatchedTokens(symbol),
                          Stream.of(Pair.of(matchedToken, symbol)))
                      .flatMap(
                          pair ->
                              referencesProvider
                                  .getReferencingTokens(pair.getKey(), pair.getValue(), false)
                                  .stream())
                      .filter(
                          referencingToken ->
                              referencingToken.tokenPathMatches(
                                  ".*.cDAssoc(Left|Right)Side.mCQualifiedType.mCQualifiedName"))
                      .collect(Collectors.toList());

              if (associationTokens.isEmpty()) return null;

              String title =
                  "Part of "
                      + associationTokens.size()
                      + " Association"
                      + (associationTokens.size() > 1 ? "s" : "");
              return new CD4AnalysisServerCommandCodeLens(matchedToken.range, title, "", List.of());
            })
        .filter(Objects::nonNull)
        .map(cl -> (CodeLens) cl)
        .findFirst();
  }

  private Stream<Pair<MatchedToken, TypeSymbol>> getSuperTypeMatchedTokens(CDTypeSymbol symbol) {
    return symbol.getSuperTypesList().stream()
        .map(SymTypeExpression::getTypeInfo)
        .flatMap(
            typeSymbol ->
                documentManager
                    .getLocation(typeSymbol)
                    .flatMap(documentManager::getDocumentInformation)
                    .flatMap(
                        documentInformation ->
                            documentInformation.getMatchedToken(
                                PositionUtils.toPosition(typeSymbol.getSourcePosition())))
                    .map(matchedToken -> Pair.of(matchedToken, typeSymbol))
                    .stream());
  }
}
