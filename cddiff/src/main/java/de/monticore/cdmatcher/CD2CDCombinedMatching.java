package de.monticore.cdmatcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.util.*;
import org.antlr.v4.runtime.misc.Triple;

public abstract class CD2CDCombinedMatching<T> {
  List<MatchingStrategy<T>> matcherList;
  List<Triple<T, T, Double>> listWithAllWeights = new ArrayList<>();
  Map<T, T> matches;
  List<T> listToMatch;
  ASTCDCompilationUnit srcCD;
  ASTCDCompilationUnit tgtCD;

  public CD2CDCombinedMatching(
      List<T> listToMatch,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD,
      List<MatchingStrategy<T>> matcherList) {
    this.listToMatch = listToMatch;
    this.matcherList = matcherList;
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
    this.matches = new HashMap<>();
    getMatchMap();
  }

  public Map<T, T> getMatches() {
    return matches;
  }

  protected void fillUpWeightList() {
    for (T srcElem : listToMatch) {
      for (MatchingStrategy<T> matcher : matcherList) {
        List<T> matchingElementsFromTgtCD = new ArrayList<>(matcher.getMatchedElements(srcElem));
        for (T matchingElem : matchingElementsFromTgtCD) {
          double weightValue = computeValueForMatching(srcElem, matchingElem);
          listWithAllWeights.add(new Triple<>(srcElem, matchingElem, weightValue));
        }
      }
    }
  }

  protected void getMatchMap() {
    fillUpWeightList();
    List<T> foundSource = new ArrayList<>();
    List<T> foundTarget = new ArrayList<>();
    listWithAllWeights.sort(Comparator.comparing(p -> -p.c));
    for (Triple<T, T, Double> x : listWithAllWeights) {
      if (x.a.equals(x.b)) {
        matches.put(x.a, x.b);
        foundSource.add(x.a);
        foundTarget.add(x.b);
      }
    }

    for (Triple<T, T, Double> x : listWithAllWeights) {
      if (!foundSource.contains(x.a) && !foundTarget.contains(x.b)) {
        matches.put(x.a, x.b);
        foundSource.add(x.a);
        foundTarget.add(x.b);
      }
    }
  }

  public abstract Double computeValueForMatching(T srcElem, T tgtElem);
}
