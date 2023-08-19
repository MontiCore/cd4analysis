package de.monticore.cddiff.syndiff.imp;

import de.monticore.ast.ASTNode;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.syntaxdiff.ASTNodeDiff;
import de.monticore.cddiff.syntaxdiff.CDSyntaxDiff;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.ArrayList;
import java.util.List;

// TODO: Write comments
public class CDDiffHelper {

  protected StringBuilder interpretation = new StringBuilder();

  public StringBuilder getInterpretation() {
    return interpretation;
  }

  public void setInterpretation(StringBuilder builder) {
    this.interpretation = builder;
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

  protected List<DiffTypes> interpretationList = new ArrayList<>();

  public List<DiffTypes> getInterpretationList() {
    return interpretationList;
  }

  public void setInterpretationList(List<DiffTypes> newInterpretationList) {
    this.interpretationList = newInterpretationList;
  }

  static double addWeightToDiffSize(
    List<CDNodeDiff<? extends ASTNode, ? extends ASTNode>> diffList) {
    double size = 0.0;
    boolean foundSignatureNameDiff = false;
    int associationNameCounter = 0;
    for (CDNodeDiff<? extends ASTNode, ? extends ASTNode> diff : diffList) {
      if (diff.isPresent() && diff.getTgtValue().isPresent()) {

        ASTNode type = diff.getTgtValue().get();

        // CDMember / Fields
        if (type instanceof ASTCDAttribute) {
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
    if ((!foundSignatureNameDiff) && (associationNameCounter == 0)) {
      size -= 2;
    }
    if (associationNameCounter > 0) {
      size -= (2 - associationNameCounter);
    }
    return size;
  }

}
