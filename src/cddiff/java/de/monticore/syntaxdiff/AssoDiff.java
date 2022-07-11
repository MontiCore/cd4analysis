package de.monticore.syntaxdiff;

import de.monticore.ast.ASTNode;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.syntaxdiff.SyntaxDiff.Op;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



/**
 * Diff Type for Associations
 * Use the constructor to create a diff between two associations
 * This diff type contains information extracted from the provided associations, especially all field changes
 */
public class AssoDiff extends AbstractDiffType{
  protected final ASTCDAssociation cd1Element;

  protected final ASTCDAssociation cd2Element;

  protected double diffSize;

  protected List<FieldDiff<? extends ASTNode>> diffList;

  public ASTCDAssociation getCd1Element() {
    return cd1Element;
  }

  public ASTCDAssociation getCd2Element() {
    return cd2Element;
  }

  public double getDiffSize() {
    return diffSize;
  }

  public List<FieldDiff<? extends ASTNode>> getDiffList() {
    return diffList;
  }

  private String ppStereo1, ppModifier1, ppName1, ppType1, ppDir1
    , ppOrderLeft1, ppModifierLeft1, ppCardLeft1, ppTypeLeft1, ppQualifierLeft1, ppRoleLeft1
    , ppOrderRight1, ppModifierRight1, ppCardRight1, ppTypeRight1, ppQualifierRight1, ppRoleRight1,
    ppStereo2, ppModifier2, ppName2, ppType2, ppDir2
    , ppOrderLeft2, ppModifierLeft2, ppCardLeft2, ppTypeLeft2, ppQualifierLeft2, ppRoleLeft2
    , ppOrderRight2, ppModifierRight2, ppCardRight2, ppTypeRight2, ppQualifierRight2, ppRoleRight2;

  /**
   * Constructor of the association diff type
   * @param cd1Element Association from the original model
   * @param cd2Element Association from the target(new) model
   */
  public AssoDiff(ASTCDAssociation cd1Element, ASTCDAssociation cd2Element) {
    this.cd1Element = cd1Element;
    this.cd2Element = cd2Element;

    // Set the required parts for diff size calculation
    assoDiff(cd1Element, cd2Element);

    this.diffSize = calculateDiffSize();
  }

  /**
   * Calculation of the diff size between the given associations, automaticly calculated on object creation
   * Name changes are weighted more
   * @return Diff size as int
   */
  private double calculateDiffSize(){
    double size = diffList.size();

    size += addWeightToDiffSize(diffList);

    return size;
  }

  /**
   * Main method of this class, calculates the differences between both associations using checks between every field
   * @param cd1Asso Association from the original model
   * @param cd2Asso Association from the target(new) model
   */
  private void assoDiff(ASTCDAssociation cd1Asso, ASTCDAssociation cd2Asso) {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    List<FieldDiff<? extends ASTNode>> diffs = new ArrayList<>();

    // Stereotype, optional
    Optional<ASTStereotype> cd1Stereo;
    if (cd1Asso.getModifier().isPresentStereotype()) cd1Stereo = Optional.of(cd1Asso.getModifier().getStereotype());
    else cd1Stereo = Optional.empty();

    Optional<ASTStereotype> cd2Stereo;
    if (cd2Asso.getModifier().isPresentStereotype()) cd2Stereo = Optional.of(cd2Asso.getModifier().getStereotype());
    else cd2Stereo = Optional.empty();

    FieldDiff<ASTStereotype> assoStereo = new FieldDiff<>(cd1Stereo, cd2Stereo);
    if (assoStereo.isPresent()){
      diffs.add(assoStereo);
    }
    cd1Stereo.ifPresent(astStereotype -> ppStereo1 = getColorCode(assoStereo) + pp.prettyprint(astStereotype));
    cd2Stereo.ifPresent(astStereotype -> ppStereo2 = getColorCode(assoStereo) + pp.prettyprint(astStereotype));

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Asso.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Asso.getModifier());
    FieldDiff<ASTModifier> assoModifier = new FieldDiff<>(cd1Modi, cd2Modi);
    if (assoModifier.isPresent()){
      diffs.add(assoModifier);
    }
    ppModifier1 = getColorCode(assoModifier) + pp.prettyprint(cd1Modi.get());
    ppModifier2 = getColorCode(assoModifier) + pp.prettyprint(cd2Modi.get());


