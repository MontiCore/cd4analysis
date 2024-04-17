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

  // TODO: vij kyde moje da izpolyvash getMatchedElements iz koda
  /**
   * A set for the matched elements which can be per definition modified
   *
   * @return all elements which have been matched
   */
  @Override
  public List<ASTCDAssociation> getMatchedElements(ASTCDAssociation srcElem) {
    return tgtCD.getCDDefinition().getCDAssociationsList().stream()
        .filter(assoc -> isMatched(srcElem, assoc))
        .collect(Collectors.toList());
  }

  /**
   * A boolean method which gives if the name of the assoc from srcCD is matched with the name of
   * the assoc from tgtCD
   *
   * @param srcElem The assoc which we pick from the class diagram
   * @param tgtElem The assoc which we pick from the class diagram
   * @return true if both types have the same name
   */
  @Override
  public boolean isMatched(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    if (tgtElem.isPresentName() && srcElem.isPresentName()) {
      return tgtElem.getName().equals(srcElem.getName());
    }
    return false;
  }
}
