package de.monticore.syntaxdiff;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd4codebasis._ast.ASTCDThrowsDeclaration;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.*;

/**
 * Diff Type for Elements (all-purpose usage for ASTNodes) Use the constructor to create a diff
 * between two ASTNode Elements (classes, associations enums...) This diff type contains information
 * extracted from the provided elements
 */
public class ElementDiff<ASTNodeType extends ASTNode> extends AbstractDiffType {
  CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

  protected final ASTNodeType cd1Element;

  protected final ASTNodeType cd2Element;

  public ASTNodeType getCd1Element() {
    return cd1Element;
  }

  public ASTNodeType getCd2Element() {
    return cd2Element;
  }

  private String ppModifier1, ppType1, ppName1, ppInitial1, ppReturn1, ppThrow1, ppExpres1,
      ppModifier2, ppType2, ppName2, ppInitial2, ppReturn2, ppThrow2, ppExpres2,
      cd1SelfbuildString, cd2SelfbuildString, interpretation;

  /**
   * Helper function to determine the color code according to the operation recognized
   *
   * @param diff Fielddiff which contains the operation from the lowest level(fields)
   * @return Color code as String (Set this String directly before the to-be-colored String)
   */
  protected static String getColorCode(FieldDiff<? extends ASTNode, ? extends ASTNode> diff) {
    if (diff.getOperation().isPresent()) {
      if (diff.getOperation().get().equals(SyntaxDiff.Op.DELETE)) {
        return COLOR_DELETE;
      }
      else if (diff.getOperation().get().equals(SyntaxDiff.Op.ADD)) {
        return COLOR_ADD;
      }
      else if (diff.getOperation().get().equals(SyntaxDiff.Op.CHANGE)) {
        return COLOR_CHANGE;
      }
    }
    // No Operation
    return RESET;
  }

  /**
   * Constructor of the element diff type
   *
   * @param cd1Element Element from the original model
   * @param cd2Element Element from the target(new) model
   */
  public ElementDiff(ASTNodeType cd1Element, ASTNodeType cd2Element) {
    this.cd1Element = cd1Element;
    this.cd2Element = cd2Element;
    if ((cd1Element instanceof ASTCDAttribute) && (cd2Element instanceof ASTCDAttribute)) {
      createDiff((ASTCDAttribute) cd1Element, (ASTCDAttribute) cd2Element);
    }
    if ((cd1Element instanceof ASTCDMethod) && (cd2Element instanceof ASTCDMethod)) {
      createDiff((ASTCDMethod) cd1Element, (ASTCDMethod) cd2Element);
    }
    if ((cd1Element instanceof ASTCDConstructor) && (cd2Element instanceof ASTCDConstructor)) {
      createDiff((ASTCDConstructor) cd1Element, (ASTCDConstructor) cd2Element);
    }
    if ((cd1Element instanceof ASTCDEnumConstant) && (cd2Element instanceof ASTCDEnumConstant)) {
      createDiff((ASTCDEnumConstant) cd1Element, (ASTCDEnumConstant) cd2Element);
    }
    this.diffSize = calculateDiffSize();
  }

