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
  private String srcMemberString, tgtMemberString, interpretation;
  //Print end

  public CDMemberDiff(ASTNode srcElem, ASTNode tgtElem) {
    this.srcElem = srcElem;
    this.tgtElem = tgtElem;
    if ((tgtElem instanceof ASTCDAttribute) && (srcElem instanceof ASTCDAttribute)) {
      memberDiff((ASTCDAttribute) tgtElem, (ASTCDAttribute) srcElem);
    }
    if ((tgtElem instanceof ASTCDEnumConstant) && (srcElem instanceof ASTCDEnumConstant)) {
      memberDiff((ASTCDEnumConstant) tgtElem, (ASTCDEnumConstant) srcElem);
    }
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
  public ASTNode getSrcElem() {
    return srcElem;
  }

  @Override
  public ASTNode getTgtElem() {
    return tgtElem;
  }

  /*--------------------------------------------------------------------*/

  public ASTNode compareMember(ASTNode srcElem, ASTNode tgtElem) {
    if (srcElem instanceof ASTCDAttribute || tgtElem instanceof ASTCDAttribute) {
      ASTCDAttribute srcAttr = (ASTCDAttribute) srcElem;
      ASTCDAttribute tgtAttr = (ASTCDAttribute) tgtElem;

      if (srcAttr.getName().equals(tgtAttr.getName())) {
        if (!srcAttr.getMCType().equals(tgtAttr.getMCType()) || !srcAttr.getModifier().equals(tgtAttr.getModifier())) {
          baseDiff.add(DiffTypes.CHANGED_ATTRIBUTE);
          return srcElem;
        }
      }
    }
    return null;
  }

  public void memberDiff(ASTCDAttribute tgtElem, ASTCDAttribute srcElem) {
    this.diffList = createDiffList(tgtElem, srcElem);
    setAttributeStrings();
  }

  public void memberDiff(ASTCDEnumConstant tgtElem, ASTCDEnumConstant srcElem) {
    this.diffList = createDiffList(tgtElem, srcElem);
    setEnumStrings();
  }

  private List<CDNodeDiff<? extends ASTNode, ? extends ASTNode>> createDiffList(ASTCDAttribute tgtElem, ASTCDAttribute srcElem) {

    List<CDNodeDiff<?,?>> diffs = new ArrayList<>();

    // Modifier
    if (!(pp.prettyprint(tgtElem.getModifier()).isEmpty() && pp.prettyprint(srcElem.getModifier()).isEmpty())) {
      diffs.add(setModifier(tgtElem.getModifier(), srcElem.getModifier()));
    }

    // MCType
    Optional<ASTMCType> cd1Type = Optional.of(tgtElem.getMCType());
    Optional<ASTMCType> cd2Type = Optional.of(srcElem.getMCType());
    CDNodeDiff<ASTMCType, ASTMCType> attributeType = new CDNodeDiff<>(cd1Type, cd2Type);
    if (attributeType.checkForAction()) {
      diffs.add(attributeType);
    }
    tgtMemberType = getColorCode(attributeType) + pp.prettyprint(cd1Type.get()) + RESET;
    srcMemberType = getColorCode(attributeType) + pp.prettyprint(cd2Type.get()) + RESET;

    // Name, non-optional
    Optional<ASTCDAttribute> cd1Name = Optional.of(tgtElem);
    Optional<ASTCDAttribute> cd2Name = Optional.of(srcElem);

    CDNodeDiff<ASTCDAttribute, ASTCDAttribute> attributeName =
      new CDNodeDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      attributeName = new CDNodeDiff<>(Actions.CHANGED, cd1Name, cd2Name);
    }

    if (attributeName.checkForAction()) {
      diffs.add(attributeName);
    }
    tgtMemberName = getColorCode(attributeName) + cd1Name.get().getName() + RESET;
    srcMemberName = getColorCode(attributeName) + cd2Name.get().getName() + RESET;

    return diffs;
  }

  private List<CDNodeDiff<? extends ASTNode, ? extends ASTNode>> createDiffList(
    ASTCDEnumConstant cd1Element, ASTCDEnumConstant cd2Element) {
    List<CDNodeDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();

    // Name, non-optional
    Optional<ASTCDEnumConstant> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDEnumConstant> cd2Name = Optional.of(cd2Element);
    CDNodeDiff<ASTCDEnumConstant, ASTCDEnumConstant> name = new CDNodeDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {name = new CDNodeDiff<>(Actions.CHANGED, cd1Name, cd2Name);
    }

    if (name.checkForAction()) {
      diffs.add(name);
    }

    tgtMemberName = getColorCode(name) + cd1Name.get().getName() + RESET;
    srcMemberName = getColorCode(name) + cd2Name.get().getName() + RESET;

    return diffs;
  }

  protected CDNodeDiff<ASTModifier, ASTModifier> setModifier(ASTModifier cd1Modi, ASTModifier cd2Modi) {
    CDNodeDiff<ASTModifier, ASTModifier> modifier = new CDNodeDiff<>(Optional.of(cd1Modi), Optional.of(cd2Modi));

    if (!(pp.prettyprint(cd1Modi).isEmpty())) {
      tgtMemberModifier = getColorCode(modifier) + pp.prettyprint(cd1Modi) + RESET;
    }

    if (!(pp.prettyprint(cd2Modi).isEmpty())) {
      srcMemberModifier = getColorCode(modifier) + pp.prettyprint(cd2Modi) + RESET;
    }

    return modifier;
  }

  private void setAttributeStrings() {
    this.tgtMemberString = insertSpaceBetweenStrings(Arrays.asList(tgtMemberModifier, tgtMemberType, tgtMemberName));
    this.srcMemberString = insertSpaceBetweenStrings(Arrays.asList(srcMemberModifier, srcMemberType, srcMemberName));
    this.interpretation = "Interpretation: ";
  }

  private void setEnumStrings() {

    this.tgtMemberString = tgtMemberName;
    this.srcMemberString = srcMemberName;
    this.interpretation = "Interpretation: ";
  }

  public String printCD1Element() {
    return tgtMemberString;
  }

  public String printCD2Element() {
    return srcMemberString;
  }
}