    // Association Type, non-optional
    Optional<ASTCDAssocType> cd1AssoType = Optional.of(cd1Asso.getCDAssocType());
    Optional<ASTCDAssocType> cd2AssoType = Optional.of(cd2Asso.getCDAssocType());
    FieldDiff<ASTCDAssocType> assoType = new FieldDiff<>(cd1AssoType,cd2AssoType);
    if (assoType.isPresent()){
      diffs.add(assoType);
    }
    ppType1 = getColorCode(assoType) + pp.prettyprint(cd1AssoType.get());
    ppType2 = getColorCode(assoType) + pp.prettyprint(cd2AssoType.get());

    // for each association the sides can be exchanged (Direction must be changed appropriately)
    // Original direction (is prioritised for equal results)
    List<FieldDiff<? extends ASTNode>> tmpOriginalDir = new ArrayList<>(
      getAssocSideDiff(cd1Asso.getLeft(), cd2Asso.getLeft(), false));

    // Association Direction, non-optional
    Optional<ASTCDAssocDir> cd1AssoDir = Optional.of(cd1Asso.getCDAssocDir());
    Optional<ASTCDAssocDir> cd2AssoDir = Optional.of(cd2Asso.getCDAssocDir());
    FieldDiff<ASTCDAssocDir> assoDir1 = new FieldDiff<>(cd1AssoDir, cd2AssoDir);
    if (assoDir1.isPresent()){
      diffs.add(assoDir1);
    }

    tmpOriginalDir.addAll(getAssocSideDiff(cd1Asso.getRight(), cd2Asso.getRight(), false));

    // Reversed direction (exchange the input and use the reversed direction, only for directed)
    List<FieldDiff<? extends ASTNode>> tmpReverseDir = new ArrayList<>();
    tmpReverseDir.addAll(getAssocSideDiff(cd1Asso.getLeft(), cd2Asso.getRight(), false));

    // Todo: Add reversed AssoDir

    tmpReverseDir.addAll(getAssocSideDiff(cd1Asso.getRight(), cd2Asso.getLeft(), false));

    if (tmpOriginalDir.size() < tmpReverseDir.size()){
      diffs.addAll(tmpOriginalDir);
      getAssocSideDiff(cd1Asso.getLeft(), cd2Asso.getLeft(), true);
      ppDir1 = getColorCode(assoDir1) + pp.prettyprint(cd1AssoDir.get());
      ppDir2 = getColorCode(assoDir1) + pp.prettyprint(cd2AssoDir.get());
      getAssocSideDiff(cd1Asso.getRight(), cd2Asso.getRight(), true);
    } else {
      diffs.addAll(tmpReverseDir);
      getAssocSideDiff(cd1Asso.getLeft(), cd2Asso.getRight(), true);
      //ppDir1 = getColorCode(assoDir1) + pp.prettyprint(cd1AssoDir.get());
      //ppDir2 = getColorCode(assoDir1) + pp.prettyprint(cd2AssoDir.get());
      getAssocSideDiff(cd1Asso.getRight(), cd2Asso.getLeft(), true);
    }

