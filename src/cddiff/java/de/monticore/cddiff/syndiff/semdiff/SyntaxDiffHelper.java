package de.monticore.cddiff.syndiff.semdiff;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.syndiff.interfaces.ICDPrintDiff;
import de.monticore.matcher.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SyntaxDiffHelper implements ICDPrintDiff {

  protected static final String COLOR_DELETE = "\033[1;31m";

  protected static final String COLOR_ADD = "\033[1;32m";

  protected static final String COLOR_INHERITED = "\033[1;35m";

  protected static final String COLOR_CHANGE = "\033[1;33m";

  protected static final String RESET = "\033[0m";

  /**
   * Concatenates a list of strings with spaces in between.
   *
   * @param stringList The list of strings to concatenate.
   * @return A single string with spaces between the input strings.
   */
  @Override
  public String insertSpaceBetweenStrings(List<String> stringList) {
    StringBuilder output = new StringBuilder();

    for (String field : stringList) {
      if (!(field == null)) {
        output.append(field).append(" ");
      }
    }
    if (!stringList.isEmpty()) {
      return output.substring(0, output.length() - 1);
    }
    return output.toString();
  }

  /**
   * Concatenates a list of strings with spaces in between and applies a green color to each string.
   *
   * @param stringList The list of strings to concatenate and color.
   * @return A single string with spaces between colored strings.
   */
  @Override
  public String insertSpaceBetweenStringsAndGreen(List<String> stringList) {
    StringBuilder output = new StringBuilder();

    for (String field : stringList) {
      if (!(field == null)) {
        output.append(COLOR_ADD).append(field).append(" ");
      }
    }
    if (!stringList.isEmpty()) {
      return output.substring(0, output.length() - 1);
    }
    return output.toString();
  }

  /**
   * Concatenates a list of strings with spaces in between and applies a red color to each string.
   *
   * @param stringList The list of strings to concatenate and color.
   * @return A single string with spaces between colored strings.
   */
  @Override
  public String insertSpaceBetweenStringsAndRed(List<String> stringList) {
    StringBuilder output = new StringBuilder();

    for (String field : stringList) {
      if (!(field == null)) {
        output.append(COLOR_DELETE).append(field).append(" ");
      }
    }
    if (!stringList.isEmpty()) {
      return output.substring(0, output.length() - 1);
    }
    return output.toString();
  }

  /**
   * Concatenates a list of strings with spaces in between and applies a red color to each string.
   *
   * @param stringList The list of strings to concatenate and color.
   * @return A single string with spaces between colored strings.
   */
  @Override
  public String insertSpaceBetweenStringsAndPurple(List<String> stringList) {
    StringBuilder output = new StringBuilder();

    for (String field : stringList) {
      if (!(field == null)) {
        output.append(COLOR_INHERITED).append(field).append(" ");
      }
    }
    if (!stringList.isEmpty()) {
      return output.substring(0, output.length() - 1);
    }
    return output.toString();
  }

  /**
   * Gets the color code based on the action associated with the difference.
   *
   * @param diff The CDNodeDiff object representing a difference.
   * @return The color code as a string.
   */
  static String getColorCode(CDNodeDiff<?, ?> diff) {
    if (diff.getAction().isPresent()) {
      if (diff.getAction().get().equals(Actions.REMOVED)) {
        return COLOR_DELETE;
      } else if (diff.getAction().get().equals(Actions.ADDED)) {
        return COLOR_ADD;
      } else if (diff.getAction().get().equals(Actions.CHANGED)) {
        return COLOR_CHANGE;
      }
    }
    return "";
  }

  /**
   * Computes a matching map of CD types between the source and target CD. It uses a combination of
   * type matching strategies to match types based on name, structure, and super types. The matching
   * strategies are applied to the list of CD types to create the final matching map.
   *
   * @param listToMatch The list of CD types to be matched.
   * @param srcCD The source CD.
   * @param tgtCD The target CD.
   * @return A map containing matched CD types between source and target CDs.
   */
  public Map<ASTCDType, ASTCDType> computeMatchingMapTypes(
      List<ASTCDType> listToMatch, ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    NameTypeMatcher nameTypeMatch = new NameTypeMatcher(tgtCD);
    StructureTypeMatcher structureTypeMatch = new StructureTypeMatcher(tgtCD);
    SuperTypeMatcher superTypeMatchNameType = new SuperTypeMatcher(nameTypeMatch, srcCD, tgtCD);
    SuperTypeMatcher superTypeMatchStructureType =
        new SuperTypeMatcher(structureTypeMatch, srcCD, tgtCD);
    List<MatchingStrategy<ASTCDType>> typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatchNameType);
    typeMatchers.add(superTypeMatchStructureType);

    CombinedMatching<ASTCDType> combinedMatching =
        new CombinedMatching<>(listToMatch, srcCD, tgtCD, typeMatchers);

    return combinedMatching.getFinalMap();
  }

  /**
   * Computes a matching map of CD associations between the source and target CD. It uses a
   * combination of association matching strategies to match associations based on name, type, and
   * structure. The matching strategies are applied to the list of CD associations to create the
   * final matching map.
   *
   * @param listToMatch The list of CD associations to be matched.
   * @param srcCD The source CD.
   * @param tgtCD The target CD.
   * @return A map containing matched CD associations between source and target CDs.
   */
  public Map<ASTCDAssociation, ASTCDAssociation> computeMatchingMapAssocs(
      List<ASTCDAssociation> listToMatch, ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    NameAssocMatcher nameAssocMatch = new NameAssocMatcher(tgtCD);
    NameTypeMatcher nameTypeMatch = new NameTypeMatcher(tgtCD);
    StructureTypeMatcher structureTypeMatch = new StructureTypeMatcher(tgtCD);
    SuperTypeMatcher superTypeMatchNameType = new SuperTypeMatcher(nameTypeMatch, srcCD, tgtCD);
    SuperTypeMatcher superTypeMatchStructureType =
        new SuperTypeMatcher(structureTypeMatch, srcCD, tgtCD);
    SrcTgtAssocMatcher associationSrcTgtMatchStructureType =
      new SrcTgtAssocMatcher(superTypeMatchStructureType, srcCD, tgtCD);
    SrcTgtAssocMatcher associationSrcTgtMatchNameType =
        new SrcTgtAssocMatcher(superTypeMatchNameType, srcCD, tgtCD);
    List<MatchingStrategy<ASTCDAssociation>> assocMatchers = new ArrayList<>();
    assocMatchers.add(nameAssocMatch);
    assocMatchers.add(associationSrcTgtMatchNameType);
    assocMatchers.add(associationSrcTgtMatchStructureType);

    CombinedMatching<ASTCDAssociation> combinedMatching =
        new CombinedMatching<>(listToMatch, srcCD, tgtCD, assocMatchers);

    return combinedMatching.getFinalMap();
  }
}
