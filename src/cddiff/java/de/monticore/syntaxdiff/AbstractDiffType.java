package de.monticore.syntaxdiff;

import de.monticore.cdbasis._ast.ASTCDClass;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public abstract class AbstractDiffType {
  protected static <T> List<T> absentElementList(List<ElementDiff<T>> matchs, List<T> elementList){
    List<T> output = new ArrayList<>();
    for (T element : elementList){
      boolean found = false;
      for (ElementDiff<T> diff : matchs) {
        if (diff.getCd1Element().equals(element) || diff.getCd2Element().equals(element)) {
          found = true;
          break;
        }
      }
      if (!found) {
        output.add(element);
      }
    }
    return output;
  }
  // Match Function, which return three lists: matched Elements, deleted(from cd1), added(in cd2)
  protected static <T> List<ElementDiff<T>> getMatchingList(List<List<ElementDiff<T>>> elementsDiffList){
    List<T> cd1matchedElements = new ArrayList<>();
    List<T> cd2matchedElements = new ArrayList<>();
    List<ElementDiff<T>> matchedElements = new ArrayList<>();

    for (List<ElementDiff<T>> currentElementList: elementsDiffList){
      double threshold = 0;
      OptionalDouble optAverage = currentElementList.stream()
        .mapToDouble(ElementDiff::getDiffSize)
        .average();
      if (optAverage.isPresent()) {
        threshold = (1 / (double) (currentElementList.size()+1))+optAverage.getAsDouble() / 2;
      }
      if (!currentElementList.isEmpty()) {
        for (ElementDiff<T> currentElementDiff : currentElementList){
          T cd1Element = currentElementDiff.getCd1Element();
          T cd2Element = currentElementDiff.getCd2Element();
          if (!cd1matchedElements.contains(cd1Element) && !cd2matchedElements.contains(cd2Element)){
            // Todo: Check if there is a match to the target attribute with a smaller diff size
            if (currentElementDiff.getDiffSize() <= threshold){
              matchedElements.add(currentElementDiff);
              cd1matchedElements.add(cd1Element);
              cd2matchedElements.add(cd2Element);
              break;
            }
          }
        }
      }
    }
    return matchedElements;
  }


  protected static <T> double calculatedThreshold(List<ElementDiff<T>> list){
    OptionalDouble optAverage = list.stream()
      .mapToDouble(ElementDiff::getDiffSize)
      .average();
    return optAverage.isPresent() ? optAverage.getAsDouble() / 2 : 0;
  }

}
