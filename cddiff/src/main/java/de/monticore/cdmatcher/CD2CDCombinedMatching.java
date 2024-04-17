package de.monticore.cdmatcher;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import java.util.*;
import org.antlr.v4.runtime.misc.Triple;

public class CD2CDCombinedMatching<T> {
  List<MatchingStrategy<T>> matcherList;
  List<Triple<T, T, Double>> listWithAllWeights = new ArrayList<>();
  Map<T, T> finalMap;
  List<T> cd1ToMatch;
  ASTCDCompilationUnit srcCD;
  ASTCDCompilationUnit tgtCD;

  public CD2CDCombinedMatching(
      List<T> listToMatch,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD,
      List<MatchingStrategy<T>> matcherList) {
    this.cd1ToMatch = listToMatch;
    this.matcherList = matcherList;
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
    getMatchMap();
  }

  public Map<T, T> getFinalMap() {
    return finalMap;
  }

  private void fillUpWeightList() {
    for (T srcElem : cd1ToMatch) {
      for (MatchingStrategy<T> matcher : matcherList) {
        List<T> matchingElementsFromTgtCD = new ArrayList<>(matcher.getMatchedElements(srcElem));
        for (T matchingElem : matchingElementsFromTgtCD) {
          double weightValue = computeValueForMatching(srcElem, matchingElem);
          listWithAllWeights.add(new Triple<>(srcElem, matchingElem, weightValue));
        }
      }
    }
  }

  private void getMatchMap() {
    fillUpWeightList();
    Map<T, T> map1 = new HashMap<>();
    List<T> foundSource = new ArrayList<>();
    List<T> foundTarget = new ArrayList<>();
    listWithAllWeights.sort(Comparator.comparing(p -> -p.c));
    for (Triple<T, T, Double> x : listWithAllWeights) {
      if (x.a.equals(x.b)) {
        map1.put(x.a, x.b);
        foundSource.add(x.a);
        foundTarget.add(x.b);
      }
    }

    for (Triple<T, T, Double> x : listWithAllWeights) {
      if (!foundSource.contains(x.a) && !foundTarget.contains(x.b)) {
        map1.put(x.a, x.b);
        foundSource.add(x.a);
        foundTarget.add(x.b);
      }
    }
    this.finalMap = map1;
  }

  public Double computeValueForMatching(T srcElem, T tgtElem) {
    if (srcElem instanceof ASTCDType && tgtElem instanceof ASTCDType) {
      return computeValueForMatching((ASTCDType) srcElem, (ASTCDType) tgtElem);
    }
    if (srcElem instanceof ASTCDAssociation && tgtElem instanceof ASTCDAssociation) {
      return computeValueForMatching(
          (ASTCDAssociation) srcElem, (ASTCDAssociation) tgtElem, srcCD, tgtCD);
    }

    return null;
  }

  public Double computeValueForMatching(ASTCDType srcElem, ASTCDType tgtElem) {
    double weight = 0;
    List<ASTCDAttribute> srcAttr = new ArrayList<>(srcElem.getCDAttributeList());
    List<ASTCDAttribute> tgtAttr = new ArrayList<>(tgtElem.getCDAttributeList());

    List<ASTCDAttribute> tgtAttrDeletedAttr = new ArrayList<>(tgtAttr);
    List<ASTCDAttribute> similarities = new ArrayList<>();

    for (ASTCDAttribute x : srcAttr) {
      for (ASTCDAttribute y : tgtAttr) {
        if (x.getName().equals(y.getName())) {
          tgtAttrDeletedAttr.remove(y);
          similarities.add(x);
        }
      }
    }

    List<ASTCDAttribute> allAttributes = new ArrayList<>(srcAttr);
    allAttributes.addAll(tgtAttrDeletedAttr);

    // Jaccard Index
    if (srcElem.getName().equals(tgtElem.getName())) {
      weight = (double) (similarities.size() + 2) / allAttributes.size();
    } else {
      weight = (double) similarities.size() / allAttributes.size();
    }

    return weight;
  }

  public Double computeValueForMatching(
      ASTCDAssociation srcElem,
      ASTCDAssociation tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    double weight = 0;

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
          weight += computeValueForMatching(srcTypeLeft, tgtTypeLeft);
          weight += computeValueForMatching(srcTypeRight, tgtTypeRight);
        } else {
          weight += computeValueForMatching(srcTypeLeft, tgtTypeRight);
          weight += computeValueForMatching(srcTypeRight, tgtTypeLeft);
        }
      }
    }
    return weight;
  }
}
