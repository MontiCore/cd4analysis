package de.monticore.syntaxdiff;

import de.monticore.ast.ASTNode;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDExtendUsage;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.syntaxdiff.SyntaxDiff.Op;

import java.util.ArrayList;
import java.util.Comparator;
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

  protected int diffSize;

  protected List<FieldDiff<? extends ASTNode>> diffList;

  public ASTCDAssociation getCd1Element() {
    return cd1Element;
  }

  public ASTCDAssociation getCd2Element() {
    return cd2Element;
  }

  public int getDiffSize() {
    return diffSize;
  }

  public List<FieldDiff<? extends ASTNode>> getDiffList() {
    return diffList;
  }


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
  private int calculateDiffSize(){
    int size = diffList.size();

    for (FieldDiff<? extends ASTNode> diff : diffList){
      if (diff.isPresent() && diff.getCd1Value().isPresent()){
        // Name Diffs are weighted doubled compared to every other diff
        // Parent Object in FieldDiff when we check the name of it (when there is no specific node for the name)
        if (diff.getCd1Value().get() instanceof ASTMCQualifiedName) {
          size += 1;
        }
      }
    }
    return size;
  }

  /**
   * Main method of this class, calculates the differences between both associations using checks between every field
   * @param cd1Asso Association from the original model
   * @param cd2Asso Association from the target(new) model
   */
  private void assoDiff(ASTCDAssociation cd1Asso, ASTCDAssociation cd2Asso) {

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

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Asso.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Asso.getModifier());
    FieldDiff<ASTModifier> assoModifier = new FieldDiff<>(cd1Modi, cd2Modi);
    if (assoModifier.isPresent()){
      diffs.add(assoModifier);
    }

    // Association Type, non-optional
    FieldDiff<ASTCDAssocType> assoType = new FieldDiff<>(Optional.of(cd1Asso.getCDAssocType()),
      Optional.of(cd2Asso.getCDAssocType()));
    if (assoType.isPresent()){
      diffs.add(assoType);
    }

    // for each association the sides can be exchanged (Direction must be changed appropriately)
    // Original direction (is prioritised for equal results)
    List<FieldDiff<? extends ASTNode>> tmpOriginalDir = new ArrayList<>(
      getAssocSideDiff(cd1Asso.getLeft(), cd2Asso.getLeft()));

    // Association Direction, non-optional
    FieldDiff<ASTCDAssocDir> assoDir1 = new FieldDiff<>(Optional.of(cd1Asso.getCDAssocDir()),
      Optional.of(cd2Asso.getCDAssocDir()));
    if (assoDir1.isPresent()){
      diffs.add(assoDir1);
    }

    tmpOriginalDir.addAll(getAssocSideDiff(cd1Asso.getRight(), cd2Asso.getRight()));

    // Reversed direction (exchange the input and use the reveres direction, only for directed)
    List<FieldDiff<? extends ASTNode>> tmpReverseDir = new ArrayList<>();
    tmpReverseDir.addAll(getAssocSideDiff(cd1Asso.getLeft(), cd2Asso.getRight()));

    // Todo: Add reversed AssoDir

    tmpReverseDir.addAll(getAssocSideDiff(cd1Asso.getRight(), cd2Asso.getLeft()));

    if (tmpOriginalDir.size() < tmpReverseDir.size()){
      diffs.addAll(tmpOriginalDir);
    } else {
      diffs.addAll(tmpReverseDir);
    }

    this.diffList = diffs;
  }

  /**
   * Help method for calculating the association diff because each association can be defined in two-ways
   * @param cd1Side Association side from the original model
   * @param cd2Side Association side from the target(new) model
   * @return List of FieldDiffs which are merged into the difflist of the main method
   */
  private static List<FieldDiff<? extends ASTNode>> getAssocSideDiff(ASTCDAssocSide cd1Side, ASTCDAssocSide cd2Side) {
    List<FieldDiff<? extends ASTNode>> diffs = new ArrayList<>();
    // Ordered, optional
    Optional<ASTCDOrdered> cd1Ordered = (cd1Side.isPresentCDOrdered()) ? Optional.of(cd1Side.getCDOrdered()) : Optional.empty();
    Optional<ASTCDOrdered> cd2Ordered = (cd2Side.isPresentCDOrdered()) ? Optional.of(cd2Side.getCDOrdered()) : Optional.empty();
    FieldDiff<ASTCDOrdered> assoOrdered = new FieldDiff<>(cd1Ordered, cd2Ordered);
    if (assoOrdered.isPresent()){
      diffs.add(assoOrdered);
    }

    // Modifier, non-optional
    FieldDiff<ASTModifier> modifier =new FieldDiff<>(Optional.of(cd1Side.getModifier()),Optional.of(cd2Side.getModifier()));

    if (modifier.isPresent()){
      diffs.add(modifier);
    }

    // Cardinality, optional
    Optional<ASTCDCardinality> cd1Card = (cd1Side.isPresentCDCardinality()) ? Optional.of(cd1Side.getCDCardinality()) : Optional.empty();
    Optional<ASTCDCardinality> cd2Card = (cd2Side.isPresentCDCardinality()) ? Optional.of(cd2Side.getCDCardinality()) : Optional.empty();
    FieldDiff<ASTCDCardinality> assoCard = new FieldDiff<>(cd1Card, cd2Card);
    if (assoCard.isPresent()){
      diffs.add(assoCard);
    }

    // QualifiedType, non-optional (participant in the association)
    FieldDiff<ASTMCQualifiedName> type =new FieldDiff<>(
      Optional.of(cd1Side.getMCQualifiedType().getMCQualifiedName()),
      Optional.of(cd2Side.getMCQualifiedType().getMCQualifiedName()));

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
    String cd1Asso = pp.prettyprint((ASTCDAssociationNode) getCd1Element());
    String cd2Asso = pp.prettyprint((ASTCDAssociationNode) getCd2Element());

    final String RESET = "\033[0m";
    for (FieldDiff<? extends ASTNode> x: diffList) {
      if (x.isPresent()) {
        String colorCode = "\033[1;33m"; // Bold White
        if (x.getOperation().isPresent()) {
          if (x.getOperation().get().equals(SyntaxDiff.Op.DELETE)) {
            colorCode = "\033[1;31m"; // Bold Red
          }
          if (x.getOperation().get().equals(SyntaxDiff.Op.ADD)) {
            colorCode = "\033[1;32m"; // Bold Green
          }
        }
        if (x.getCd1Value().isPresent() && x.getCd1pp().isPresent()) {
          String cd1pp = x.getCd1pp().get();
          if (cd1Asso.contains(cd1pp)) {
            cd1Asso = cd1Asso.replace(cd1pp, colorCode + cd1pp + RESET);
          }
        }
        if (x.getCd2Value().isPresent() && x.getCd2pp().isPresent()) {
          String cd2pp = x.getCd2pp().get();
          if (cd2Asso.contains(cd2pp)) {
            cd2Asso = cd2Asso.replace(cd2pp, colorCode + cd2pp + RESET);
          }
        }

        // Build Interpretation
        if (x.getInterpretation().isPresent()) {
          interpretation.append(x.getInterpretation().get()).append(" ");
        }
      }
    }



    // Build Association Output
    output.append("Line Matched Association with diff score ").append(getDiffSize())
      .append(System.lineSeparator())
      .append(this.getCd1Element().get_SourcePositionStart().getLine())
      .append("   ").append(cd1Asso)
      .append(this.getCd2Element().get_SourcePositionStart().getLine())
      .append("   ").append(cd2Asso)
      .append(interpretation)
      .append(System.lineSeparator());

    //System.out.println(output);
    return output;
  }
}
