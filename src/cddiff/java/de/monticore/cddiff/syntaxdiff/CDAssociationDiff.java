package de.monticore.cddiff.syntaxdiff;

import de.monticore.ast.ASTNode;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Diff Type for Associations Use the constructor to create a diff between two associations This
 * diff type contains information extracted from the provided associations, especially all field
 * changes
 */
public class CDAssociationDiff extends CDElementDiff {
  protected final ASTCDAssociation cd1Element;

  protected final ASTCDAssociation cd2Element;

  public ASTCDAssociation getCd1Element() {
    return cd1Element;
  }

  public ASTCDAssociation getCd2Element() {
    return cd2Element;
  }

  private String ppStereo1, ppModifier1, ppName1, ppType1, ppDir1, ppOrderLeft1, ppModifierLeft1,
      ppCardLeft1, ppTypeLeft1, ppQualifierLeft1, ppRoleLeft1, ppOrderRight1, ppModifierRight1,
      ppCardRight1, ppTypeRight1, ppQualifierRight1, ppRoleRight1, ppStereo2, ppModifier2,
      ppName2, ppType2, ppDir2, ppOrderLeft2, ppModifierLeft2, ppCardLeft2, ppTypeLeft2,
      ppQualifierLeft2, ppRoleLeft2, ppOrderRight2, ppModifierRight2, ppCardRight2, ppTypeRight2,
      ppQualifierRight2, ppRoleRight2, assoCD1, assoCD2, assoCD1NC, assoCD2NC;
  boolean directionChanged = false;
  private final CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
  /**
   * Constructor of the association diff type
   *
   * @param cd1Element Association from the original model
   * @param cd2Element Association from the target(new) model
   */
  public CDAssociationDiff(ASTCDAssociation cd1Element, ASTCDAssociation cd2Element) {
    this.cd1Element = cd1Element;
    this.cd2Element = cd2Element;

    // Set the required parts for diff size calculation
    assoDiff(cd1Element, cd2Element);

    this.diffSize = calculateDiffSize();
    setStrings();
    for (ASTNodeDiff<? extends ASTNode, ? extends ASTNode> diff : diffList) {
      if (diff.isPresent() && diff.getInterpretation().isPresent()) {
        interpretationList.add(diff.getInterpretation().get());
      }
    }

    setBreakingChange();

  }

  /**
   * Calculation of the diff size between two associations, automaticly calculated on object creation.
   * Name changes are weighted more, by triggering a weighting method.
   * @return Diff size as double
   */
  private double calculateDiffSize() {
    double size = diffList.size();

    size += addWeightToDiffSize(diffList);
    // Reduce diff size if the direction was reversed
    if (directionChanged){
      size -= 1;
    }

    return size;
  }

