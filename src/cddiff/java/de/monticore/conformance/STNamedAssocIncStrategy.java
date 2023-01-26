package de.monticore.conformance;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDAssociationTOP;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class STNamedAssocIncStrategy implements IncarnationStrategy<ASTCDAssociation> {

  protected ASTCDCompilationUnit refCD;
  protected Set<String> mappings;

  public STNamedAssocIncStrategy(ASTCDCompilationUnit refCD, Set<String> mappings) {
    this.refCD = refCD;
  }

  @Override
  public Set<ASTCDAssociation> getRefElements(ASTCDAssociation concrete) {
    Set<ASTCDAssociation> refTypes = new HashSet<>();
    if (concrete.getModifier().isPresentStereotype()) {
      Set<String> refNames =
          mappings.stream()
              .filter(mapping -> concrete.getModifier().getStereotype().contains(mapping))
              .map(mapping -> concrete.getModifier().getStereotype().getValue(mapping))
              .collect(Collectors.toSet());
      refNames.forEach(
          name ->
              refTypes.addAll(
                  refCD.getCDDefinition().getCDAssociationsList().stream()
                      .filter(ASTCDAssociationTOP::isPresentName)
                      .filter(assoc -> assoc.getName().equals(name))
                      .collect(Collectors.toSet())));
    }
    return refTypes;
  }
}