    this.diffList = diffs;
  }

  /**
   * Help method for calculating the association diff because each association can be defined in two-ways
   * @param cd1Side Association side from the original model
   * @param cd2Side Association side from the target(new) model
   * @return List of FieldDiffs which are merged into the difflist of the main method
   */
  private List<FieldDiff<? extends ASTNode>> getAssocSideDiff(ASTCDAssocSide cd1Side, ASTCDAssocSide cd2Side, boolean setPP) {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    List<FieldDiff<? extends ASTNode>> diffs = new ArrayList<>();

    // Ordered, optional
    Optional<ASTCDOrdered> cd1Ordered = (cd1Side.isPresentCDOrdered()) ? Optional.of(cd1Side.getCDOrdered()) : Optional.empty();
    Optional<ASTCDOrdered> cd2Ordered = (cd2Side.isPresentCDOrdered()) ? Optional.of(cd2Side.getCDOrdered()) : Optional.empty();
    FieldDiff<ASTCDOrdered> assoOrdered = new FieldDiff<>(cd1Ordered, cd2Ordered);
    if (assoOrdered.isPresent()){
      diffs.add(assoOrdered);
    }

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Side.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Side.getModifier());
    FieldDiff<ASTModifier> assoModifier = new FieldDiff<>(cd1Modi, cd2Modi);
    if (assoModifier.isPresent()){
      diffs.add(assoModifier);
    }


    // Cardinality, optional
    Optional<ASTCDCardinality> cd1Card = (cd1Side.isPresentCDCardinality()) ? Optional.of(cd1Side.getCDCardinality()) : Optional.empty();
    Optional<ASTCDCardinality> cd2Card = (cd2Side.isPresentCDCardinality()) ? Optional.of(cd2Side.getCDCardinality()) : Optional.empty();
    FieldDiff<ASTCDCardinality> assoCard = new FieldDiff<>(cd1Card, cd2Card);
    if (assoCard.isPresent()){
      diffs.add(assoCard);
    }

    // QualifiedType, non-optional (participant in the association)
    Optional<ASTMCQualifiedName> cd1Type = Optional.of(cd1Side.getMCQualifiedType().getMCQualifiedName());
    Optional<ASTMCQualifiedName> cd2Type = Optional.of(cd2Side.getMCQualifiedType().getMCQualifiedName());
    FieldDiff<ASTMCQualifiedName> type = new FieldDiff<>(cd1Type, cd2Type);

    if (type.isPresent()){
      diffs.add(type);
    }

    // CDQualifier, optional
    Optional<ASTCDQualifier> cd1Quali = (cd1Side.isPresentCDQualifier()) ? Optional.of(cd1Side.getCDQualifier()) : Optional.empty();
    Optional<ASTCDQualifier> cd2Quali = (cd2Side.isPresentCDQualifier()) ? Optional.of(cd2Side.getCDQualifier()) : Optional.empty();
    FieldDiff<ASTCDQualifier> assoQuali = new FieldDiff<>(cd1Quali, cd2Quali);
    if (assoQuali.isPresent()){
      diffs.add(assoQuali);
    }

    // CDRole, optional
    Optional<ASTCDRole> cd1Role = (cd1Side.isPresentCDRole()) ? Optional.of(cd1Side.getCDRole()) : Optional.empty();
    Optional<ASTCDRole> cd2Role = (cd2Side.isPresentCDRole()) ? Optional.of(cd2Side.getCDRole()) : Optional.empty();
    FieldDiff<ASTCDRole> assoRole = new FieldDiff<>(cd1Role, cd2Role);
    if (assoRole.isPresent()){
      diffs.add(assoRole);
    }

    if (setPP) {
      if (cd1Side.isLeft()) {
        cd1Ordered.ifPresent(astOrdered -> ppOrderLeft1 = getColorCode(assoOrdered) + pp.prettyprint(astOrdered));
        ppModifierLeft1 = getColorCode(assoModifier) + pp.prettyprint(cd1Modi.get());
        cd1Card.ifPresent(astCardi -> ppCardLeft1 = getColorCode(assoCard) + pp.prettyprint(astCardi));
        ppTypeLeft1 = getColorCode(type) + pp.prettyprint(cd1Type.get());
        cd1Quali.ifPresent(astQuali -> ppQualifierLeft1 = getColorCode(assoQuali) + pp.prettyprint(astQuali));
        cd1Role.ifPresent(role -> ppRoleLeft1 = getColorCode(assoRole) + pp.prettyprint((ASTCDBasisNode) role));
      } else {
        cd1Ordered.ifPresent(astOrdered -> ppOrderRight1 = getColorCode(assoOrdered) + pp.prettyprint(astOrdered));
        ppModifierRight1 = getColorCode(assoModifier) + pp.prettyprint(cd1Modi.get());
        cd1Card.ifPresent(astCardi -> ppCardRight1 = getColorCode(assoCard) + pp.prettyprint(astCardi));
        ppTypeRight1 = getColorCode(type) + pp.prettyprint(cd1Type.get());
        cd1Quali.ifPresent(astQuali -> ppQualifierRight1 = getColorCode(assoQuali) + pp.prettyprint(astQuali));
        cd1Role.ifPresent(role -> ppRoleRight1 = getColorCode(assoRole) + pp.prettyprint((ASTCDBasisNode) role));
      }
      if (cd2Side.isLeft()) {
        cd2Ordered.ifPresent(astOrdered -> ppOrderLeft2 = getColorCode(assoOrdered) + pp.prettyprint(astOrdered));
        ppModifierLeft2 = getColorCode(assoModifier) + pp.prettyprint(cd2Modi.get());
        cd2Card.ifPresent(astCardi -> ppCardLeft2 = getColorCode(assoCard) + pp.prettyprint(astCardi));
        ppTypeLeft2 = getColorCode(type) + pp.prettyprint(cd2Type.get());
        cd2Quali.ifPresent(astQuali -> ppQualifierLeft2 = getColorCode(assoQuali) + pp.prettyprint(astQuali));
        cd2Role.ifPresent(role -> ppRoleLeft2 = getColorCode(assoRole) + pp.prettyprint((ASTCDBasisNode) role));
      } else {
        cd2Ordered.ifPresent(astOrdered -> ppOrderRight2 = getColorCode(assoOrdered) + pp.prettyprint(astOrdered));
        ppModifierRight2 = getColorCode(assoModifier) + pp.prettyprint(cd2Modi.get());
        cd2Card.ifPresent(astCardi -> ppCardRight2 = getColorCode(assoCard) + pp.prettyprint(astCardi));
        ppTypeRight2 = getColorCode(type) + pp.prettyprint(cd2Type.get());
        cd2Quali.ifPresent(astQuali -> ppQualifierRight2 = getColorCode(assoQuali) + pp.prettyprint(astQuali));
        cd2Role.ifPresent(role -> ppRoleRight2 = getColorCode(assoRole) + pp.prettyprint((ASTCDBasisNode) role));
      }
    }
    return diffs;
  }

  /**
   * Print function for the association diff, used to output the diffs appropriately formated
   */
  public StringBuilder print() {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    StringBuilder output = new StringBuilder();
    StringBuilder interpretation = new StringBuilder();
    interpretation.append("Interpretation: ");

    StringBuilder cd1AssoSelfbuild = new StringBuilder();
      //Signature
    cd1AssoSelfbuild
      .append(ppStereo1).append(" ")
      .append(ppModifier1).append(" ")
      .append(ppType1).append(" ")
      .append(ppName1).append(" ")

      // Left Side
        .append(ppOrderLeft1).append(" ")
        .append(ppModifierLeft1).append(" ")
        .append(ppCardLeft1).append(" ")
        .append(ppTypeLeft1).append(" ")
        .append(ppQualifierLeft1).append(" ")
        .append(ppRoleLeft1).append(" ")

      .append(ppDir1).append(" ")
      // Right Side
        .append(ppRoleRight1).append(" ")
        .append(ppQualifierRight1).append(" ")
        .append(ppTypeRight1).append(" ")
        .append(ppCardRight1).append(" ")
        .append(ppModifierRight1).append(" ")
        .append(ppOrderRight1)
      .append(";");
    String cd1AssoSelfbuildString = cd1AssoSelfbuild.toString().replace("null", "").replace("  "," ").replace("  "," ");

    StringBuilder cd2AssoSelfbuild = new StringBuilder();
    //Signature
    cd2AssoSelfbuild
      .append(ppStereo2).append(" ")
      .append(ppModifier2).append(" ")
      .append(ppType2).append(" ")
      .append(ppName2).append(" ")

      // Left Side
        .append(ppOrderLeft2).append(" ")
        .append(ppModifierLeft2).append(" ")
        .append(ppCardLeft2).append(" ")
        .append(ppTypeLeft2).append(" ")
        .append(ppQualifierLeft2).append(" ")
        .append(ppRoleLeft2).append(" ")

      .append(ppDir2).append(" ")
      // Right Side
        .append(ppRoleRight2).append(" ")
        .append(ppQualifierRight2).append(" ")
        .append(ppTypeRight2).append(" ")
        .append(ppCardRight2).append(" ")
        .append(ppModifierRight2).append(" ")
        .append(ppOrderRight2)
      .append(";");
    String cd2AssoSelfbuildString = cd2AssoSelfbuild.toString().replace("null", "").replace("  "," ").replace("  "," ");

    // Build Association Output
    output.append("Line Matched Association with diff score ").append(getDiffSize())
      .append(System.lineSeparator())
      .append(this.getCd1Element().get_SourcePositionStart().getLine())
      .append("   ").append(cd1AssoSelfbuildString)
      .append(System.lineSeparator())
      .append(this.getCd2Element().get_SourcePositionStart().getLine())
      .append("   ").append(cd2AssoSelfbuildString)
      .append(System.lineSeparator())
      .append(interpretation)
      .append(System.lineSeparator());

    //System.out.println(output);
    return output;
  }
}