  private double calculateDiffSize() {
    double size = diffList.size() / 3.0;

    for (FieldDiff<? extends ASTNode, ? extends ASTNode> diff : diffList) {
      if (diff.isPresent() && diff.getCd1Value().isPresent()) {
        // Name Diffs are weighted doubled compared to every other diff
        // Parent Object in FieldDiff when we check the name of it (when there is no specific
        // node for the name)
        if (diff.getCd1Value().get() instanceof ASTCDAttribute || diff.getCd1Value()
            .get() instanceof ASTMCQualifiedName || diff.getCd1Value()
            .get() instanceof ASTCDClass) {
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

  public void createDiff(ASTCDConstructor cd1Element, ASTCDConstructor cd2Element) {
    this.diffList = createDiffList(cd1Element, cd2Element);
    setConstructorStrings();
  }

  public void createDiff(ASTCDMethod cd1Element, ASTCDMethod cd2Element) {
    this.diffList = createDiffList(cd1Element, cd2Element);
    setMethodStrings();
  }

  public void createDiff(ASTCDEnumConstant cd1Element, ASTCDEnumConstant cd2Element) {
    this.diffList = createDiffList(cd1Element, cd2Element);
    setEnumStrings();
  }

  private List<FieldDiff<? extends ASTNode, ? extends ASTNode>> createDiffList(
      ASTCDAttribute cd1Element, ASTCDAttribute cd2Element) {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    List<FieldDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Element.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Element.getModifier());
    FieldDiff<ASTModifier, ASTModifier> attributeModifier = new FieldDiff<>(cd1Modi, cd2Modi);
    if (attributeModifier.isPresent()) {
      diffs.add(attributeModifier);
    }
    ppModifier1 = getColorCode(attributeModifier) + pp.prettyprint(cd1Modi.get()) + RESET;
    ppModifier2 = getColorCode(attributeModifier) + pp.prettyprint(cd2Modi.get()) + RESET;

    // MCType, non-optional
    Optional<ASTMCType> cd1Type = Optional.of(cd1Element.getMCType());
    Optional<ASTMCType> cd2Type = Optional.of(cd2Element.getMCType());
    FieldDiff<ASTMCType, ASTMCType> attributeType = new FieldDiff<>(cd1Type, cd2Type);
    if (attributeType.isPresent()) {
      diffs.add(attributeType);
    }
    ppType1 = getColorCode(attributeType) + pp.prettyprint(cd1Type.get()) + RESET;
    ppType2 = getColorCode(attributeType) + pp.prettyprint(cd2Type.get()) + RESET;

    // Name, non-optional
    Optional<ASTCDAttribute> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDAttribute> cd2Name = Optional.of(cd2Element);
    FieldDiff<ASTCDAttribute, ASTCDAttribute> attributeName = new FieldDiff<>(null, cd1Name,
        cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      attributeName = new FieldDiff<>(SyntaxDiff.Op.CHANGE, cd1Name, cd2Name);
    }

    if (attributeName.isPresent()) {
      diffs.add(attributeName);
    }
    ppName1 = getColorCode(attributeName) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(attributeName) + cd2Name.get().getName() + RESET;

    // Initial expression, optional
    Optional<ASTExpression> cd1Initial = (cd1Element.isPresentInitial()) ?
        Optional.of(cd1Element.getInitial()) :
        Optional.empty();
    Optional<ASTExpression> cd2Initial = (cd2Element.isPresentInitial()) ?
        Optional.of(cd2Element.getInitial()) :
        Optional.empty();
    FieldDiff<ASTExpression, ASTExpression> attributeInital = new FieldDiff<>(cd1Initial,
        cd2Initial);
    if (attributeInital.isPresent()) {
      diffs.add(attributeInital);
    }
    cd1Initial.ifPresent(initial -> ppInitial1 =
        "= " + getColorCode(attributeInital) + pp.prettyprint(initial) + RESET);
    cd2Initial.ifPresent(initial -> ppInitial2 =
        "= " + getColorCode(attributeInital) + pp.prettyprint(initial) + RESET);

    return diffs;
  }

  private List<FieldDiff<? extends ASTNode, ? extends ASTNode>> createDiffList(
      ASTCDMethod cd1Element, ASTCDMethod cd2Element) {

    List<FieldDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Element.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Element.getModifier());
    FieldDiff<ASTModifier, ASTModifier> modifier = new FieldDiff<>(cd1Modi, cd2Modi);
    if (modifier.isPresent()) {
      diffs.add(modifier);
    }
    ppModifier1 = getColorCode(modifier) + pp.prettyprint(cd1Modi.get()) + RESET;
    ppModifier2 = getColorCode(modifier) + pp.prettyprint(cd2Modi.get()) + RESET;

    // ASTMCReturnType, non-optional
    Optional<ASTMCReturnType> cd1ReturnType = Optional.of(cd1Element.getMCReturnType());
    Optional<ASTMCReturnType> cd2ReturnType = Optional.of(cd2Element.getMCReturnType());
    FieldDiff<ASTMCReturnType, ASTMCReturnType> methodeReturnType = new FieldDiff<>(cd1ReturnType,
        cd2ReturnType);
    if (methodeReturnType.isPresent()) {
      diffs.add(methodeReturnType);
    }
    ppReturn1 = getColorCode(methodeReturnType) + pp.prettyprint(cd1ReturnType.get()) + RESET;
    ppReturn2 = getColorCode(methodeReturnType) + pp.prettyprint(cd2ReturnType.get()) + RESET;

    // Name, non-optional
    Optional<ASTCDMethod> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDMethod> cd2Name = Optional.of(cd2Element);
    FieldDiff<ASTCDMethod, ASTCDMethod> methodeName = new FieldDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      methodeName = new FieldDiff<>(SyntaxDiff.Op.CHANGE, cd1Name, cd2Name);
    }

    if (methodeName.isPresent()) {
      diffs.add(methodeName);
    }
    ppName1 = getColorCode(methodeName) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(methodeName) + cd2Name.get().getName() + RESET;

    // ThrowsDeclaration, optional
    Optional<ASTCDThrowsDeclaration> cd1ThrowDec = (cd1Element.isPresentCDThrowsDeclaration()) ?
        Optional.of(cd1Element.getCDThrowsDeclaration()) :
        Optional.empty();
    Optional<ASTCDThrowsDeclaration> cd2ThrowDec = (cd2Element.isPresentCDThrowsDeclaration()) ?
        Optional.of(cd2Element.getCDThrowsDeclaration()) :
        Optional.empty();
    FieldDiff<ASTCDThrowsDeclaration, ASTCDThrowsDeclaration> throwDecl = new FieldDiff<>(
        cd1ThrowDec, cd2ThrowDec);
    if (throwDecl.isPresent()) {
      diffs.add(throwDecl);
    }
    cd1ThrowDec.ifPresent(
        throwDec -> ppThrow1 = getColorCode(throwDecl) + pp.prettyprint(throwDec) + RESET);
    cd2ThrowDec.ifPresent(
        throwDec -> ppThrow2 = getColorCode(throwDecl) + pp.prettyprint(throwDec) + RESET);

    // Parameter List Diff, non-optional (List is empty is no Parameter is present)
    //List<List<ElementDiff<ASTCDParameter>>> list = getElementDiffList(cd1Element
    // .getCDParameterList(), cd2Element.getCDParameterList());
    // Todo: Save Parameter Diff in an appropriate way
    return diffs;
  }

  private List<FieldDiff<? extends ASTNode, ? extends ASTNode>> createDiffList(
      ASTCDEnumConstant cd1Element, ASTCDEnumConstant cd2Element) {
    List<FieldDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();

    // Name, non-optional
    Optional<ASTCDEnumConstant> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDEnumConstant> cd2Name = Optional.of(cd2Element);
    FieldDiff<ASTCDEnumConstant, ASTCDEnumConstant> name = new FieldDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      name = new FieldDiff<>(SyntaxDiff.Op.CHANGE, cd1Name, cd2Name);
    }

    if (name.isPresent()) {
      diffs.add(name);
    }
    ppName1 = getColorCode(name) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(name) + cd2Name.get().getName() + RESET;

    return diffs;
  }

