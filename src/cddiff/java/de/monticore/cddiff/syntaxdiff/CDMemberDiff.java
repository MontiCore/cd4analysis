/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syntaxdiff;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
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
import java.util.stream.Collectors;

/**
 * Diff Type for Elements (all-purpose usage for ASTNodes) Use the constructor to create a diff
 * between two ASTNode Elements (classes, associations enums...) This diff type contains information
 * extracted from the provided elements
 */
public class CDMemberDiff<ASTNodeType extends ASTNode> extends CDElementDiff {
  CD4CodeFullPrettyPrinter pp5252 = new CD4CodeFullPrettyPrinter(new IndentPrinter());

  protected final ASTNodeType cd1Element;

  protected final ASTNodeType cd2Element;

  protected List<CDMemberDiff<ASTCDParameter>> matchedParameter;

  protected List<ASTCDParameter> addedParameter;

  protected List<ASTCDParameter> deletedParameter;

  public ASTNodeType getCd1Element() {
    return cd1Element;
  }

  public ASTNodeType getCd2Element() {
    return cd2Element;
  }

  private String ppModifier1,
      ppType1,
      ppName1,
      ppInitial1,
      ppReturn1,
      ppThrow1,
      ppExpres1,
      ppModifier2,
      ppType2,
      ppName2,
      ppInitial2,
      ppReturn2,
      ppThrow2,
      ppExpres2,
      cd1SelfbuildString,
      cd2SelfbuildString,
      interpretation,
      parameter1,
      parameter2;

  /**
   * Constructor of the element diff type
   *
   * @param cd1Element Element from the original model
   * @param cd2Element Element from the target(new) model
   */
  public CDMemberDiff(ASTNodeType cd1Element, ASTNodeType cd2Element) {
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
    if ((cd1Element instanceof ASTCDParameter) && (cd2Element instanceof ASTCDParameter)) {
      createDiff((ASTCDParameter) cd1Element, (ASTCDParameter) cd2Element);
    }
    this.diffSize = calculateDiffSize();
  }

