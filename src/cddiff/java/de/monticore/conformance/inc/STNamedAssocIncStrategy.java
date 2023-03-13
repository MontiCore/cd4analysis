package de.monticore.conformance.inc;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.util.Set;
import java.util.stream.Collectors;

public class STNamedAssocIncStrategy implements IncarnationStrategy<ASTCDAssociation> {

  protected ASTCDCompilationUnit refCD;
  protected String mapping;

  public STNamedAssocIncStrategy(ASTCDCompilationUnit refCD, String mapping) {
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
    if (concrete.getModifier().isPresentStereotype()
        && concrete.getModifier().getStereotype().contains(mapping)
        && ref.isPresentName()) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      return ref.getName().equals(refName);
    }
    return false;
  }
}