  /**
   * Check if the current diff contains breaking changes
   * If there are any a breaking change score is increased. The output can be sorted by this score.
   */
  private void setBreakingChange(){
    // Check if Rolename was changed in Navigation direction
    for (ASTNodeDiff<? extends ASTNode,? extends ASTNode> diff : diffList){
      ASTCDAssocDir asso1Dir = getCd1Element().getCDAssocDir();
      ASTCDAssocDir asso2Dir = getCd2Element().getCDAssocDir();
      if (diff.isPresent() && diff.getCd1Value().isPresent() && diff.getCd1Value().get() instanceof ASTCDRole){
        ASTCDRole cd1Role = (ASTCDRole) diff.getCd1Value().get();
        if ((getCd1Element().getLeft().isPresentCDRole()
          && getCd1Element().getLeft().getCDRole().deepEquals(cd1Role)
          && asso1Dir.isDefinitiveNavigableLeft()
          && asso2Dir.isDefinitiveNavigableLeft())
          || (getCd1Element().getRight().isPresentCDRole()
          && getCd1Element().getRight().getCDRole().deepEquals(cd1Role)
          && asso1Dir.isDefinitiveNavigableRight()
          && asso2Dir.isDefinitiveNavigableRight())){
          // Role was either deleted or changed in navigation direction
          breakingChange += 1;
          interpretationList.add(CDSyntaxDiff.Interpretation.BREAKINGCHANGE);
          interpretation.append("Rolename changed in navigation direction")
            .append(": ")
            .append(CDSyntaxDiff.Interpretation.BREAKINGCHANGE)
            .append(", ");
        }
      } else if (diff.isPresent() && diff.getCd2Value().isPresent() && diff.getCd2Value().get() instanceof ASTCDRole){
        ASTCDRole cd2Role = (ASTCDRole) diff.getCd2Value().get();
        if ((getCd2Element().getLeft().isPresentCDRole()
          && getCd2Element().getLeft().getCDRole().deepEquals(cd2Role)
          && asso1Dir.isDefinitiveNavigableLeft()
          && asso2Dir.isDefinitiveNavigableLeft())
          || (getCd2Element().getRight().isPresentCDRole()
          && getCd2Element().getRight().getCDRole().deepEquals(cd2Role)
          && asso1Dir.isDefinitiveNavigableRight()
          && asso2Dir.isDefinitiveNavigableRight())){
          // Role was either deleted or changed in navigation direction
          breakingChange += 1;
          interpretationList.add(CDSyntaxDiff.Interpretation.BREAKINGCHANGE);
          interpretation.append("Rolename changed in navigation direction")
            .append(": ")
            .append(CDSyntaxDiff.Interpretation.BREAKINGCHANGE)
            .append(", ");
        }
      }
    }
  }
  /**
   * Main method for calculation of the difference between two associations using checks
   * between every field
   * @param cd1Asso Association from the original(old) model
   * @param cd2Asso Association from the target(new) model
   */
  private void assoDiff(ASTCDAssociation cd1Asso, ASTCDAssociation cd2Asso) {
    this.assoCD1NC = pp.prettyprint((ASTCDAssociationNode) cd1Asso);
    this.assoCD2NC = pp.prettyprint((ASTCDAssociationNode) cd2Asso);

    if (assoCD1NC.contains("\n")) {
      assoCD1NC = assoCD1NC.split("\n")[0];
    }
    if (assoCD2NC.contains("\n")) {
      assoCD2NC = assoCD2NC.split("\n")[0];
    }


    List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();
    interpretation.append("Interpretation: ");

    // Stereotype, optional
    Optional<ASTStereotype> cd1Stereo;
    if (cd1Asso.getModifier().isPresentStereotype())
      cd1Stereo = Optional.of(cd1Asso.getModifier().getStereotype());
    else
      cd1Stereo = Optional.empty();

    Optional<ASTStereotype> cd2Stereo;
    if (cd2Asso.getModifier().isPresentStereotype())
      cd2Stereo = Optional.of(cd2Asso.getModifier().getStereotype());
    else
      cd2Stereo = Optional.empty();

    ASTNodeDiff<ASTStereotype, ASTStereotype> assoStereo = new ASTNodeDiff<>(cd1Stereo, cd2Stereo);
    if (assoStereo.isPresent()) {
      diffs.add(assoStereo);
      if (assoStereo.getInterpretation().isPresent()) {
        interpretation.append("Stereotype")
            .append(": ")
            .append(assoStereo.getInterpretation().get())
            .append(", ");
      }
    }
    cd1Stereo.ifPresent(
        astStereotype -> ppStereo1 = getColorCode(assoStereo) + pp.prettyprint(astStereotype) + RESET);
    cd2Stereo.ifPresent(
        astStereotype -> ppStereo2 = getColorCode(assoStereo) + pp.prettyprint(astStereotype) + RESET);


    // Modifier, non-optional
    if (!(pp.prettyprint(cd1Asso.getModifier()).length() < 1
      && pp.prettyprint(cd2Asso.getModifier()).length() < 1)){
      diffs.add(setModifier(cd1Asso.getModifier(), cd2Asso.getModifier()));
    }

    // Association Type, non-optional
    Optional<ASTCDAssocType> cd1AssoType = Optional.of(cd1Asso.getCDAssocType());
    Optional<ASTCDAssocType> cd2AssoType = Optional.of(cd2Asso.getCDAssocType());
    ASTNodeDiff<ASTCDAssocType, ASTCDAssocType> assoType = new ASTNodeDiff<>(cd1AssoType, cd2AssoType);
    if (assoType.isPresent()) {
      diffs.add(assoType);
      if (assoType.getInterpretation().isPresent()) {
        interpretation.append("Type")
            .append(": ")
            .append(assoType.getInterpretation().get())
            .append(", ");
      }
    }
    ppType1 = getColorCode(assoType) + pp.prettyprint(cd1AssoType.get()) + RESET;
    ppType2 = getColorCode(assoType) + pp.prettyprint(cd2AssoType.get()) + RESET;

    // for each association the sides can be exchanged (Direction must be changed appropriately)
    // Original direction (is prioritised for equal results)
    List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> tmpOriginalDir = new ArrayList<>(
        getAssocSideDiff(cd1Asso.getLeft(), cd2Asso.getLeft(), false));

    // Association Direction, non-optional
    Optional<ASTCDAssocDir> cd1AssoDir = Optional.of(cd1Asso.getCDAssocDir());
    Optional<ASTCDAssocDir> cd2AssoDir = Optional.of(cd2Asso.getCDAssocDir());
    ASTNodeDiff<ASTCDAssocDir, ASTCDAssocDir> assoDir1 = new ASTNodeDiff<>(cd1AssoDir, cd2AssoDir);
    if (assoDir1.isPresent()) {
      diffs.add(assoDir1);
      if (assoDir1.getInterpretation().isPresent()) {
        interpretation.append("Direction")
            .append(": ")
            .append(assoDir1.getInterpretation().get())
            .append(", ");
      }
    }

    // Check if direction '->' was changed to '<-' or '<-' to '->'.
    // If yes then add weight for calculating the smallest diff for each side combination.
    int weightDirection = 0;
    if ( (cd1AssoDir.get().isDefinitiveNavigableRight() && !cd1AssoDir.get().isDefinitiveNavigableLeft())
      && (!cd2AssoDir.get().isDefinitiveNavigableRight() && cd2AssoDir.get().isDefinitiveNavigableLeft())
      ||(!cd1AssoDir.get().isDefinitiveNavigableRight() && cd1AssoDir.get().isDefinitiveNavigableLeft())
      && (cd2AssoDir.get().isDefinitiveNavigableRight() && !cd2AssoDir.get().isDefinitiveNavigableLeft())){
      directionChanged = true;
      weightDirection = 1;
    }

    tmpOriginalDir.addAll(getAssocSideDiff(cd1Asso.getRight(), cd2Asso.getRight(), false));

    // Reversed direction (exchange the input and use the reversed direction, only for directed)
    List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> tmpReverseDir = new ArrayList<>();
    tmpReverseDir.addAll(getAssocSideDiff(cd1Asso.getLeft(), cd2Asso.getRight(), false));

    tmpReverseDir.addAll(getAssocSideDiff(cd1Asso.getRight(), cd2Asso.getLeft(), false));

    if ((tmpOriginalDir.size() + weightDirection) < tmpReverseDir.size()) {
      diffs.addAll(tmpOriginalDir);
      getAssocSideDiff(cd1Asso.getLeft(), cd2Asso.getLeft(), true);
      ppDir1 = getColorCode(assoDir1) + pp.prettyprint(cd1AssoDir.get()) + RESET;
      ppDir2 = getColorCode(assoDir1) + pp.prettyprint(cd2AssoDir.get()) + RESET;
      getAssocSideDiff(cd1Asso.getRight(), cd2Asso.getRight(), true);
      directionChanged = false;
    }
    else {
      diffs.addAll(tmpReverseDir);
      getAssocSideDiff(cd1Asso.getLeft(), cd2Asso.getRight(), true);
      if (directionChanged){
        ppDir1 = pp.prettyprint(cd1AssoDir.get());
        ppDir2 = pp.prettyprint(cd2AssoDir.get());
      }else {
        ppDir1 = getColorCode(assoDir1) + pp.prettyprint(cd1AssoDir.get()) + RESET;
        ppDir2 = getColorCode(assoDir1) + pp.prettyprint(cd2AssoDir.get()) + RESET;
      }

      getAssocSideDiff(cd1Asso.getRight(), cd2Asso.getLeft(), true);
    }

    this.diffList = diffs;
  }

