package de.monticore.cdmatcher;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CombinedCDAssocMatching extends CD2CDCombinedMatching<ASTCDAssociation> {
  public CombinedCDAssocMatching(
      List<ASTCDAssociation> listToMatch,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD,
      List<MatchingStrategy<ASTCDAssociation>> matcherList) {
    super(listToMatch, srcCD, tgtCD, matcherList);
  }

  public Double computeValueForMatching(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    double weight = 0;

    List<MatchingStrategy<ASTCDType>> matcherList = initMatching4AssocTypes(srcCD, tgtCD);
    CDTypeSimilarity typeSimilarity = new CDTypeSimilarity();

    Optional<CDTypeSymbol> srcTypeSymbolLeft =
        srcCD.getEnclosingScope().resolveCDTypeDown(srcElem.getLeftQualifiedName().getQName());
    Optional<CDTypeSymbol> srcTypeSymbolRight =
        srcCD.getEnclosingScope().resolveCDTypeDown(srcElem.getRightQualifiedName().getQName());
    Optional<CDTypeSymbol> tgtTypeSymbolLeft =
        tgtCD.getEnclosingScope().resolveCDTypeDown(tgtElem.getLeftQualifiedName().getQName());
    Optional<CDTypeSymbol> tgtTypeSymbolRight =
        tgtCD.getEnclosingScope().resolveCDTypeDown(tgtElem.getRightQualifiedName().getQName());

    boolean isReversed = false;

    if (srcTypeSymbolLeft.isPresent()
        && srcTypeSymbolRight.isPresent()
        && tgtTypeSymbolLeft.isPresent()
        && tgtTypeSymbolRight.isPresent()) {
      ASTCDType srcTypeLeft = srcTypeSymbolLeft.get().getAstNode();
      ASTCDType tgtTypeLeft = tgtTypeSymbolLeft.get().getAstNode();
      ASTCDType srcTypeRight = srcTypeSymbolRight.get().getAstNode();
      ASTCDType tgtTypeRight = tgtTypeSymbolRight.get().getAstNode();

      for (MatchingStrategy<ASTCDType> x : matcherList) {
        if (x.isMatched(srcTypeLeft, tgtTypeRight) || x.isMatched(srcTypeRight, tgtTypeLeft)) {
          isReversed = true;
        }

        if (!isReversed) {
          weight += typeSimilarity.computeWeight(srcTypeLeft, tgtTypeLeft);
          weight += typeSimilarity.computeWeight(srcTypeRight, tgtTypeRight);
        } else {
          weight += typeSimilarity.computeWeight(srcTypeLeft, tgtTypeRight);
          weight += typeSimilarity.computeWeight(srcTypeRight, tgtTypeLeft);
        }
      }
    }
    return weight;
  }

  protected List<MatchingStrategy<ASTCDType>> initMatching4AssocTypes(
      ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    List<MatchingStrategy<ASTCDType>> matcherList = new ArrayList<>();
    MatchCDTypesByName nameTypeMatch = new MatchCDTypesByName(tgtCD);
    MatchCDTypeByStructure structureTypeMatch = new MatchCDTypeByStructure(tgtCD);
    MatchCDTypesToSuperTypes superTypeMatchName =
        new MatchCDTypesToSuperTypes(nameTypeMatch, srcCD, tgtCD);
    MatchCDTypesToSuperTypes superTypeMatchStructure =
        new MatchCDTypesToSuperTypes(structureTypeMatch, srcCD, tgtCD);
    matcherList.add(nameTypeMatch);
    matcherList.add(structureTypeMatch);
    matcherList.add(superTypeMatchName);
    matcherList.add(superTypeMatchStructure);
    return matcherList;
  }
}
