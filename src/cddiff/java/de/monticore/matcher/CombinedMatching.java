package de.monticore.matcher;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

public class CombinedMatching<T> {

  //A list with all matching strategies
  //Tuk zavisi dali izpolzvame za assocs ili za types
  List<MatchingStrategy<T>> matcherList = new ArrayList<>();

  List<T> listWithAllEligableMatchingCandidates = new ArrayList<>();
  //Syzdavame Map
  //Kato input vzimame lista s neshta ot cd1 deto trqbva ada match-nem
  Map<T,T> getMatchMap(List<T> cd1ToMatch) {
    Map<T,T> map1 = new HashMap<T,T>();
    List<T> cd2Matched = new ArrayList<>();
    List<Pair<Pair<T,T>,Double>> listWithAllWeights = new ArrayList<>();

      for(T srcElem : cd1ToMatch){
        //Pylnim i osigurqvame ListWithValues
        for (MatchingStrategy<T> matcher : matcherList) {
          List<T> matchingElementsFromTgtCD = new ArrayList<>(matcher.getMatchedElements(srcElem));
          for(T matchingElem : matchingElementsFromTgtCD) {
            double weightValue = computeValueForMatching(srcElem, matchingElem);
            listWithAllWeights.add(new Pair<>(new Pair<>(srcElem, matchingElem), weightValue));
          }
        }
      }

      for(T srcElem : cd1ToMatch){
        //Vzimame chastta ot ListWithValues, koqto se otnasq za current srcElem
        List<Pair<Pair<T,T>,Double>> listWithAllWeightsForCurrentClass = new ArrayList<>();
        List<Pair<Pair<T,T>,Double>> listWithAllWeightsForOtherClasses = new ArrayList<>();
        for(Pair<Pair<T,T>,Double> x : listWithAllWeights){
          if(x.a.a.equals(srcElem)){
            listWithAllWeightsForCurrentClass.add(new Pair<>(new Pair<>(x.a.a, x.a.b),x.b));
          } else {
            listWithAllWeightsForOtherClasses.add(new Pair<>(new Pair<>(x.a.a, x.a.b),x.b));
          }
        }

        listWithAllWeightsForCurrentClass.sort(Comparator.comparing(p -> +p.b));
        T tmp = listWithAllWeightsForCurrentClass.get(0).a.b;
        double tmpWeight = listWithAllWeightsForCurrentClass.get(0).b;
        boolean notFoundBiggerValue = true;
        for(Pair<Pair<T,T>,Double> x : listWithAllWeightsForOtherClasses){
          if(tmp.equals(x.a.b)){
            if(tmpWeight < x.b){
              notFoundBiggerValue = false;
            }
          }
        }
        if(!notFoundBiggerValue){
          break;
        } else {
          if(!cd2Matched.contains(tmp)){
            cd2Matched.add(tmp);
            cd1ToMatch.remove(srcElem);
            map1.put(srcElem, tmp);
            listWithAllWeights.removeIf(x -> x.a.a.equals(listWithAllWeightsForCurrentClass.get(0).a.a));
          } else {
            break;
          }
        }
      }
      /*for (T srcElem : cd1ToMatch) {
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
      }*/
    return map1;
  }

  public Double computeValueForMatching(T srcElem, T tgtElem){
    double weight = 0;
    if(srcElem instanceof ASTCDType){
      if(((ASTCDType) srcElem).getName().equals(((ASTCDType) tgtElem).getName())){
        weight += 1;
      }

      for(ASTCDAttribute x : ((ASTCDType) srcElem).getCDAttributeList()){
        for(ASTCDAttribute y : ((ASTCDType) tgtElem).getCDAttributeList()){
          if(x.getName().equals(y.getName())){
            weight += 0.1;
          }
        }
      }
    }

    if(srcElem instanceof ASTCDAssociation){
      if(((ASTCDAssociation) srcElem).getName().equals(((ASTCDAssociation) tgtElem).getName())){
        weight += 1;
      }
    }
    return weight;
  }
}
