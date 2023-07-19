package de.monticore.cddiff.syndiff.imp;

import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.cddiff.syndiff.DiffTypes;
import de.monticore.cddiff.syndiff.ICDMemberDiff;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Arrays;
import java.util.List;

public class CDMemberDiff implements ICDMemberDiff {
  private final ASTCDMember srcElem;
  private final ASTCDMember tgtElem;
  private List<DiffTypes> baseDiff;

  // Printer help functions and strings
  protected static final String CHANGED_ATTRIBUTE = "\u001B[33m";
  final String RESET = "\u001B[0m";
  String srcAttrName;
  String srcAttrType;
  String srcAttrModifier;

  String tgtAttrName;
  String tgtAttrType;
  String tgtAttrModifier;

  String differenceInWords;
  String attrSrcCD;
  String attrTgtCD;

  public CDMemberDiff(ASTCDMember srcElem, ASTCDMember tgtElem) {
    this.srcElem = srcElem;
    this.tgtElem = tgtElem;
    buildingInterpretation();
  }

  @Override
  public List<DiffTypes> getBaseDiff() {
    return baseDiff;
  }

  @Override
  public void setBaseDiff(List<DiffTypes> baseDiff) {
    this.baseDiff = baseDiff;
  }

  @Override
  public ASTCDMember getSrcElem() {
    return srcElem;
  }

  @Override
  public ASTCDMember getTgtElem() {
    return tgtElem;
  }

  public ASTCDMember compareMember(ASTCDMember srcElem, ASTCDMember tgtElem) {
    CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    if (srcElem instanceof ASTCDAttribute || tgtElem instanceof ASTCDAttribute) {
      ASTCDAttribute srcAttr = (ASTCDAttribute) srcElem;
      ASTCDAttribute tgtAttr = (ASTCDAttribute) tgtElem;

      srcAttrName = srcAttr.getName();
      tgtAttrName = tgtAttr.getName();

      if (srcAttr.getName().equals(tgtAttr.getName())) {
        if (!srcAttr.getMCType().equals(tgtAttr.getMCType())) {
          baseDiff.add(DiffTypes.CHANGED_ATTRIBUTE);


          srcAttrType = CHANGED_ATTRIBUTE + printer.prettyprint(srcAttr.getMCType()) + RESET;
          tgtAttrType = CHANGED_ATTRIBUTE + printer.prettyprint(tgtAttr.getMCType()) + RESET;
          return srcElem;
        }
        if (!srcAttr.getModifier().equals(tgtAttr.getModifier())) {
          baseDiff.add(DiffTypes.CHANGED_VISIBILITY);
          srcAttrModifier = CHANGED_ATTRIBUTE + printer.prettyprint(srcAttr.getModifier()) + RESET;
          tgtAttrModifier = CHANGED_ATTRIBUTE + printer.prettyprint(tgtAttr.getModifier()) + RESET;
          return srcElem;
        }
      }
    }
    return null;
  }

  private void buildingInterpretation() {

    this.attrSrcCD =
      buildStrings(Arrays.asList(srcAttrModifier, srcAttrType, srcAttrName));

    this.attrTgtCD =
      buildStrings(Arrays.asList(tgtAttrModifier, tgtAttrType, tgtAttrName));

    this.differenceInWords = "Difference in words: ";
  }

  @Override
  public String buildStrings(List<String> stringList) {
    return buildStrings(stringList) + ";";
  }

  public String printHelperSrcCD() {
    return attrSrcCD;
  }

  public String printHelperTgtCD() {
    return attrTgtCD;
  }
}