  /**
   * Help method for calculating the association diff because each association can be defined in
   * two-ways
   *
   * @param cd1Side Association side from the original model
   * @param cd2Side Association side from the target(new) model
   * @return List of FieldDiffs which are merged into the difflist of the main method
   */
  private List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> getAssocSideDiff(
      ASTCDAssocSide cd1Side, ASTCDAssocSide cd2Side, boolean setPP) {
    List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();

    // Ordered, optional
    Optional<ASTCDOrdered> cd1Ordered = (cd1Side.isPresentCDOrdered()) ?
        Optional.of(cd1Side.getCDOrdered()) :
        Optional.empty();
    Optional<ASTCDOrdered> cd2Ordered = (cd2Side.isPresentCDOrdered()) ?
        Optional.of(cd2Side.getCDOrdered()) :
        Optional.empty();
    ASTNodeDiff<ASTCDOrdered, ASTCDOrdered> assoOrdered = new ASTNodeDiff<>(cd1Ordered, cd2Ordered);
    if (assoOrdered.isPresent()) {
      diffs.add(assoOrdered);
      if (assoOrdered.getInterpretation().isPresent() && setPP) {
        interpretation.append(cd1Side.isLeft() ? "Left " : "Right ");
        interpretation.append("Right ")
            .append("Ordered")
            .append(": ")
            .append(assoOrdered.getInterpretation().get())
            .append(", ");
      }
    }

    // Modifier, non-optional

    /*
    // Modifier, non-optional
    if (!(pp.prettyprint(cd1Side.getModifier()).length() < 1
      && pp.prettyprint(cd2Side.getModifier()).length() < 1)){
      diffs.add(setModifier(cd1Side.getModifier(), cd2Side.getModifier()));
    }

     */
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Side.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Side.getModifier());
    ASTNodeDiff<ASTModifier, ASTModifier> assoModifier = new ASTNodeDiff<>(cd1Modi, cd2Modi);
    if (assoModifier.isPresent()) {
      if (!(pp.prettyprint(cd1Side.getModifier()).length() < 1 && pp.prettyprint(cd2Side.getModifier()).length() < 1)){
        diffs.add(assoModifier);
        if (assoModifier.getInterpretation().isPresent() && setPP) {
          interpretation.append(cd1Side.isLeft() ? "Left " : "Right ");
          interpretation.append("Modifier")
            .append(": ")
            .append(assoModifier.getInterpretation().get())
            .append(", ");
        }
      }
    }

