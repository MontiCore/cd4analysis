package de.monticore.cddiff.syndiff.imp;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cddiff.syndiff.interfaces.ICDMemberDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.*;

public class CDMemberDiff extends CDDiffHelper implements ICDMemberDiff {
  private final ASTNode srcElem;
  private final ASTNode tgtElem;
  private List<DiffTypes> baseDiff;
  //Print
  CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
  private String srcMemberModifier, srcMemberType, srcMemberName;
  int srcLineOfCode;
  private String tgtMemberModifier, tgtMemberType, tgtMemberName;
  int tgtLineOfCode;
  private String srcMemberString, tgtMemberString;
  //Print end

  public CDMemberDiff(ASTNode srcElem, ASTNode tgtElem) {
    this.srcElem = srcElem;
    this.tgtElem = tgtElem;
    this.baseDiff = new ArrayList<>();

    if ((srcElem instanceof ASTCDAttribute) && (tgtElem instanceof ASTCDAttribute)) {
      createDiffList((ASTCDAttribute) srcElem, (ASTCDAttribute) tgtElem);
      setAttributeStrings();
    }
  }

  @Override
  public List<DiffTypes> getBaseDiff() {
    return baseDiff;
  }

  @Override
  public void setBaseDiff(List<DiffTypes> baseDiff) { this.baseDiff = baseDiff;}

  @Override
  public ASTNode getSrcElem() {
    return srcElem;
  }

  @Override
  public ASTNode getTgtElem() {
    return tgtElem;
  }

  /*--------------------------------------------------------------------*/
  private void createDiffList(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    // Modifier
    if (!(pp.prettyprint(srcElem.getModifier()).isEmpty() && pp.prettyprint(tgtElem.getModifier()).isEmpty())) {
      CDNodeDiff<ASTModifier, ASTModifier> modifierDiff = new CDNodeDiff<>(Optional.of(srcElem.getModifier()), Optional.of(tgtElem.getModifier()));

      if (!(pp.prettyprint(srcElem.getModifier()).isEmpty())) {
        if (!baseDiff.contains(DiffTypes.CHANGED_ATTRIBUTE_MODIFIER)) {
          baseDiff.add(DiffTypes.CHANGED_ATTRIBUTE_MODIFIER);
        }
        srcMemberModifier = getColorCode(modifierDiff) + pp.prettyprint(srcElem.getModifier()) + RESET;
      }

      if (!(pp.prettyprint(tgtElem.getModifier()).isEmpty())) {
        if (!baseDiff.contains(DiffTypes.CHANGED_ATTRIBUTE_MODIFIER)) {
          baseDiff.add(DiffTypes.CHANGED_ATTRIBUTE_MODIFIER);
        }
        tgtMemberModifier = getColorCode(modifierDiff) + pp.prettyprint(tgtElem.getModifier()) + RESET;
      }
    }

    // MCType
    Optional<ASTMCType> srcType = Optional.of(srcElem.getMCType());
    Optional<ASTMCType> tgtType = Optional.of(tgtElem.getMCType());
    CDNodeDiff<ASTMCType, ASTMCType> attributeType = new CDNodeDiff<>(srcType, tgtType);
    if (attributeType.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_ATTRIBUTE_TYPE)) {
        baseDiff.add(DiffTypes.CHANGED_ATTRIBUTE_TYPE);
      }
    }
    srcMemberType = getColorCode(attributeType) + pp.prettyprint(srcType.get()) + RESET;
    tgtMemberType = getColorCode(attributeType) + pp.prettyprint(tgtType.get()) + RESET;

    // Name
    srcMemberName = srcElem.getName() + RESET;
    tgtMemberName = tgtElem.getName() + RESET;

    srcLineOfCode = srcElem.get_SourcePositionStart().getLine();
    tgtLineOfCode = tgtElem.get_SourcePositionStart().getLine();
  }

  private void setAttributeStrings() {
    this.srcMemberString = insertSpaceBetweenStrings(Arrays.asList(srcMemberModifier, srcMemberType, srcMemberName)) + "; " +
      "(Line in srcCD: " +  srcLineOfCode + " | Line in tgtCD: " +  tgtLineOfCode + ")";
    this.tgtMemberString = insertSpaceBetweenStrings(Arrays.asList(tgtMemberModifier, tgtMemberType, tgtMemberName)) + "; " +
      "(Line in srcCD: " +  srcLineOfCode + " | Line in tgtCD: " +  tgtLineOfCode + ")";
  }

  public String printSrcMember() { return srcMemberString; }
  public String printTgtMember() { return tgtMemberString; }
}
