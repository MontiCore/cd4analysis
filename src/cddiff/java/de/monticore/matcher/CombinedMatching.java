package de.monticore.matcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;

import java.util.ArrayList;
import java.util.List;

public class CombinedMatching {

  /*List<MatchingStrategy<T>> matcherList = new ArrayList<>();

  Map<T,T> getMatchMap(List<T> cd1ToMatch){
    Map map1 = new HashMap();
    List<T> cd2Matched = new ArrayList<>();
    for(MatchingStrategy matcher : matcherList){
      for(T srcElem : cd1ToMatch){
        matcherList = matcher.getMatchedElements(srcElem);
        matcherList.removeAll(cd2Matched);

        if(!matcherList.isEmpty()){
          map1.entrySet(srcElem, matcherList.get(0));
          cd1ToMatch.remove(srcElem);
          cd2Matched.add(matcherList.get(0));
        }
      }
    }

  }*/
}