    // Cardinality, optional
    Optional<ASTCDCardinality> cd1Card = (cd1Side.isPresentCDCardinality()) ?
        Optional.of(cd1Side.getCDCardinality()) :
        Optional.empty();
    Optional<ASTCDCardinality> cd2Card = (cd2Side.isPresentCDCardinality()) ?
        Optional.of(cd2Side.getCDCardinality()) :
        Optional.empty();
    ASTNodeDiff<ASTCDCardinality, ASTCDCardinality> assoCard = new ASTNodeDiff<>(cd1Card, cd2Card);
    if (assoCard.isPresent()) {
      diffs.add(assoCard);
      if (assoCard.getInterpretation().isPresent() && setPP) {
        interpretation.append(cd1Side.isLeft() ? "Left " : "Right ");
        interpretation.append("Cardinality")
            .append(": ")
            .append(assoCard.getInterpretation().get())
            .append(", ");
      }
    }

    // QualifiedType, non-optional (participant in the association)
    Optional<ASTMCQualifiedName> cd1Type = Optional.of(
        cd1Side.getMCQualifiedType().getMCQualifiedName());
    Optional<ASTMCQualifiedName> cd2Type = Optional.of(
        cd2Side.getMCQualifiedType().getMCQualifiedName());
    ASTNodeDiff<ASTMCQualifiedName, ASTMCQualifiedName> type = new ASTNodeDiff<>(cd1Type, cd2Type);

    if (type.isPresent()) {
      diffs.add(type);
      if (type.getInterpretation().isPresent() && setPP) {
        interpretation.append(cd1Side.isLeft() ? "Left " : "Right ");
        interpretation.append("Name")
            .append(": ")
            .append(type.getInterpretation().get())
            .append(", ");
      }
    }

