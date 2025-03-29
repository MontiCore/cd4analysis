package de.monticore.cdconcretization.cd;

import static de.monticore.cdconformance.CDConfParameter.*;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.AbstractCDCompleter;
import de.monticore.cdconcretization.CompletionContext;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconformance.CDConformanceChecker;
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

  public ConformanceCheckCompletionStep(String mapping, String errorMessage) {
    this.mapping = mapping;
    this.errorMessage = errorMessage;
    this.conformanceChecker =
        new CDConformanceChecker(
            Set.of(
                STEREOTYPE_MAPPING,
                NAME_MAPPING,
                SRC_TARGET_ASSOC_MAPPING,
                INHERITANCE,
                ALLOW_CARD_RESTRICTION));
  }

  @Override
  public void complete(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, CompletionContext context)
      throws CompletionException {
    if (!conformanceChecker.checkConformance(concreteCD, referenceCD, mapping)) {
      // The association completion result is not conform
      throw new CompletionException(errorMessage);
    }
    super.complete(concreteCD, referenceCD, context);
  }
}
