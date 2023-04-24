package de.monticore.conformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.conformance.inc.IncarnationStrategy;
import java.util.HashSet;
import java.util.Set;

public class CompAssocIncStrategy implements IncarnationStrategy<ASTCDAssociation> {
  protected ASTCDCompilationUnit refCD;
  protected String mapping;

  protected EqNameAssocIncStrategy eqNameAssocIncStrategy;
  protected STNamedAssocIncStrategy stNamedAssocIncStrategy;

  public CompAssocIncStrategy(ASTCDCompilationUnit refCD, String mapping) {
    this.eqNameAssocIncStrategy = new EqNameAssocIncStrategy(refCD, mapping);
    this.stNamedAssocIncStrategy = new STNamedAssocIncStrategy(refCD, mapping);
    this.refCD = refCD;
    this.mapping = mapping;
  }

  @Override
  public Set<ASTCDAssociation> getRefElements(ASTCDAssociation concrete) {
    Set<ASTCDAssociation> refElements =
        new HashSet<>(stNamedAssocIncStrategy.getRefElements(concrete));
    if (refElements.isEmpty()) {
      refElements.addAll(eqNameAssocIncStrategy.getRefElements(concrete));
    }
    return refElements;
  }

  @Override
  public boolean isIncarnation(ASTCDAssociation concrete, ASTCDAssociation ref) {
    return getRefElements(concrete).contains(ref);
  }
}
