package de.monticore.conformance.inc.type;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.conformance.inc.IncarnationStrategy;
import java.util.HashSet;
import java.util.Set;

public class CompTypeIncStrategy implements IncarnationStrategy<ASTCDType> {
  protected ASTCDCompilationUnit refCD;
  protected String mapping;

  protected EqTypeIncStrategy eqTypeIncStrategy;
  protected STTypeIncStrategy stTypeIncStrategy;

  public CompTypeIncStrategy(ASTCDCompilationUnit refCD, String mapping) {
    this.eqTypeIncStrategy = new EqTypeIncStrategy(refCD, mapping);
    this.stTypeIncStrategy = new STTypeIncStrategy(refCD, mapping);
    this.refCD = refCD;
    this.mapping = mapping;
  }

  @Override
  public Set<ASTCDType> getRefElements(ASTCDType concrete) {
    Set<ASTCDType> refElements = new HashSet<>(stTypeIncStrategy.getRefElements(concrete));
    if (refElements.isEmpty()) {
      refElements.addAll(eqTypeIncStrategy.getRefElements(concrete));
    }
    return refElements;
  }

  @Override
  public boolean isIncarnation(ASTCDType concrete, ASTCDType ref) {
    return getRefElements(concrete).contains(ref);
  }
}
