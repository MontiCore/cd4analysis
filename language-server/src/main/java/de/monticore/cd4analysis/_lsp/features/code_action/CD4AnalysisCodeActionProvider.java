package de.monticore.cd4analysis._lsp.features.code_action;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.AstPrettyPrinter;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

public class CD4AnalysisCodeActionProvider extends CD4AnalysisCodeActionProviderTOP {
  private final List<CodeActionStrategy> strategies;

  public CD4AnalysisCodeActionProvider(
      DocumentManager documentManager, AstPrettyPrinter<ASTCDCompilationUnit> prettyPrinter) {
    super(documentManager, prettyPrinter);
    this.strategies = new ArrayList<>();
  }

  public void addCodeActionStrategy(CodeActionStrategy codeActionStrategy) {
    strategies.add(codeActionStrategy);
  }

  @Override
  public List<Either<Command, CodeAction>> codeAction(
      TextDocumentItem document, CodeActionContext context, Range range) {
    List<Either<Command, CodeAction>> baseActions = super.codeAction(document, context, range);

    strategies.stream()
        .flatMap(strategy -> strategy.apply(document, context, range).stream())
        .forEach(baseActions::add);

    return baseActions;
  }
}
