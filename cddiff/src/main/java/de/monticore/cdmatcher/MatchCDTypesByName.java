package de.monticore.cdmatcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MatchCDTypesByName implements MatchingStrategy<ASTCDType> {

  private final ASTCDCompilationUnit tgtCD;

  public MatchCDTypesByName(ASTCDCompilationUnit tgtCD) {
    this.tgtCD = tgtCD;
  }

  /**
   * A set for the matched elements which can be per definition modified
   *
   * @return all elements which have been matched
   */
  @Override
  public List<ASTCDType> getMatchedElements(ASTCDType srcElem) {
    List<ASTCDType> result = new ArrayList<>();

    result.addAll(
        tgtCD.getCDDefinition().getCDClassesList().stream()
            .filter(type -> isMatched(srcElem, type))
            .collect(Collectors.toList()));
    result.addAll(
        tgtCD.getCDDefinition().getCDInterfacesList().stream()
            .filter(type -> isMatched(srcElem, type))
            .collect(Collectors.toList()));
    result.addAll(
        tgtCD.getCDDefinition().getCDEnumsList().stream()
            .filter(type -> isMatched(srcElem, type))
            .collect(Collectors.toList()));

    return result;
  }

  /**
   * A boolean method which gives if the name of the tgterence type from tgtCD is matched with the
   * name of the srccrete type from srcCD
   *
   * @param srcElem The tgterence type which we pick from the tgterence class diagram
   * @param tgtElem The srccrete type which we pick from the srccrete class diagram
   * @return true if both types have the same name
   */
  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    if (srcElem.getName().equals(tgtElem.getName())) {
      return true;
    }
    return false;
  }
}
