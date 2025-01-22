package de.monticore.cdmatcher;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.util.List;
import java.util.stream.Collectors;

public class MatchCDAssocsByName implements MatchingStrategy<ASTCDAssociation> {

  private final ASTCDCompilationUnit tgtCD;

  public MatchCDAssocsByName(ASTCDCompilationUnit tgtCD) {
    this.tgtCD = tgtCD;
  }

  @Override
  public List<ASTCDAssociation> getMatchedElements(ASTCDAssociation srcElem) {
    return tgtCD.getCDDefinition().getCDAssociationsList().stream()
        .filter(assoc -> isMatched(srcElem, assoc))
        .collect(Collectors.toList());
  }

  /** Match two associations iff their names are present and equal. */
  @Override
  public boolean isMatched(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    if (tgtElem.isPresentName() && srcElem.isPresentName()) {
      return tgtElem.getName().equals(srcElem.getName());
    }
    return false;
  }
}
