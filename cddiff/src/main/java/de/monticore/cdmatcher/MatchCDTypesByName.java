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

  /** Match types iff they have the same name. */
  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    return srcElem.getName().equals(tgtElem.getName());
  }
}
