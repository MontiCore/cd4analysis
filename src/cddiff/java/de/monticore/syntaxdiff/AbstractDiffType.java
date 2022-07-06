package de.monticore.syntaxdiff;

import de.monticore.ast.ASTNode;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public abstract class AbstractDiffType {

  protected static final String COLOR_DELETE = "\033[1;31m";
  protected static final String COLOR_ADD = "\033[1;32m";
  protected static final String COLOR_CHANGE = "\033[1;33m";
  protected static final String RESET = "\033[0m";

  /**
   * Methode for calculating a list of elements which are not included in any match provided by the matchs list.
   * @param matchs List of matches between elements with the ElementDiff type
   * @param elementList List of elements to be reduced e.g. List of Attributes from a CD Class
   * @return Reduced list of type provided as elementList
   * @param <T> Type of the element, e.g. Classes
   */
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

  /**
   * Methode to reduce a given list of potential matches between elements to at most one match for each entry
   * @param elementsDiffList List of diffs between one element of the first model and every element of the same type from the second model
   * @return Reduced list of matches for elements between two models
   * @param <T> Type of the element, e.g. Classes
   */
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
          T currentcd1Element = currentElementDiff.getCd1Element();
          T currentcd2Element = currentElementDiff.getCd2Element();
          if (!cd1matchedElements.contains(currentcd1Element) && !cd2matchedElements.contains(currentcd2Element)){
            // Todo: Check if there is a match to the target attribute with a smaller diff size
            if (currentElementDiff.getDiffSize() <= threshold){
              matchedElements.add(currentElementDiff);
              cd1matchedElements.add(currentcd1Element);
              cd2matchedElements.add(currentcd2Element);
              break;
            }
          }
        }
      }
    }
    return matchedElements;
  }

  /**
   * Helper function to determine the color code according to the operation recognized
   * @param diff Fielddiff which contains the operation from the lowest level(fields)
   * @return Color code as String (Set this String directly before the to-be-colored String)
   */
  protected static String getColorCode(FieldDiff<? extends ASTNode> diff){
    if (diff.getOperation().isPresent()) {
      if (diff.getOperation().get().equals(SyntaxDiff.Op.DELETE)) {
        return COLOR_DELETE;
      }else if (diff.getOperation().get().equals(SyntaxDiff.Op.ADD)) {
        return COLOR_ADD;
      }
    }
    // Operation is 'change'
    return COLOR_CHANGE;
  }
}
