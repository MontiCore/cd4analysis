package de.monticore.conformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.conformance.inc.IncarnationStrategy;
import java.util.Set;
import java.util.stream.Collectors;

public class EqNameAssocIncStrategy implements IncarnationStrategy<ASTCDAssociation> {

  protected ASTCDCompilationUnit refCD;
  protected String mapping;

  public EqNameAssocIncStrategy(ASTCDCompilationUnit refCD, String mapping) {
    this.refCD = refCD;
    this.mapping = mapping;
  }

  @Override
  public Set<ASTCDAssociation> getRefElements(ASTCDAssociation concrete) {
    return refCD.getCDDefinition().getCDAssociationsList().stream()
        .filter(assoc -> isIncarnation(concrete, assoc))
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isIncarnation(ASTCDAssociation concrete, ASTCDAssociation ref) {
    if (concrete.isPresentName() && ref.isPresentName()) {
      return ref.getName().equals(concrete.getName());
    }
    return false;
  }
}
