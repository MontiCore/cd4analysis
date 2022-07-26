package de.monticore.cddiff.syntaxdiff;

import de.monticore.ast.ASTNode;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

public abstract class AbstractDiffType {

  protected static final String COLOR_DELETE = "\033[1;31m";

  protected static final String COLOR_ADD = "\033[1;32m";

  protected static final String COLOR_CHANGE = "\033[1;33m";

  protected static final String RESET = "\033[0m";

  protected StringBuilder interpretation = new StringBuilder();

  protected double diffSize;

  protected List<FieldDiff<? extends ASTNode, ? extends ASTNode>> diffList;

  protected int breakingChange;

  protected List<SyntaxDiff.Interpretation> interpretationList = new ArrayList<>();

  public StringBuilder getInterpretation() {
    return interpretation;
  }

  public int getBreakingChange() {
    return breakingChange;
  }

  public List<SyntaxDiff.Interpretation> getInterpretationList() {
    return interpretationList;
  }

  public double getDiffSize() {
    return diffSize;
  }

  public List<FieldDiff<? extends ASTNode, ? extends ASTNode>> getDiffList() {
    return diffList;
  }

  /**
   * Methode for calculating a list of elements which are not included in any match provided by the
   * matchs list.
   *
   * @param matchs      List of matches between elements with the ElementDiff type
   * @param elementList List of elements to be reduced e.g. List of Attributes from a CD Class
   * @param <T>         Type of the element, e.g. Classes
   * @return Reduced list of type provided as elementList
   */
  protected static <T extends ASTNode> List<T> absentElementList(List<ElementDiff<T>> matchs,
      List<T> elementList) {
    List<T> output = new ArrayList<>();
    for (T element : elementList) {
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
   * Methode to reduce a given list of potential matches between elements to at most one match for
   * each entry
   *
   * @param elementsDiffList List of diffs between one element of the first model and every element
   *                         of the same type from the second model
   * @param <T>              Type of the element, e.g. Classes
   * @return Reduced list of matches for elements between two models
   */
  protected static <T extends ASTNode> List<ElementDiff<T>> getMatchingList(
      List<List<ElementDiff<T>>> elementsDiffList) {
    List<T> cd1matchedElements = new ArrayList<>();
    List<T> cd2matchedElements = new ArrayList<>();
    List<ElementDiff<T>> matchedElements = new ArrayList<>();

    for (List<ElementDiff<T>> currentElementList : elementsDiffList) {
      double threshold = 0;
      OptionalDouble optAverage = currentElementList.stream()
          .mapToDouble(ElementDiff::getDiffSize)
          .average();
      if (optAverage.isPresent()) {
        threshold = (1 / (double) (currentElementList.size() + 1)) + optAverage.getAsDouble() / 2;
      }
      if (!currentElementList.isEmpty()) {
        for (ElementDiff<T> currentElementDiff : currentElementList) {
          T currentcd1Element = currentElementDiff.getCd1Element();
          T currentcd2Element = currentElementDiff.getCd2Element();
          if (!cd1matchedElements.contains(currentcd1Element) && !cd2matchedElements.contains(
              currentcd2Element)) {
            boolean found = false;
            for (List<ElementDiff<T>> nextElementDiffList : elementsDiffList) {
              if (!nextElementDiffList.equals(currentElementList)) {
                if (!nextElementDiffList.isEmpty()) {
                  for (ElementDiff<T> nextElementDiff : nextElementDiffList) {
                    if (nextElementDiff.getCd2Element().deepEquals(currentcd2Element)
                        && nextElementDiff.getDiffSize() < currentElementDiff.getDiffSize()) {
                      found = true;
                    }
                  }
                }
              }
            }
            if (!found && currentElementDiff.getDiffSize() <= threshold) {
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
   *
   * @param diff Fielddiff which contains the operation from the lowest level(fields)
   * @return Color code as String (Set this String directly before the to-be-colored String)
   */
  protected static String getColorCode(FieldDiff<? extends ASTNode, ? extends ASTNode> diff) {
    if (diff.getOperation().isPresent()) {
      if (diff.getOperation().get().equals(SyntaxDiff.Op.DELETE)) {
        return COLOR_DELETE;
      }
      else if (diff.getOperation().get().equals(SyntaxDiff.Op.ADD)) {
        return COLOR_ADD;
      }
      else if (diff.getOperation().get().equals(SyntaxDiff.Op.CHANGE)) {
        return COLOR_CHANGE;
      }
    }
    // No Operation
    return RESET;
  }

  protected static double addWeightToDiffSize(
      List<FieldDiff<? extends ASTNode, ? extends ASTNode>> diffList) {
    double size = 0.0;
    for (FieldDiff<? extends ASTNode, ? extends ASTNode> diff : diffList) {
      if (diff.isPresent() && diff.getCd1Value().isPresent()) {
        // Name Diffs are weighted doubled compared to every other diff
        // Parent Object in FieldDiff when we check the name of it (when there is no specific
        // node for the name)
        if (diff.getCd1Value().get() instanceof ASTCDAttribute || diff.getCd1Value()
            .get() instanceof ASTMCQualifiedName || diff.getCd1Value().get() instanceof ASTCDClass
            || diff.getCd1Value().get() instanceof ASTCDConstructor || diff.getCd1Value()
            .get() instanceof ASTCDMethod) {
          size += 1;
        }
      }
    }
    return size;
  }

  /**
   * Help method for calculating the class diff because each class can contains multiple methodes
   * which need to be matched
   *
   * @param cd1ElementList List of methodes from the original model
   * @param cd2ElementList List of methodes from the target(new) model
   * @return Returns a difflist for each methodes, ordered by diffsize (small diff values ==
   * similar)
   */
  protected static <T extends ASTNode> List<List<ElementDiff<T>>> getElementDiffList(
      List<T> cd1ElementList, List<T> cd2ElementList) {
    List<List<ElementDiff<T>>> diffs = new ArrayList<>();
    for (T cd1Element : cd1ElementList) {
      List<ElementDiff<T>> cd1ElementMatches = new ArrayList<>();
      for (T cd2Element : cd2ElementList) {
        cd1ElementMatches.add(new ElementDiff<>(cd1Element, cd2Element));
      }
      // Sort by size of diffs, ascending
      cd1ElementMatches.sort(Comparator.comparing(ElementDiff::getDiffSize));
      diffs.add(cd1ElementMatches);
    }
    return diffs;
  }

  protected String combineWithoutNulls(List<String> stringList) {
    StringBuilder output = new StringBuilder();

    for (String field : stringList) {
      if (!(field == null) && field.length() > 8) {
        output.append(field).append(" ");
      }
    }
    return output.toString();
  }

}
