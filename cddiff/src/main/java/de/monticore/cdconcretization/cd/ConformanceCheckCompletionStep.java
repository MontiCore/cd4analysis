package de.monticore.cdconcretization.cd;

import static de.monticore.cdconformance.CDConfParameter.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconformance.CDConformanceChecker;
import de.se_rwth.commons.logging.Log;
import java.util.Set;

/**
 * Completer that checks the conformance of a CD with a reference CD. This can be used as a last
 * step in the completion process to ensure that the completion result is conform to the reference
 * CD.
 */
public class ConformanceCheckCompletionStep extends AbstractCDCompleter {

  private final String mapping;

  private final String errorMessage;
  private final CDConformanceChecker conformanceChecker;

  public ConformanceCheckCompletionStep(String mapping, Set<CDConfParameter> params,  String errorMessage) {
    this.mapping = mapping;
    this.errorMessage = errorMessage;
    this.conformanceChecker = new CDConformanceChecker(params);
  }

  @Override
  public void complete(
      ASTCDCompilationUnit concreteCD,
      ASTCDCompilationUnit referenceCD,
      CDCompletionContext context)
      throws CompletionException {
    if (!conformanceChecker.checkConformance(concreteCD, referenceCD, mapping)) {
      Log.warn("Conformance check failed");
      Log.warn("Concretized CD:");
      Log.warn(CD4CodeMill.prettyPrint(concreteCD, false));
      throw new CompletionException(errorMessage);
    }
    super.complete(concreteCD, referenceCD, context);
  }
}
