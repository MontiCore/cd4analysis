package de.monticore.conformance.inc;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.util.Set;
import java.util.stream.Collectors;

public class STNamedAssocIncStrategy implements IncarnationStrategy<ASTCDAssociation> {

  protected ASTCDCompilationUnit refCD;
  protected Set<String> mappings;

  public STNamedAssocIncStrategy(ASTCDCompilationUnit refCD, Set<String> mappings) {
    this.refCD = refCD;
    this.mappings = mappings;
  }

  @Override
  public Set<ASTCDAssociation> getRefElements(ASTCDAssociation concrete) {
    return refCD.getCDDefinition().getCDAssociationsList().stream()
        .filter(assoc -> isInstance(concrete, assoc))
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isInstance(ASTCDAssociation concrete, ASTCDAssociation ref) {
    if (concrete.getModifier().isPresentStereotype() && ref.isPresentName()) {
      Set<String> refNames =
          mappings.stream()
              .filter(mapping -> concrete.getModifier().getStereotype().contains(mapping))
              .map(mapping -> concrete.getModifier().getStereotype().getValue(mapping))
              .collect(Collectors.toSet());
      return refNames.stream().anyMatch(name -> ref.getName().equals(name));
    }
    return false;
  }
}
