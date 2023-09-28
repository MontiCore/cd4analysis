package de.monticore.matcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

public class CombinedMatching<T> {

  /*//A list with all matching strategies
  //Tuk zavisi dali izpolzvame za assocs ili za types
  List<MatchingStrategy<T>> matcherList = new ArrayList<>();

  List<T> listWithAllEligableMatchingCandidates = new ArrayList<>();
  //Syzdavame Map
  //Kato input vzimame lista s neshta ot cd1 deto trqbva ada match-nem
  Map<T,T> getMatchMap(List<T> cd1ToMatch) {
    Map map1 = new HashMap();
    List<T> cd2Matched = new ArrayList<>();
    for (MatchingStrategy<T> matcher : matcherList) {
      for (T srcElem : cd1ToMatch) {
        listWithAllEligableMatchingCandidates.addAll(matcher.getMatchedElements(srcElem));
        List<Pair<Integer, T>> valuesForMatchingCandidates = new ArrayList<>();
        for (T matchingCandidate : listWithAllEligableMatchingCandidates) {
          //valuesForMatchingCandidates.add(new Pair<>(computeValueForMatching(matchingCandidate), matchingCandidate));
        }
        //Narejdame gi v nizhodqsh red spored prioriteta im
        valuesForMatchingCandidates.sort(Comparator.comparing(p -> +p.a));
        //Sled kato sa naredeni, A, B, C, proverqvame dali pyrviqt e v cd2Matched,
        valuesForMatchingCandidates.removeIf(candidate -> cd2Matched.contains(candidate.b));
        cd2Matched.add(valuesForMatchingCandidates.get(0).b);


        //matcherList.removeAll(cd2Matched);

        if (!listWithAllEligableMatchingCandidates.isEmpty()) {
          //map1.entrySet(srcElem, matcherList.get(0));
          cd1ToMatch.remove(srcElem);
          //
          // cd2Matched.add(matcherList.get(0));
        }
      }
    }


  }*/
}
