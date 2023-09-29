package de.monticore.matcher;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import org.antlr.v4.runtime.misc.Triple;

import java.util.*;

import static com.google.common.collect.Sets.intersection;


public class CombinedMatching<T> {

  //A list with all matching strategies
  //Tuk zavisi dali izpolzvame za assocs ili za types
  List<MatchingStrategy<T>> matcherList = new ArrayList<>();
  List<Triple<T,T,Double>> listWithAllWeights = new ArrayList<>();
  Map<T,T> finalMap;
  List<T> cd1ToMatch;
  public CombinedMatching(List<T> listToMatch){
    this.cd1ToMatch = listToMatch;
    fillUpWeightList();
    getMatchMap();
  }

  public Map<T,T> getFinalMap(){
    return finalMap;
  }

  private void fillUpWeightList(){
    for(T srcElem : cd1ToMatch){
      //Pylnim i osigurqvame listWithAllWeights
      for (MatchingStrategy<T> matcher : matcherList) {
        List<T> matchingElementsFromTgtCD = new ArrayList<>(matcher.getMatchedElements(srcElem));
        for(T matchingElem : matchingElementsFromTgtCD) {
          double weightValue = computeValueForMatching(srcElem, matchingElem);
          listWithAllWeights.add(new Triple<>(srcElem, matchingElem, weightValue));
        }
      }
    }
  }

  //Syzdavame Map
  //Kato input vzimame lista s neshta ot cd1 deto trqbva ada match-nem
  private void getMatchMap() {
      Map<T,T> map1 = new HashMap<>();
      List<T> foundSource = new ArrayList<>();
      List<T> foundTarget = new ArrayList<>();
      listWithAllWeights.sort(Comparator.comparing(p -> -p.c));
      for(Triple<T,T,Double> x : listWithAllWeights){
        if(x.a.equals(x.b)){
          map1.put(x.a,x.b);
          foundSource.add(x.a);
          foundTarget.add(x.b);
        }
      }

      for(Triple<T,T,Double> x : listWithAllWeights){
        if(!foundSource.contains(x.a) && !foundTarget.contains(x.b)){
          map1.put(x.a,x.b);
          foundSource.add(x.a);
          foundTarget.add(x.b);
        }
      }
      /*somethingWasChanged = false;

      for(T srcElem : cd1ToMatch){
        //Vzimame chastta ot ListWithValues, koqto se otnasq za current srcElem
        //List<Pair<Pair<T,T>,Double>> listWithAllWeightsForCurrentClass = new ArrayList<>();
        List<Triple<T,T,Double>> listWithAllWeightsForCurrentClass = new ArrayList<>();
        //List<Pair<Pair<T,T>,Double>> listWithAllWeightsForOtherClasses = new ArrayList<>();
        List<Triple<T,T,Double>> listWithAllWeightsForOtherClasses = new ArrayList<>();
        for(Triple<T,T,Double> x : listWithAllWeights){
          if(x.a.equals(srcElem)){
            listWithAllWeightsForCurrentClass.add(new Triple<>(x.a,x.b,x.c));
          } else {
            listWithAllWeightsForOtherClasses.add(new Triple<>(x.a,x.b,x.c));
          }
        }

        listWithAllWeightsForCurrentClass.sort(Comparator.comparing(p -> -p.c));
        T tmp = listWithAllWeightsForCurrentClass.get(0).b;
        double tmpWeight = listWithAllWeightsForCurrentClass.get(0).c;
        boolean notFoundBiggerValue = true;
        for(Triple<T,T,Double> x : listWithAllWeightsForOtherClasses){
          if(tmp.equals(x.b)){
            if(tmpWeight < x.c){
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
            listWithAllWeights.removeIf(x -> x.a.equals(listWithAllWeightsForCurrentClass.get(0).a));
            somethingWasChanged = true;
          } else {
            break;
          }
        }
      }*/
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
      this.finalMap = map1;
  }

  public Double computeValueForMatching(T srcElem, T tgtElem){
    double weight = 0;
    if(srcElem instanceof ASTCDType){
        Set<ASTCDAttribute> tgtAttr = new HashSet<>(((ASTCDType) tgtElem).getCDAttributeList());
        Set<ASTCDAttribute> srcAttr = new HashSet<>(((ASTCDType) srcElem).getCDAttributeList());

        Set<ASTCDAttribute> allAttributes = new HashSet<>();
        allAttributes.addAll(tgtAttr);
        allAttributes.addAll(srcAttr);
        srcAttr.retainAll(tgtAttr);
        weight += (double) srcAttr.size() / allAttributes.size();
    }
    return weight;
  }

}
