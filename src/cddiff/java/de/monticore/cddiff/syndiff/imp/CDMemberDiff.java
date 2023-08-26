package de.monticore.cddiff.syndiff.imp;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cddiff.syndiff.interfaces.ICDMemberDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


// TODO: Write Comments
public class CDMemberDiff extends CDDiffHelper implements ICDMemberDiff {
  private final ASTNode srcElem;
  private final ASTNode tgtElem;
  private List<DiffTypes> baseDiff;

  //Print
  CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
  private String srcMemberModifier, srcMemberType, srcMemberName;
  private String tgtMemberModifier, tgtMemberType, tgtMemberName;
  private String srcMemberString, tgtMemberString;
  //Print end

  public CDMemberDiff(ASTNode srcElem, ASTNode tgtElem) {
    this.srcElem = srcElem;
    this.tgtElem = tgtElem;
    this.baseDiff = new ArrayList<>();

    if ((tgtElem instanceof ASTCDAttribute) && (srcElem instanceof ASTCDAttribute)) {
      memberDiff((ASTCDAttribute) srcElem, (ASTCDAttribute) tgtElem);
    }
    if ((tgtElem instanceof ASTCDEnumConstant) && (srcElem instanceof ASTCDEnumConstant)) {
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

  public void memberDiff(ASTCDAttribute tgtElem, ASTCDAttribute srcElem) {
    this.diffList = createDiffList(srcElem, tgtElem);
    setAttributeStrings();
  }

  public void memberDiff(ASTCDEnumConstant tgtElem, ASTCDEnumConstant srcElem) {
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
    Optional<ASTMCType> cd1Type = Optional.of(tgtElem.getMCType());
    Optional<ASTMCType> cd2Type = Optional.of(srcElem.getMCType());
    CDNodeDiff<ASTMCType, ASTMCType> attributeType = new CDNodeDiff<>(cd1Type, cd2Type);
    if (attributeType.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_ATTRIBUTE_TYPE)) {
        baseDiff.add(DiffTypes.CHANGED_ATTRIBUTE_TYPE);
      }
      synDiffs.add(attributeType);
    }
    tgtMemberType = getColorCode(attributeType) + pp.prettyprint(cd1Type.get()) + RESET;
    srcMemberType = getColorCode(attributeType) + pp.prettyprint(cd2Type.get()) + RESET;

    // Name
    Optional<ASTCDAttribute> cd1Name = Optional.of(tgtElem);
    Optional<ASTCDAttribute> cd2Name = Optional.of(srcElem);

    CDNodeDiff<ASTCDAttribute, ASTCDAttribute> attributeName =  new CDNodeDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      attributeName = new CDNodeDiff<>(Actions.CHANGED, cd1Name, cd2Name);
    }

    if (attributeName.checkForAction()) {
      synDiffs.add(attributeName);
    }
    tgtMemberName = getColorCode(attributeName) + cd1Name.get().getName() + RESET;
    srcMemberName = getColorCode(attributeName) + cd2Name.get().getName() + RESET;

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

    return synDiffs;
  }

  private void setAttributeStrings() {
    this.srcMemberString = insertSpaceBetweenStrings(Arrays.asList(srcMemberModifier, srcMemberType, srcMemberName)) + ";";
    this.tgtMemberString = insertSpaceBetweenStrings(Arrays.asList(tgtMemberModifier, tgtMemberType, tgtMemberName)) + ";";
  }

  private void setEnumStrings() {
    this.srcMemberString = insertSpaceBetweenStrings(Arrays.asList(srcMemberName)) + ";";
    this.tgtMemberString = insertSpaceBetweenStrings(Arrays.asList(tgtMemberName)) + ";";
  }

  public String printSrcMember() {
    return srcMemberString;
  }
  public String printTgtMember() {
    return tgtMemberString;
  }
}