    // CDQualifier, optional
    Optional<ASTCDQualifier> cd1Quali = (cd1Side.isPresentCDQualifier()) ?
        Optional.of(cd1Side.getCDQualifier()) :
        Optional.empty();
    Optional<ASTCDQualifier> cd2Quali = (cd2Side.isPresentCDQualifier()) ?
        Optional.of(cd2Side.getCDQualifier()) :
        Optional.empty();
    ASTNodeDiff<ASTCDQualifier, ASTCDQualifier> assoQuali = new ASTNodeDiff<>(cd1Quali, cd2Quali);
    if (assoQuali.isPresent()) {
      diffs.add(assoQuali);
      if (assoQuali.getInterpretation().isPresent() && setPP) {
        interpretation.append(cd1Side.isLeft() ? "Left " : "Right ");
        interpretation.append("Qualifier")
            .append(": ")
            .append(assoQuali.getInterpretation().get())
            .append(", ");
      }
    }

    // CDRole, optional
    Optional<ASTCDRole> cd1Role = (cd1Side.isPresentCDRole()) ?
        Optional.of(cd1Side.getCDRole()) :
        Optional.empty();
    Optional<ASTCDRole> cd2Role = (cd2Side.isPresentCDRole()) ?
        Optional.of(cd2Side.getCDRole()) :
        Optional.empty();
    ASTNodeDiff<ASTCDRole, ASTCDRole> assoRole = new ASTNodeDiff<>(cd1Role, cd2Role);
    if (assoRole.isPresent()) {
      diffs.add(assoRole);
      if (assoRole.getInterpretation().isPresent() && setPP) {
        interpretation.append(cd1Side.isLeft() ? "Left " : "Right ");
        interpretation.append("Role")
            .append(": ")
            .append(assoRole.getInterpretation().get())
            .append(", ");
      }
    }

