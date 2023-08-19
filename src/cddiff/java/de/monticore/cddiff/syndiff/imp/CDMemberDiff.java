package de.monticore.cddiff.syndiff.imp;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cddiff.syndiff.interfaces.ICDMemberDiff;
import de.monticore.cddiff.syndiff.interfaces.ICDPrintDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


// TODO: Write Comments
public class CDMemberDiff extends CDDiffHelper implements ICDMemberDiff, ICDPrintDiff {
  private final ASTNode srcElem;
  private final ASTNode tgtElem;
  private List<DiffTypes> baseDiff;

  CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

  private String ppModifier1,
    ppType1,
    ppName1,
    ppModifier2,
    ppType2,
    ppName2,
    cd1SelfbuildString,
    cd2SelfbuildString,
    interpretation;

  public CDMemberDiff(ASTNode srcElem, ASTNode tgtElem) {
    this.srcElem = srcElem;
    this.tgtElem = tgtElem;
    if ((tgtElem instanceof ASTCDAttribute) && (srcElem instanceof ASTCDAttribute)) {
      createDiff((ASTCDAttribute) tgtElem, (ASTCDAttribute) srcElem);
    }
    if ((tgtElem instanceof ASTCDEnumConstant) && (srcElem instanceof ASTCDEnumConstant)) {
      createDiff((ASTCDEnumConstant) tgtElem, (ASTCDEnumConstant) srcElem);
    }
    this.diffSize = calculateDiffSize();
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

  private double calculateDiffSize() {
    double size = diffList.size() / 3.0;
    for (CDNodeDiff diff : diffList) {
      if (diff.isPresent() && diff.getTgtValue().isPresent()) {
        if (diff.getTgtValue().get() instanceof ASTCDAttribute
          || diff.getTgtValue().get() instanceof ASTMCQualifiedName
          || diff.getTgtValue().get() instanceof ASTCDClass) {
          size += 1.0 / 3.0;
        }
      }
    }
    return size;
  }

  public void createDiff(ASTCDAttribute cd1Element, ASTCDAttribute cd2Element) {
    this.diffList = createDiffList(cd1Element, cd2Element);
    setAttributeStrings();
  }

  public void createDiff(ASTCDEnumConstant cd1Element, ASTCDEnumConstant cd2Element) {
    this.diffList = createDiffList(cd1Element, cd2Element);
    setEnumStrings();
  }

  private List<CDNodeDiff<? extends ASTNode, ? extends ASTNode>> createDiffList(ASTCDAttribute cd1Element, ASTCDAttribute cd2Element) {

    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    List<CDNodeDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();

    // Modifier, non-optional
    if (!(pp.prettyprint(cd1Element.getModifier()).length() < 1 && pp.prettyprint(cd2Element.getModifier()).length() < 1)) {
      diffs.add(setModifier(cd1Element.getModifier(), cd2Element.getModifier()));
    }

    // MCType, non-optional
    Optional<ASTMCType> cd1Type = Optional.of(cd1Element.getMCType());
    Optional<ASTMCType> cd2Type = Optional.of(cd2Element.getMCType());
    CDNodeDiff<ASTMCType, ASTMCType> attributeType = new CDNodeDiff<>(cd1Type, cd2Type);
    if (attributeType.isPresent()) {
      diffs.add(attributeType);
    }
    ppType1 = ICDPrintDiff.getColorCode(attributeType) + pp.prettyprint(cd1Type.get()) + RESET;
    ppType2 = ICDPrintDiff.getColorCode(attributeType) + pp.prettyprint(cd2Type.get()) + RESET;

    // Name, non-optional
    Optional<ASTCDAttribute> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDAttribute> cd2Name = Optional.of(cd2Element);

    CDNodeDiff<ASTCDAttribute, ASTCDAttribute> attributeName =
      new CDNodeDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      attributeName = new CDNodeDiff<>(Actions.CHANGED, cd1Name, cd2Name);
    }

    if (attributeName.isPresent()) {
      diffs.add(attributeName);
    }
    ppName1 = ICDPrintDiff.getColorCode(attributeName) + cd1Name.get().getName() + RESET;
    ppName2 = ICDPrintDiff.getColorCode(attributeName) + cd2Name.get().getName() + RESET;

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

    if (name.isPresent()) {
      diffs.add(name);
    }

    ppName1 = ICDPrintDiff.getColorCode(name) + cd1Name.get().getName() + RESET;
    ppName2 = ICDPrintDiff.getColorCode(name) + cd2Name.get().getName() + RESET;

    return diffs;
  }

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

  protected CDNodeDiff<ASTModifier, ASTModifier> setModifier(
    ASTModifier cd1Modi, ASTModifier cd2Modi) {
    CDNodeDiff<ASTModifier, ASTModifier> modifier = new CDNodeDiff<>(Optional.of(cd1Modi), Optional.of(cd2Modi));

    if (!(pp.prettyprint(cd1Modi).length() < 1)) {
      ppModifier1 = ICDPrintDiff.getColorCode(modifier) + pp.prettyprint(cd1Modi) + RESET;
    }

    if (!(pp.prettyprint(cd2Modi).length() < 1)) {
      ppModifier2 = ICDPrintDiff.getColorCode(modifier) + pp.prettyprint(cd2Modi) + RESET;
    }

    return modifier;
  }

  private void setEnumStrings() {

    this.cd1SelfbuildString = ppName1;
    this.cd2SelfbuildString = ppName2;
    this.interpretation = "Interpretation: ";
  }

  private void setAttributeStrings() {

    this.cd1SelfbuildString = ICDPrintDiff.combineWithoutNulls(Arrays.asList(ppModifier1, ppType1, ppName1));
    this.cd2SelfbuildString = ICDPrintDiff.combineWithoutNulls(Arrays.asList(ppModifier2, ppType2, ppName2));
    this.interpretation = "Interpretation: ";
  }

  public String printCD1Element() {
    return cd1SelfbuildString;
  }

  public String printCD2Element() {
    return cd2SelfbuildString;
  }
}
