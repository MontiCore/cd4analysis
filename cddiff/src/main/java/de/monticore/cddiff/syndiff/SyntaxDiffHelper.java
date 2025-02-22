package de.monticore.cddiff.syndiff;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.*;
import java.util.*;

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

  public Map<ASTCDType, ASTCDType> computeMatchingMapTypes(
      List<ASTCDType> listToMatch,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD,
      List<de.monticore.cddiff.syn2semdiff.datastructures.MatchingStrategy> matchingStrategies) {
    List<MatchingStrategy<ASTCDType>> typeMatchers = new ArrayList<>();
    if (matchingStrategies.isEmpty()) {
      matchingStrategies =
          Arrays.asList(de.monticore.cddiff.syn2semdiff.datastructures.MatchingStrategy.values());
    }
    for (de.monticore.cddiff.syn2semdiff.datastructures.MatchingStrategy matchingStrategy :
        matchingStrategies) {
      if (Objects.requireNonNull(matchingStrategy)
          == de.monticore.cddiff.syn2semdiff.datastructures.MatchingStrategy.SUPER_TYPE_MATCHER) {
        MatchCDTypesToSuperTypes superTypeMatchNameType =
            new MatchCDTypesToSuperTypes(new MatchCDTypesByName(tgtCD), srcCD, tgtCD);
        MatchCDTypesToSuperTypes superTypeMatchStructureType =
            new MatchCDTypesToSuperTypes(new MatchCDTypeByStructure(tgtCD), srcCD, tgtCD);
        typeMatchers.add(superTypeMatchNameType);
        typeMatchers.add(superTypeMatchStructureType);
      }
    }
    MatchCDTypesByName nameTypeMatch = new MatchCDTypesByName(tgtCD);
    typeMatchers.add(nameTypeMatch);

    CD2CDCombinedMatching<ASTCDType> combinedMatching =
        new CombinedCDTypeMatching(listToMatch, srcCD, tgtCD, typeMatchers);

    return combinedMatching.getMatches();
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
    List<MatchingStrategy<ASTCDType>> typeMatchers = new ArrayList<>();

    MatchCDTypeByStructure structureTypeMatch = new MatchCDTypeByStructure(tgtCD);
    typeMatchers.add(structureTypeMatch);

    MatchCDTypesToSuperTypes superTypeMatchNameType =
        new MatchCDTypesToSuperTypes(new MatchCDTypesByName(tgtCD), srcCD, tgtCD);
    MatchCDTypesToSuperTypes superTypeMatchStructureType =
        new MatchCDTypesToSuperTypes(new MatchCDTypeByStructure(tgtCD), srcCD, tgtCD);
    typeMatchers.add(superTypeMatchNameType);
    typeMatchers.add(superTypeMatchStructureType);
    MatchCDTypesByName nameTypeMatch = new MatchCDTypesByName(tgtCD);
    typeMatchers.add(nameTypeMatch);

    CD2CDCombinedMatching<ASTCDType> combinedMatching =
        new CombinedCDTypeMatching(listToMatch, srcCD, tgtCD, typeMatchers);

    return combinedMatching.getMatches();
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
      List<ASTCDAssociation> listToMatch,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD,
      List<de.monticore.cddiff.syn2semdiff.datastructures.MatchingStrategy> matchingStrategies) {
    List<MatchingStrategy<ASTCDAssociation>> assocMatchers = new ArrayList<>();
    if (matchingStrategies.isEmpty()) {
      matchingStrategies =
          Arrays.asList(de.monticore.cddiff.syn2semdiff.datastructures.MatchingStrategy.values());
    }
    for (de.monticore.cddiff.syn2semdiff.datastructures.MatchingStrategy matchingStrategy :
        matchingStrategies) {
      if (Objects.requireNonNull(matchingStrategy)
          == de.monticore.cddiff.syn2semdiff.datastructures.MatchingStrategy
              .SOURCE_TARGET_MATCHING) {
        MatchCDAssocsBySrcNameAndTgtRole associationSrcTgtMatchNameType =
            new MatchCDAssocsBySrcNameAndTgtRole(new MatchCDTypesByName(tgtCD), srcCD, tgtCD);
        MatchCDAssocsBySrcNameAndTgtRole associationSrcTgtMatchStructureType =
            new MatchCDAssocsBySrcNameAndTgtRole(new MatchCDTypeByStructure(tgtCD), srcCD, tgtCD);
        assocMatchers.add(associationSrcTgtMatchNameType);
        assocMatchers.add(associationSrcTgtMatchStructureType);
      }
    }

    CD2CDCombinedMatching<ASTCDAssociation> combinedMatching =
        new CombinedCDAssocMatching(listToMatch, srcCD, tgtCD, assocMatchers);

    return combinedMatching.getMatches();
  }
}
