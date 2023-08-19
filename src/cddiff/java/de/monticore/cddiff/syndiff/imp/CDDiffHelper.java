package de.monticore.cddiff.syndiff.imp;

import de.monticore.ast.ASTNode;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.syndiff.interfaces.ICDPrintDiff;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.ArrayList;
import java.util.List;

// TODO: Write comments
public class CDDiffHelper implements ICDPrintDiff {

  protected static final String COLOR_DELETE = "\033[1;31m";

  protected static final String COLOR_ADD = "\033[1;32m";

  protected static final String COLOR_CHANGE = "\033[1;33m";

  protected static final String RESET = "\033[0m";
  protected StringBuilder diffType = new StringBuilder();

  public StringBuilder getDiffType() {
    return diffType;
  }

  public void setDiffType(StringBuilder builder) {
    this.diffType = builder;
  }

  protected double diffSize;

  public double getDiffSize() {
    return diffSize;
  }

  public void addDiffSize(int value) {
    this.diffSize += value;
  }

  protected List<CDNodeDiff<? extends ASTNode, ? extends ASTNode>> diffList;

  public List<CDNodeDiff<? extends ASTNode, ? extends ASTNode>> getDiffList() {
    return diffList;
  }

  protected int breakingChange = 0;

  public int getBreakingChange() {
    return breakingChange;
  }

  protected List<DiffTypes> diffTypesList = new ArrayList<>();

  public List<DiffTypes> getDiffTypesList() {
    return diffTypesList;
  }

  public void setDiffTypesList(List<DiffTypes> newInterpretationList) {
    this.diffTypesList = newInterpretationList;
  }

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

  static String getColorCode(CDNodeDiff<? extends ASTNode, ? extends ASTNode> diff) {
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

}
