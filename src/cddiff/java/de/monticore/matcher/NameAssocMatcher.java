package de.monticore.matcher;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.util.Set;
import java.util.stream.Collectors;

public class NameAssocMatcher implements MatchingStrategy<ASTCDAssociation> {
  /**
   * A set for the matched elements which can be per definition modified
   *
   * @return all elements which have been matched
   */
  @Override
  public Set<ASTCDAssociation> getMatchedElements(
      ASTCDAssociation srcElem, ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    return tgtCD.getCDDefinition().getCDAssociationsList().stream()
        .filter(assoc -> isMatched(srcElem, assoc, srcCD, tgtCD))
        .collect(Collectors.toSet());
  }

  /**
   * A boolean method which gives if the name of the assoc from srcCD is matched with the name of
   * the assoc from tgtCD
   *
   * @param srcElem The assoc which we pick from the class diagram
   * @param tgtElem The assoc which we pick from the class diagram
   * @param srcCD The class diagram is the new class diagram
   * @param tgtCD The class diagram is the old class diagram
   * @return true if both types have the same name
   */
  @Override
  public boolean isMatched(
      ASTCDAssociation srcElem,
      ASTCDAssociation tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    if (tgtElem.isPresentName() && srcElem.isPresentName()) {
      if (tgtElem.getName().equals(srcElem.getName())) {
        return true;
      } else {
        System.out.println("Association names do not match!");
      }
    }
    return false;
  }
}
