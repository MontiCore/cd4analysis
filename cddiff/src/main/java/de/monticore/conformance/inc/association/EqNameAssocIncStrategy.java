package de.monticore.conformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.matcher.MatchingStrategy;
import java.util.List;
import java.util.stream.Collectors;

public class EqNameAssocIncStrategy implements MatchingStrategy<ASTCDAssociation> {

  protected ASTCDCompilationUnit refCD;
  protected String mapping;

  public EqNameAssocIncStrategy(ASTCDCompilationUnit refCD, String mapping) {
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
    if (concrete.isPresentName() && ref.isPresentName()) {
      return ref.getName().equals(concrete.getName());
    }
    return false;
  }
}
