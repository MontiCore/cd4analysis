package de.monticore.cd4analysis._lsp.features.code_action;

import java.util.Optional;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

public interface CodeActionStrategy {
  Optional<Either<Command, CodeAction>> apply(
      TextDocumentItem document, CodeActionContext context, Range range);
}