  private double calculateDiffSize() {
    double size = diffList.size() / 3.0;

    if (matchedParameter != null) {
      for (CDMemberDiff<ASTCDParameter> x : matchedParameter) {
        size += x.getDiffSize() / 3.0;
      }
    }
    if (addedParameter != null) {
      size += addedParameter.size() / 2.0;
    }
    if (deletedParameter != null) {
      size += deletedParameter.size() / 2.0;
    }

    for (ASTNodeDiff<? extends ASTNode, ? extends ASTNode> diff : diffList) {
      if (diff.isPresent() && diff.getCd1Value().isPresent()) {
        // Name Diffs are weighted doubled compared to every other diff
        // Parent Object in ASTNodeDiff when we check the name of it (when there is no specific
        // node for the name)
        if (diff.getCd1Value().get() instanceof ASTCDAttribute
            || diff.getCd1Value().get() instanceof ASTMCQualifiedName
            || diff.getCd1Value().get() instanceof ASTCDClass) {
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

  public void createDiff(ASTCDParameter cd1Element, ASTCDParameter cd2Element) {
    this.diffList = createDiffList(cd1Element, cd2Element);
    setParameterStrings();
  }

  private List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> createDiffList(
      ASTCDAttribute cd1Element, ASTCDAttribute cd2Element) {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();

    // Modifier, non-optional
    if (!(pp.prettyprint(cd1Element.getModifier()).length() < 1
        && pp.prettyprint(cd2Element.getModifier()).length() < 1)) {
      diffs.add(setModifier(cd1Element.getModifier(), cd2Element.getModifier()));
    }

    // MCType, non-optional
    Optional<ASTMCType> cd1Type = Optional.of(cd1Element.getMCType());
    Optional<ASTMCType> cd2Type = Optional.of(cd2Element.getMCType());
    ASTNodeDiff<ASTMCType, ASTMCType> attributeType = new ASTNodeDiff<>(cd1Type, cd2Type);
    if (attributeType.isPresent()) {
      diffs.add(attributeType);
    }
    ppType1 = getColorCode(attributeType) + pp.prettyprint(cd1Type.get()) + RESET;
    ppType2 = getColorCode(attributeType) + pp.prettyprint(cd2Type.get()) + RESET;

    // Name, non-optional
    Optional<ASTCDAttribute> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDAttribute> cd2Name = Optional.of(cd2Element);
    ASTNodeDiff<ASTCDAttribute, ASTCDAttribute> attributeName =
        new ASTNodeDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      attributeName = new ASTNodeDiff<>(CDSyntaxDiff.Op.CHANGE, cd1Name, cd2Name);
    }

    if (attributeName.isPresent()) {
      diffs.add(attributeName);
    }
    ppName1 = getColorCode(attributeName) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(attributeName) + cd2Name.get().getName() + RESET;

    // Initial expression, optional
    Optional<ASTExpression> cd1Initial =
        (cd1Element.isPresentInitial()) ? Optional.of(cd1Element.getInitial()) : Optional.empty();
    Optional<ASTExpression> cd2Initial =
        (cd2Element.isPresentInitial()) ? Optional.of(cd2Element.getInitial()) : Optional.empty();
    ASTNodeDiff<ASTExpression, ASTExpression> attributeInital =
        new ASTNodeDiff<>(cd1Initial, cd2Initial);
    if (attributeInital.isPresent()) {
      diffs.add(attributeInital);
    }
    cd1Initial.ifPresent(
        initial ->
            ppInitial1 = "= " + getColorCode(attributeInital) + pp.prettyprint(initial) + RESET);
    cd2Initial.ifPresent(
        initial ->
            ppInitial2 = "= " + getColorCode(attributeInital) + pp.prettyprint(initial) + RESET);

    return diffs;
  }

  private List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> createDiffList(
      ASTCDMethod cd1Element, ASTCDMethod cd2Element) {

    List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();

    // Modifier, non-optional
    if (!(pp5252.prettyprint(cd1Element.getModifier()).length() < 1
        && pp5252.prettyprint(cd2Element.getModifier()).length() < 1)) {
      diffs.add(setModifier(cd1Element.getModifier(), cd2Element.getModifier()));
    }

    // ASTMCReturnType, non-optional
    Optional<ASTMCReturnType> cd1ReturnType = Optional.of(cd1Element.getMCReturnType());
    Optional<ASTMCReturnType> cd2ReturnType = Optional.of(cd2Element.getMCReturnType());
    ASTNodeDiff<ASTMCReturnType, ASTMCReturnType> methodeReturnType =
        new ASTNodeDiff<>(cd1ReturnType, cd2ReturnType);
    if (methodeReturnType.isPresent()) {
      diffs.add(methodeReturnType);
    }
    ppReturn1 = getColorCode(methodeReturnType) + pp5252.prettyprint(cd1ReturnType.get()) + RESET;
    ppReturn2 = getColorCode(methodeReturnType) + pp5252.prettyprint(cd2ReturnType.get()) + RESET;

    // Name, non-optional
    Optional<ASTCDMethod> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDMethod> cd2Name = Optional.of(cd2Element);
    ASTNodeDiff<ASTCDMethod, ASTCDMethod> methodeName = new ASTNodeDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      methodeName = new ASTNodeDiff<>(CDSyntaxDiff.Op.CHANGE, cd1Name, cd2Name);
    }

    if (methodeName.isPresent()) {
      diffs.add(methodeName);
    }
    ppName1 = getColorCode(methodeName) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(methodeName) + cd2Name.get().getName() + RESET;

    // ThrowsDeclaration, optional
    Optional<ASTCDThrowsDeclaration> cd1ThrowDec =
        (cd1Element.isPresentCDThrowsDeclaration())
            ? Optional.of(cd1Element.getCDThrowsDeclaration())
            : Optional.empty();
    Optional<ASTCDThrowsDeclaration> cd2ThrowDec =
        (cd2Element.isPresentCDThrowsDeclaration())
            ? Optional.of(cd2Element.getCDThrowsDeclaration())
            : Optional.empty();
    ASTNodeDiff<ASTCDThrowsDeclaration, ASTCDThrowsDeclaration> throwDecl =
        new ASTNodeDiff<>(cd1ThrowDec, cd2ThrowDec);
    if (throwDecl.isPresent()) {
      diffs.add(throwDecl);
    }
    cd1ThrowDec.ifPresent(
        throwDec -> ppThrow1 = getColorCode(throwDecl) + pp5252.prettyprint(throwDec) + RESET);
    cd2ThrowDec.ifPresent(
        throwDec -> ppThrow2 = getColorCode(throwDecl) + pp5252.prettyprint(throwDec) + RESET);

    setParameterDiff(cd1Element.getCDParameterList(), cd2Element.getCDParameterList());
    return diffs;
  }

  private List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> createDiffList(
      ASTCDEnumConstant cd1Element, ASTCDEnumConstant cd2Element) {
    List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();

    // Name, non-optional
    Optional<ASTCDEnumConstant> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDEnumConstant> cd2Name = Optional.of(cd2Element);
    ASTNodeDiff<ASTCDEnumConstant, ASTCDEnumConstant> name =
        new ASTNodeDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      name = new ASTNodeDiff<>(CDSyntaxDiff.Op.CHANGE, cd1Name, cd2Name);
    }

    if (name.isPresent()) {
      diffs.add(name);
    }
    ppName1 = getColorCode(name) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(name) + cd2Name.get().getName() + RESET;

    return diffs;
  }

  private List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> createDiffList(
      ASTCDConstructor cd1Element, ASTCDConstructor cd2Element) {
    List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();

    // Modifier, non-optional
    if (!(pp5252.prettyprint(cd1Element.getModifier()).length() < 1
        && pp5252.prettyprint(cd2Element.getModifier()).length() < 1)) {
      diffs.add(setModifier(cd1Element.getModifier(), cd2Element.getModifier()));
    }

    // Name, non-optional
    Optional<ASTCDConstructor> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDConstructor> cd2Name = Optional.of(cd2Element);
    ASTNodeDiff<ASTCDConstructor, ASTCDConstructor> constructorName =
        new ASTNodeDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      constructorName = new ASTNodeDiff<>(CDSyntaxDiff.Op.CHANGE, cd1Name, cd2Name);
    }

    if (constructorName.isPresent()) {
      diffs.add(constructorName);
    }
    ppName1 = getColorCode(constructorName) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(constructorName) + cd2Name.get().getName() + RESET;

    // ThrowsDeclaration, optional
    Optional<ASTCDThrowsDeclaration> cd1ThrowDec =
        (cd1Element.isPresentCDThrowsDeclaration())
            ? Optional.of(cd1Element.getCDThrowsDeclaration())
            : Optional.empty();
    Optional<ASTCDThrowsDeclaration> cd2ThrowDec =
        (cd2Element.isPresentCDThrowsDeclaration())
            ? Optional.of(cd2Element.getCDThrowsDeclaration())
            : Optional.empty();
    ASTNodeDiff<ASTCDThrowsDeclaration, ASTCDThrowsDeclaration> throwDecl =
        new ASTNodeDiff<>(cd1ThrowDec, cd2ThrowDec);
    if (throwDecl.isPresent()) {
      diffs.add(throwDecl);
    }
    cd1ThrowDec.ifPresent(
        throwDec -> ppThrow1 = getColorCode(throwDecl) + pp5252.prettyprint(throwDec) + RESET);
    cd2ThrowDec.ifPresent(
        throwDec -> ppThrow2 = getColorCode(throwDecl) + pp5252.prettyprint(throwDec) + RESET);

    setParameterDiff(cd1Element.getCDParameterList(), cd2Element.getCDParameterList());
    return diffs;
  }