  private List<FieldDiff<? extends ASTNode, ? extends ASTNode>> createDiffList(
      ASTCDConstructor cd1Element, ASTCDConstructor cd2Element) {
    List<FieldDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Element.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Element.getModifier());
    FieldDiff<ASTModifier, ASTModifier> modifier = new FieldDiff<>(cd1Modi, cd2Modi);
    if (modifier.isPresent()) {
      diffs.add(modifier);
    }
    ppModifier1 = getColorCode(modifier) + pp.prettyprint(cd1Modi.get()) + RESET;
    ppModifier2 = getColorCode(modifier) + pp.prettyprint(cd2Modi.get()) + RESET;

    // Name, non-optional
    Optional<ASTCDConstructor> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDConstructor> cd2Name = Optional.of(cd2Element);
    FieldDiff<ASTCDConstructor, ASTCDConstructor> constructorName = new FieldDiff<>(null, cd1Name,
        cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      constructorName = new FieldDiff<>(SyntaxDiff.Op.CHANGE, cd1Name, cd2Name);
    }

    if (constructorName.isPresent()) {
      diffs.add(constructorName);
    }
    ppName1 = getColorCode(constructorName) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(constructorName) + cd2Name.get().getName() + RESET;

    // ThrowsDeclaration, optional
    Optional<ASTCDThrowsDeclaration> cd1ThrowDec = (cd1Element.isPresentCDThrowsDeclaration()) ?
        Optional.of(cd1Element.getCDThrowsDeclaration()) :
        Optional.empty();
    Optional<ASTCDThrowsDeclaration> cd2ThrowDec = (cd2Element.isPresentCDThrowsDeclaration()) ?
        Optional.of(cd2Element.getCDThrowsDeclaration()) :
        Optional.empty();
    FieldDiff<ASTCDThrowsDeclaration, ASTCDThrowsDeclaration> throwDecl = new FieldDiff<>(
        cd1ThrowDec, cd2ThrowDec);
    if (throwDecl.isPresent()) {
      diffs.add(throwDecl);
    }
    cd1ThrowDec.ifPresent(
        throwDec -> ppThrow1 = getColorCode(throwDecl) + pp.prettyprint(throwDec) + RESET);
    cd2ThrowDec.ifPresent(
        throwDec -> ppThrow2 = getColorCode(throwDecl) + pp.prettyprint(throwDec) + RESET);

