/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MatchCDTypeInHierarchy implements ExternalCandidatesMatchingStrategy<ASTCDType> {

  protected BooleanMatchingStrategy<ASTCDType> typeMatcher;
  protected final ASTCDCompilationUnit srcCD;
  protected final ASTCDCompilationUnit tgtCD;

  public MatchCDTypeInHierarchy(BooleanMatchingStrategy<ASTCDType> typeMatcher, ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    this.typeMatcher = typeMatcher;
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
  }

  @Override
  public List<ASTCDType> getMatchedElements(ASTCDType srcElem) {
    List<ASTCDType> result = new ArrayList<>();

    result.addAll(tgtCD.getCDDefinition().getCDClassesList().stream().filter(type -> isMatched(
        srcElem, type)).collect(Collectors.toList()));
    result.addAll(tgtCD.getCDDefinition().getCDInterfacesList().stream().filter(type -> isMatched(
        srcElem, type)).collect(Collectors.toList()));
    result.addAll(tgtCD.getCDDefinition().getCDEnumsList().stream().filter(type -> isMatched(
        srcElem, type)).collect(Collectors.toList()));

    return result;
  }

}
