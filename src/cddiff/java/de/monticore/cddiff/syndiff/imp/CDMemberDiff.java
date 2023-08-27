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


// TODO: Write Comments
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
      memberDiff((ASTCDAttribute) srcElem, (ASTCDAttribute) tgtElem);
    }
    if ((srcElem instanceof ASTCDEnumConstant) && (tgtElem instanceof ASTCDEnumConstant)) {
      memberDiff((ASTCDEnumConstant) srcElem, (ASTCDEnumConstant) tgtElem);
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

  public void memberDiff(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    this.diffList = createDiffList(srcElem, tgtElem);
    setAttributeStrings();
  }

  public void memberDiff(ASTCDEnumConstant srcElem, ASTCDEnumConstant tgtElem) {
    this.diffList = createDiffList(srcElem, tgtElem);
    setEnumStrings();
  }

  private List<CDNodeDiff<?,?>> createDiffList(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {

    List<CDNodeDiff<?,?>> synDiffs = new ArrayList<>();

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

      synDiffs.add(modifierDiff);
    }

    // MCType
    Optional<ASTMCType> srcType = Optional.of(srcElem.getMCType());
    Optional<ASTMCType> tgtType = Optional.of(tgtElem.getMCType());
    CDNodeDiff<ASTMCType, ASTMCType> attributeType = new CDNodeDiff<>(srcType, tgtType);
    if (attributeType.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_ATTRIBUTE_TYPE)) {
        baseDiff.add(DiffTypes.CHANGED_ATTRIBUTE_TYPE);
      }
      synDiffs.add(attributeType);
    }
    srcMemberType = getColorCode(attributeType) + pp.prettyprint(srcType.get()) + RESET;
    tgtMemberType = getColorCode(attributeType) + pp.prettyprint(tgtType.get()) + RESET;

    // Name
    Optional<ASTCDAttribute> srcName = Optional.of(srcElem);
    Optional<ASTCDAttribute> tgtName = Optional.of(tgtElem);

    CDNodeDiff<ASTCDAttribute, ASTCDAttribute> attributeName =  new CDNodeDiff<>(null, srcName, tgtName);

    if (!srcName.get().getName().equals(tgtName.get().getName())) {
      attributeName = new CDNodeDiff<>(Actions.CHANGED, srcName, tgtName);
    }

    if (attributeName.checkForAction()) {
      synDiffs.add(attributeName);
    }
    srcMemberName = getColorCode(attributeName) + srcName.get().getName() + RESET;
    tgtMemberName = getColorCode(attributeName) + tgtName.get().getName() + RESET;

    srcLineOfCode = srcElem.get_SourcePositionStart().getLine();
    tgtLineOfCode = tgtElem.get_SourcePositionStart().getLine();

    return synDiffs;
  }

  private List<CDNodeDiff<? extends ASTNode, ? extends ASTNode>> createDiffList(ASTCDEnumConstant srcEnum, ASTCDEnumConstant tgtEnum) {
    List<CDNodeDiff<?,?>> synDiffs = new ArrayList<>();

    // Name
    Optional<ASTCDEnumConstant> srcEnumConst = Optional.of(srcEnum);
    Optional<ASTCDEnumConstant> tgtEnumConst = Optional.of(tgtEnum);
    CDNodeDiff<ASTCDEnumConstant, ASTCDEnumConstant> name = new CDNodeDiff<>(null, srcEnumConst, tgtEnumConst);

    if (!srcEnumConst.get().getName().equals(tgtEnumConst.get().getName())) {
      name = new CDNodeDiff<>(Actions.CHANGED, srcEnumConst, tgtEnumConst);
    }

    if (name.checkForAction()) {
      synDiffs.add(name);
    }

    srcMemberName = getColorCode(name) + srcEnum.getName() + RESET;
    tgtMemberName = getColorCode(name) + tgtEnum.getName() + RESET;

    srcLineOfCode = srcEnum.get_SourcePositionStart().getLine();
    tgtLineOfCode = tgtEnum.get_SourcePositionStart().getLine();

    return synDiffs;
  }

  private void setAttributeStrings() {
    this.srcMemberString = insertSpaceBetweenStrings(Arrays.asList(srcMemberModifier, srcMemberType, srcMemberName)) + "; " +
      "(Line in srcCD: " +  srcLineOfCode + " | Line in tgtCD: " +  tgtLineOfCode + ")";
    this.tgtMemberString = insertSpaceBetweenStrings(Arrays.asList(tgtMemberModifier, tgtMemberType, tgtMemberName)) + "; " +
      "(Line in srcCD: " +  srcLineOfCode + " | Line in tgtCD: " +  tgtLineOfCode + ")";
  }

  private void setEnumStrings() {
    this.srcMemberString = insertSpaceBetweenStrings(Collections.singletonList(srcMemberName)) + "; " +
      "(Line in srcCD: " +  srcLineOfCode + " | Line in tgtCD: " +  tgtLineOfCode + ")";
    this.tgtMemberString = insertSpaceBetweenStrings(Collections.singletonList(tgtMemberName)) + "; " +
      "(Line in srcCD: " +  srcLineOfCode + " | Line in tgtCD: " +  tgtLineOfCode + ")";
  }

  public String printSrcMember() {
    return srcMemberString;
  }
  public String printTgtMember() {
    return tgtMemberString;
  }
}