  /**
   * Help method for calculating the class diff because each classcontains multiple attributes which
   * need to be matched
   *
   * @param cd1Element List of attributes from the original model
   * @param cd2Element List of attributes from the target(new) model
   * @return Returns a difflist for each attribute, ordered by diffsize (small diff values ==
   *     similar)
   */

  // MCType (ellipsis:["..."])? Name ("=" defaultValue:Expression)?;
  private List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> createDiffList(
      ASTCDParameter cd1Element, ASTCDParameter cd2Element) {
    List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();

    // MCType, non-optional
    Optional<ASTMCType> cd1Type = Optional.of(cd1Element.getMCType());
    Optional<ASTMCType> cd2Type = Optional.of(cd2Element.getMCType());
    ASTNodeDiff<ASTMCType, ASTMCType> eleType = new ASTNodeDiff<>(cd1Type, cd2Type);
    if (eleType.isPresent()) {
      diffs.add(eleType);
    }
    ppType1 = getColorCode(eleType) + pp5252.prettyprint(cd1Type.get()) + RESET;
    ppType2 = getColorCode(eleType) + pp5252.prettyprint(cd2Type.get()) + RESET;

    // Name, non-optional
    Optional<ASTCDParameter> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDParameter> cd2Name = Optional.of(cd2Element);
    ASTNodeDiff<ASTCDParameter, ASTCDParameter> nameDiff =
        new ASTNodeDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      nameDiff = new ASTNodeDiff<>(CDSyntaxDiff.Op.CHANGE, cd1Name, cd2Name);
    }

