package de.monticore.conformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.matcher.MatchingStrategy;

import java.util.List;
import java.util.stream.Collectors;

public class STNamedAssocIncStrategy implements MatchingStrategy<ASTCDAssociation> {

  protected ASTCDCompilationUnit refCD;
  protected String mapping;

  public STNamedAssocIncStrategy(ASTCDCompilationUnit refCD, String mapping) {
    this.refCD = refCD;
    this.mapping = mapping;
  }

  @Override
  public List<ASTCDAssociation> getMatchedElements(ASTCDAssociation concrete) {
    return refCD.getCDDefinition().getCDAssociationsList().stream()
        .filter(assoc -> isMatched(concrete, assoc))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDAssociation concrete, ASTCDAssociation ref) {
    if (concrete.getModifier().isPresentStereotype()
        && concrete.getModifier().getStereotype().contains(mapping)
        && ref.isPresentName()) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      return ref.getName().equals(refName);
    }
    return false;
  }
}
