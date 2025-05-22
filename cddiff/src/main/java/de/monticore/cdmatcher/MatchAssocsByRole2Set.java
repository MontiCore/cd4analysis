package de.monticore.cdmatcher;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MatchAssocsByRole2Set extends MatchCDAssocsBySrcTypeAndTgtRole{

  protected LinkedHashSet<ASTCDAssociation> tgtSet;

  public MatchAssocsByRole2Set(MatchingStrategy<ASTCDType> typeMatcher, ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, Collection<ASTCDAssociation> tgtSet) {
    super(typeMatcher,srcCD,tgtCD);
    this.tgtSet = new LinkedHashSet<>(tgtSet);
  }

  @Override
  public List<ASTCDAssociation> getMatchedElements(ASTCDAssociation srcElem) {
    return tgtSet.stream()
      .filter(assoc -> isMatched(srcElem, assoc))
      .collect(Collectors.toList());
  }
}
