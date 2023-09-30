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

public class CDMemberDiff extends CDPrintDiff implements ICDMemberDiff {
  private final ASTNode srcElem;
  private final ASTNode tgtElem;
  private List<DiffTypes> baseDiff;
  //Print
  CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
  private String srcMemberModifier, srcMemberType, srcMemberName, addedMember;
  int srcLineOfCode;
  private String tgtMemberModifier, tgtMemberType, tgtMemberName;
  int tgtLineOfCode;
  private String srcMemberString, tgtMemberString, removedMember;
  //Print end

  public CDMemberDiff(ASTNode srcElem, ASTNode tgtElem) {
    this.srcElem = srcElem;
    this.tgtElem = tgtElem;
    this.baseDiff = new ArrayList<>();

    if ((srcElem instanceof ASTCDAttribute) && (tgtElem instanceof ASTCDAttribute)) {
      createDiffList((ASTCDAttribute) srcElem, (ASTCDAttribute) tgtElem);
      setMemberStrings();
    }

    if ((srcElem instanceof ASTCDEnumConstant) && (tgtElem instanceof ASTCDEnumConstant)) {
      createDiffList((ASTCDEnumConstant) srcElem, (ASTCDEnumConstant) tgtElem);
      setMemberStrings();
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
  /**
   * Creates a list of differences between two CD attributes (source and target).
   *
   * @param srcElem The source CD attribute.
   * @param tgtElem The target CD attribute.
   */
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

  /**
   * Creates a difference list for two ASTCDEnumConstant elements by setting their names and line numbers.
   *
   * @param srcElem The source ASTCDEnumConstant.
   * @param tgtElem The target ASTCDEnumConstant.
   */
  private void createDiffList(ASTCDEnumConstant srcElem, ASTCDEnumConstant tgtElem) {
    // Name
    srcMemberName = srcElem.getName() + RESET;
    tgtMemberName = tgtElem.getName() + RESET;

    srcLineOfCode = srcElem.get_SourcePositionStart().getLine();
    tgtLineOfCode = tgtElem.get_SourcePositionStart().getLine();
  }

  /**
   * Sets member strings for source and target CD attributes, and their added and removed representations.
   */
  private void setMemberStrings() {
    this.srcMemberString = "\t" + "//new, L: " + srcLineOfCode + System.lineSeparator() + "\t" + insertSpaceBetweenStrings(Arrays.asList(srcMemberModifier, srcMemberType, srcMemberName)) + "; ";
    this.tgtMemberString = "\t" + "//old, L: " + tgtLineOfCode + System.lineSeparator() + "\t" + insertSpaceBetweenStrings(Arrays.asList(tgtMemberModifier, tgtMemberType, tgtMemberName)) + "; ";
    this.addedMember = "\t" + insertSpaceBetweenStringsAndGreen(Arrays.asList(srcMemberModifier, srcMemberType, srcMemberName)) + COLOR_ADD + ";";
    this.removedMember = "\t" + insertSpaceBetweenStringsAndRed(Arrays.asList(tgtMemberModifier, tgtMemberType, tgtMemberName)) + COLOR_DELETE +  ";";
  }

  /**
   * Returns the source member string representation.
   *
   * @return The source member string.
   */
  public String printSrcMember() { return srcMemberString; }

  /**
   * Returns the added member representation.
   *
   * @return The added member representation.
   */
  public String printAddedMember() { return addedMember; }

  /**
   * Returns the target member string representation.
   *
   * @return The target member string.
   */
  public String printTgtMember() { return tgtMemberString; }

  /**
   * Returns the changed member representation, combining source and target member strings.
   *
   * @return The changed member representation.
   */
  public String printChangedMember() { return "//changed attribute" + System.lineSeparator() + srcMemberString + System.lineSeparator() + tgtMemberString; }

  /**
   * Returns the removed member representation.
   *
   * @return The removed member representation.
   */
  public String printRemovedMember() { return removedMember; }
}