    if (setPP) {
      if (cd1Side.isLeft()) {
        cd1Ordered.ifPresent(
            astOrdered -> ppOrderLeft1 = getColorCode(assoOrdered) + pp.prettyprint(astOrdered) + RESET);
        if (! (pp.prettyprint(cd1Modi.get()).length() < 1)){
          ppModifierLeft1 = getColorCode(assoModifier) + pp.prettyprint(cd1Modi.get()) + RESET;
        }
        cd1Card.ifPresent(
            astCardi -> ppCardLeft1 = getColorCode(assoCard) + pp.prettyprint(astCardi) + RESET);
        ppTypeLeft1 = getColorCode(type) + pp.prettyprint(cd1Type.get()) + RESET;
        cd1Quali.ifPresent(
            astQuali -> ppQualifierLeft1 = getColorCode(assoQuali) + pp.prettyprint(astQuali) + RESET);
        cd1Role.ifPresent(
            role -> ppRoleLeft1 = getColorCode(assoRole) + pp.prettyprint((ASTCDBasisNode) role) + RESET);
      }
      else {
        cd1Ordered.ifPresent(
            astOrdered -> ppOrderRight1 = getColorCode(assoOrdered) + pp.prettyprint(astOrdered) + RESET);
        if (! (pp.prettyprint(cd1Modi.get()).length() < 1)){
          ppModifierRight1 = getColorCode(assoModifier) + pp.prettyprint(cd1Modi.get()) + RESET;
        }
        cd1Card.ifPresent(
            astCardi -> ppCardRight1 = getColorCode(assoCard) + pp.prettyprint(astCardi) + RESET);
        ppTypeRight1 = getColorCode(type) + pp.prettyprint(cd1Type.get()) + RESET;
        cd1Quali.ifPresent(
            astQuali -> ppQualifierRight1 = getColorCode(assoQuali) + pp.prettyprint(astQuali) + RESET);
        cd1Role.ifPresent(
            role -> ppRoleRight1 = getColorCode(assoRole) + pp.prettyprint((ASTCDBasisNode) role) + RESET);
      }
      if (cd2Side.isLeft()) {
        cd2Ordered.ifPresent(
            astOrdered -> ppOrderLeft2 = getColorCode(assoOrdered) + pp.prettyprint(astOrdered) + RESET);
        if (! (pp.prettyprint(cd2Modi.get()).length() < 1)){
          ppModifierLeft2 = getColorCode(assoModifier) + pp.prettyprint(cd2Modi.get()) + RESET;
        }
        cd2Card.ifPresent(
            astCardi -> ppCardLeft2 = getColorCode(assoCard) + pp.prettyprint(astCardi) + RESET);
        ppTypeLeft2 = getColorCode(type) + pp.prettyprint(cd2Type.get()) + RESET;
        cd2Quali.ifPresent(
            astQuali -> ppQualifierLeft2 = getColorCode(assoQuali) + pp.prettyprint(astQuali) + RESET);
        cd2Role.ifPresent(
            role -> ppRoleLeft2 = getColorCode(assoRole) + pp.prettyprint((ASTCDBasisNode) role) + RESET);
      }
      else {
        cd2Ordered.ifPresent(
            astOrdered -> ppOrderRight2 = getColorCode(assoOrdered) + pp.prettyprint(astOrdered) + RESET);
        if (! (pp.prettyprint(cd2Modi.get()).length() < 1)){
          ppModifierRight2 = getColorCode(assoModifier) + pp.prettyprint(cd2Modi.get()) + RESET;
        }
        cd2Card.ifPresent(
            astCardi -> ppCardRight2 = getColorCode(assoCard) + pp.prettyprint(astCardi) + RESET);
        ppTypeRight2 = getColorCode(type) + pp.prettyprint(cd2Type.get()) + RESET;
        cd2Quali.ifPresent(
            astQuali -> ppQualifierRight2 = getColorCode(assoQuali) + pp.prettyprint(astQuali) + RESET);
        cd2Role.ifPresent(
            role -> ppRoleRight2 = getColorCode(assoRole) + pp.prettyprint((ASTCDBasisNode) role) + RESET);
      }
    }
    return diffs;
  }

  /**
   * Help function to reduce code reusing. Create a modifier diff and sets print and interpretation strings.
   * @param cd1Modi Modifier of the first model
   * @param cd2Modi Modifier of the second model
   * @return ASTNodeDiff of type ASTModifier
   */

  protected ASTNodeDiff<ASTModifier, ASTModifier> setModifier (ASTModifier cd1Modi, ASTModifier cd2Modi){
    ASTNodeDiff<ASTModifier, ASTModifier> modifier = new ASTNodeDiff<>(Optional.of(cd1Modi), Optional.of(cd2Modi));

    if (! (pp.prettyprint(cd1Modi).length() < 1)){
      ppModifier1 = getColorCode(modifier) + pp.prettyprint(cd1Modi) + RESET;
    }
    if (! (pp.prettyprint(cd2Modi).length() < 1)){
      ppModifier2 = getColorCode(modifier) + pp.prettyprint(cd2Modi) + RESET;
    }

    if (modifier.isPresent() && modifier.getOperation().isPresent()) {
      if (modifier.getInterpretation().isPresent()) {
        interpretation.append("Modifier")
          .append(": ")
          .append(modifier.getInterpretation().get())
          .append(" ");
      }
    }
    return modifier;
  }

  private void setStrings() {
    // Build CD1 String
    assoCD1 = combineWithoutNulls(Arrays.asList(
      // Signature
      ppStereo1, ppModifier1, ppType1, ppName1,
      // Left Side
      ppOrderLeft1, ppModifierLeft1, ppCardLeft1, ppTypeLeft1, ppQualifierLeft1, ppRoleLeft1,
      // Direction
      ppDir1,
      // Right Side
      ppRoleRight1, ppQualifierRight1, ppTypeRight1, ppCardRight1, ppModifierRight1, ppOrderRight1));

    // Build CD2 String
    assoCD2 = combineWithoutNulls(Arrays.asList(
      // Signature
      ppStereo2, ppModifier2, ppType2, ppName2,
      // Left Side
      ppOrderLeft2, ppModifierLeft2, ppCardLeft2, ppTypeLeft2, ppQualifierLeft2, ppRoleLeft2,
      // Direction
      ppDir2,
      // Right Side
      ppRoleRight2, ppQualifierRight2, ppTypeRight2, ppCardRight2, ppModifierRight2, ppOrderRight2));
  }

  @Override
  public String combineWithoutNulls(List<String> stringList) {
    return super.combineWithoutNulls(stringList) + ";";
  }
  /**
   * Print function for the association diff, used to output the diffs appropriately formated
   */
  public String printCD1() {
    return assoCD1;
  }

  public String printCD2() {
    return assoCD2;
  }

  public String printCD1NC() {
    return assoCD1NC;
  }

  public String printCD2NC() {
    return assoCD2NC;
  }

}