    // Parameter List Diff, non-optional (List is empty is no Parameter is present)
    //List<List<ElementDiff<ASTCDParameter>>> list = getElementDiffList(cd1Member
    // .getCDParameterList(), cd2Member.getCDParameterList());
    // Todo: Save Parameter Diff in an appropriate way
    return diffs;
  }

  /**
   * Help method for calculating the class diff because each classcontains multiple attributes which
   * need to be matched
   *
   * @param cd1Element List of attributes from the original model
   * @param cd2Element List of attributes from the target(new) model
   * @return Returns a difflist for each attribute, ordered by diffsize (small diff values ==
   * similar)
   */

  //MCType (ellipsis:["..."])? Name ("=" defaultValue:Expression)?;
  private List<FieldDiff<? extends ASTNode, ? extends ASTNode>> createDiffList(
      ASTCDParameter cd1Element, ASTCDParameter cd2Element) {
    List<FieldDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();

    // MCType, non-optional
    Optional<ASTMCType> cd1Type = Optional.of(cd1Element.getMCType());
    Optional<ASTMCType> cd2Type = Optional.of(cd2Element.getMCType());
    FieldDiff<ASTMCType, ASTMCType> eleType = new FieldDiff<>(cd1Type, cd2Type);
    if (eleType.isPresent()) {
      diffs.add(eleType);
    }
    ppType1 = getColorCode(eleType) + pp.prettyprint(cd1Type.get()) + RESET;
    ppType2 = getColorCode(eleType) + pp.prettyprint(cd2Type.get()) + RESET;

    // Name, non-optional
    Optional<ASTCDParameter> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDParameter> cd2Name = Optional.of(cd2Element);
    FieldDiff<ASTCDParameter, ASTCDParameter> name = new FieldDiff<>(cd1Name, cd2Name);
    if (name.isPresent()) {
      diffs.add(name);
    }
    ppName1 = getColorCode(name) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(name) + cd2Name.get().getName() + RESET;

    // Default Value, optional
    Optional<ASTExpression> cd1Default = (cd1Element.isPresentDefaultValue()) ?
        Optional.of(cd1Element.getDefaultValue()) :
        Optional.empty();
    Optional<ASTExpression> cd2Default = (cd2Element.isPresentDefaultValue()) ?
        Optional.of(cd2Element.getDefaultValue()) :
        Optional.empty();
    FieldDiff<ASTExpression, ASTExpression> parameterDefault = new FieldDiff<>(cd1Default,
        cd2Default);
    if (parameterDefault.isPresent()) {
      diffs.add(parameterDefault);
    }
    cd1Default.ifPresent(defExpres -> ppExpres1 =
        getColorCode(parameterDefault) + pp.prettyprint(defExpres) + RESET);
    cd2Default.ifPresent(defExpres -> ppExpres2 =
        getColorCode(parameterDefault) + pp.prettyprint(defExpres) + RESET);

    return diffs;
  }

  private void setEnumStrings() {

    this.cd1SelfbuildString = ppName1;

    this.cd2SelfbuildString = ppName2;

    this.interpretation = "Interpretation: ";
  }

  private void setAttributeStrings() {

    this.cd1SelfbuildString = combineWithoutNulls(
        Arrays.asList(ppModifier1, ppType1, ppName1, ppInitial1));

    this.cd2SelfbuildString = combineWithoutNulls(
        Arrays.asList(ppModifier2, ppType2, ppName2, ppInitial2));

    this.interpretation = "Interpretation: ";
  }

  //Methods: Modifier MCReturnType Name "(" (CDParameter || ",")* ")" CDThrowsDeclaration? ";";
  private void setMethodStrings() {
    //Signature
    this.cd1SelfbuildString = combineWithoutNulls(
        Arrays.asList(ppModifier1, ppReturn1, ppName1, "(", ") ", ppThrow1));
    this.cd2SelfbuildString = combineWithoutNulls(
        Arrays.asList(ppModifier2, ppReturn2, ppName2, "(", ") ", ppThrow2));

    this.interpretation = "Interpretation: ";
  }

  private void setConstructorStrings() {
    this.cd1SelfbuildString = combineWithoutNulls(
        Arrays.asList(ppModifier1, ppName1, "(", ") ", ppThrow1));
    this.cd2SelfbuildString = combineWithoutNulls(
        Arrays.asList(ppModifier2, ppName2, "(", ") ", ppThrow2));

    this.interpretation = "Interpretation: ";
  }

  @Override
  protected String combineWithoutNulls(List<String> stringList) {
    return super.combineWithoutNulls(stringList) + ";";
  }

  public String printCD1Element() {
    return cd1SelfbuildString;
  }

  public String printCD2Element() {
    return cd2SelfbuildString;
  }

}