    if (nameDiff.isPresent()) {
      diffs.add(nameDiff);
    }
    ppName1 = getColorCode(nameDiff) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(nameDiff) + cd2Name.get().getName() + RESET;

    // Default Value, optional
    Optional<ASTExpression> cd1Default =
        (cd1Element.isPresentDefaultValue())
            ? Optional.of(cd1Element.getDefaultValue())
            : Optional.empty();
    Optional<ASTExpression> cd2Default =
        (cd2Element.isPresentDefaultValue())
            ? Optional.of(cd2Element.getDefaultValue())
            : Optional.empty();
    ASTNodeDiff<ASTExpression, ASTExpression> parameterDefault =
        new ASTNodeDiff<>(cd1Default, cd2Default);
    if (parameterDefault.isPresent()) {
      diffs.add(parameterDefault);
    }
    cd1Default.ifPresent(
        defExpres ->
            ppExpres1 = getColorCode(parameterDefault) + pp5252.prettyprint(defExpres) + RESET);
    cd2Default.ifPresent(
        defExpres ->
            ppExpres2 = getColorCode(parameterDefault) + pp5252.prettyprint(defExpres) + RESET);

    return diffs;
  }

  private void setParameterDiff(List<ASTCDParameter> cd1list, List<ASTCDParameter> cd2list) {
    List<ASTCDParameter> cd1ReducedList;
    List<ASTCDParameter> cd2ReducedList;

    List<ASTCDParameter> deleted = new ArrayList<>();
    List<ASTCDParameter> added = new ArrayList<>();

    if (cd1list.size() == cd2list.size()) {
      cd1ReducedList = cd1list;
      cd2ReducedList = cd2list;
    } else {
      int min = Math.min(cd1list.size(), cd2list.size());
      cd1ReducedList = cd1list.stream().limit(min).collect(Collectors.toList());
      cd2ReducedList = cd2list.stream().limit(min).collect(Collectors.toList());
      for (int i = min; i < cd1list.size(); i++) {
        deleted.add(cd1list.get(i));
      }
      for (int i = min; i < cd2list.size(); i++) {
        added.add(cd2list.get(i));
      }
    }
    List<CDMemberDiff<ASTCDParameter>> paraDiffList = new ArrayList<>();
    // minimum only for safety, lists should have equal length at this point
    for (int i = 0; i < Math.min(cd1ReducedList.size(), cd2ReducedList.size()); i++) {
      CDMemberDiff<ASTCDParameter> tmp1 =
          new CDMemberDiff<>(cd1ReducedList.get(i), cd2ReducedList.get(i));
      paraDiffList.add(tmp1);
    }

    StringBuilder builder1 = new StringBuilder();
    StringBuilder builder2 = new StringBuilder();

    for (CDMemberDiff<ASTCDParameter> x : paraDiffList) {
      builder1.append(x.printCD1Element()).append(", ");
      builder2.append(x.printCD2Element()).append(", ");
    }
    for (ASTCDParameter x : deleted) {
      builder1.append(COLOR_DELETE).append(pp5252.prettyprint(x)).append(RESET).append(", ");
    }
    for (ASTCDParameter x : added) {
      builder2.append(COLOR_ADD).append(pp5252.prettyprint(x)).append(RESET).append(", ");
    }
    if (!paraDiffList.isEmpty() || !(deleted.isEmpty())) {
      this.parameter1 = builder1.substring(0, builder1.length() - 2);
    }
    if (!paraDiffList.isEmpty() || !(added.isEmpty())) {
      this.parameter2 = builder2.substring(0, builder2.length() - 2);
    }

    this.matchedParameter = paraDiffList;
    this.addedParameter = added;
    this.deletedParameter = deleted;
  }

  protected ASTNodeDiff<ASTModifier, ASTModifier> setModifier(
      ASTModifier cd1Modi, ASTModifier cd2Modi) {
    ASTNodeDiff<ASTModifier, ASTModifier> modifier =
        new ASTNodeDiff<>(Optional.of(cd1Modi), Optional.of(cd2Modi));
    // Special case, as prettyprint of empty modifiers still produce a non-empty string
    if (!(pp5252.prettyprint(cd1Modi).length() < 1)) {
      ppModifier1 = getColorCode(modifier) + pp5252.prettyprint(cd1Modi) + RESET;
    }
    if (!(pp5252.prettyprint(cd2Modi).length() < 1)) {
      ppModifier2 = getColorCode(modifier) + pp5252.prettyprint(cd2Modi) + RESET;
    }
    return modifier;
  }

  private void setParameterStrings() {

    this.cd1SelfbuildString = super.combineWithoutNulls(Arrays.asList(ppType1, ppName1, ppExpres1));

    this.cd2SelfbuildString = super.combineWithoutNulls(Arrays.asList(ppType2, ppName2, ppExpres2));

    this.interpretation = "Interpretation: ";
  }

  private void setEnumStrings() {

    this.cd1SelfbuildString = ppName1;

    this.cd2SelfbuildString = ppName2;

    this.interpretation = "Interpretation: ";
  }

  private void setAttributeStrings() {

    this.cd1SelfbuildString =
        combineWithoutNulls(Arrays.asList(ppModifier1, ppType1, ppName1, ppInitial1));

    this.cd2SelfbuildString =
        combineWithoutNulls(Arrays.asList(ppModifier2, ppType2, ppName2, ppInitial2));

    this.interpretation = "Interpretation: ";
  }

  // Methods: Modifier MCReturnType Name "(" (CDParameter || ",")* ")" CDThrowsDeclaration? ";";
  private void setMethodStrings() {
    // Signature
    this.cd1SelfbuildString =
        combineWithoutNulls(
            Arrays.asList(ppModifier1, ppReturn1, ppName1, "(", parameter1, ")", ppThrow1));
    this.cd2SelfbuildString =
        combineWithoutNulls(
            Arrays.asList(ppModifier2, ppReturn2, ppName2, "(", parameter2, ")", ppThrow2));

    this.interpretation = "Interpretation: ";
  }

  private void setConstructorStrings() {
    this.cd1SelfbuildString =
        combineWithoutNulls(Arrays.asList(ppModifier1, ppName1, "(", parameter1, ")", ppThrow1));
    this.cd2SelfbuildString =
        combineWithoutNulls(Arrays.asList(ppModifier2, ppName2, "(", parameter2, ")", ppThrow2));

    this.interpretation = "Interpretation: ";
  }

  @Override
  public String combineWithoutNulls(List<String> stringList) {
    return combineWithoutNulls(stringList) + ";";
  }

  public String printCD1Element() {
    return cd1SelfbuildString;
  }

  public String printCD2Element() {
    return cd2SelfbuildString;
  }
}
