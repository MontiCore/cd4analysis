package de.monticore.cddiff.syntaxdiff;

import de.monticore.ast.ASTNode;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import java.util.ArrayList;
import java.util.List;

public abstract class CDElementDiff implements IElementDiff {

  protected static final String COLOR_DELETE = "\033[1;31m";

  protected static final String COLOR_ADD = "\033[1;32m";

  protected static final String COLOR_CHANGE = "\033[1;33m";

  protected static final String RESET = "\033[0m";

  protected StringBuilder interpretation = new StringBuilder();

  protected double diffSize;

  protected List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> diffList;

  protected int breakingChange = 0;

  protected List<CDSyntaxDiff.Interpretation> interpretationList = new ArrayList<>();

  @Override
  public StringBuilder getInterpretation() {
    return interpretation;
  }

  @Override
  public void setInterpretation(StringBuilder builder) {
    this.interpretation = builder;
  }

  @Override
  public int getBreakingChange() {
    return breakingChange;
  }

  @Override
  public List<CDSyntaxDiff.Interpretation> getInterpretationList() {
    return interpretationList;
  }

  @Override
  public void setInterpretationList(List<CDSyntaxDiff.Interpretation> newInterpretationList) {
    this.interpretationList = newInterpretationList;
  }

  @Override
  public double getDiffSize() {
    return diffSize;
  }

  @Override
  public void addDiffSize(int value) {
    this.diffSize += value;
  }

  @Override
  public List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> getDiffList() {
    return diffList;
  }

  @Override
  public String combineWithoutNulls(List<String> stringList) {
    StringBuilder output = new StringBuilder();

    for (String field : stringList) {
      // if (!(field == null) && field.length() > 8) {
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
   * Helper function to determine the color code according to the operation recognized
   *
   * @param diff Fielddiff which contains the operation from the lowest level(fields)
   * @return Color code as String (Set this String directly before the to-be-colored String)
   */
  static String getColorCode(ASTNodeDiff<? extends ASTNode, ? extends ASTNode> diff) {
    if (diff.getOperation().isPresent()) {
      if (diff.getOperation().get().equals(CDSyntaxDiff.Op.DELETE)) {
        return CDElementDiff.COLOR_DELETE;
      } else if (diff.getOperation().get().equals(CDSyntaxDiff.Op.ADD)) {
        return CDElementDiff.COLOR_ADD;
      } else if (diff.getOperation().get().equals(CDSyntaxDiff.Op.CHANGE)) {
        return CDElementDiff.COLOR_CHANGE;
      }
    }
    // No Operation
    return "";
  }

  static double addWeightToDiffSize(
      List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> diffList) {
    double size = 0.0;
    boolean foundSignatureNameDiff = false;
    int associationNameCounter = 0;
    for (ASTNodeDiff<? extends ASTNode, ? extends ASTNode> diff : diffList) {
      if (diff.isPresent() && diff.getCd1Value().isPresent()) {
        // Name Diffs are weighted doubled compared to every other diff
        // Parent Object in ASTNodeDiff when we check the name of it (when there is no specific
        // node for the name)

        ASTNode type = diff.getCd1Value().get();

        // CDMember / Fields
        if (type instanceof ASTCDAttribute
            || type instanceof ASTCDConstructor
            || type instanceof ASTCDMethod
            || type instanceof ASTCDParameter) {
          size += 1;
        } else

        // Main Signature Names
        if (type instanceof ASTCDType) {
          size += 2;
          foundSignatureNameDiff = true;
        } else
        // Association participant names
        if (type instanceof ASTMCQualifiedName) {
          size += 1;
          associationNameCounter += 1;
        }
      }
    }
    // No namediff in current diff set -> Name is equal, asso counter is 0 only if both qualified
    // names are equal
    if ((!foundSignatureNameDiff) && (associationNameCounter == 0)) {
      size -= 2;
    }
    if (associationNameCounter > 0) {
      size -= (2 - associationNameCounter);
    }
    return size;
  }
}
