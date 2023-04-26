/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syntaxdiff;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.*;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.umlmodifier._ast.ASTModifier;
import java.util.*;
import java.util.stream.Collectors;

public class CDTypeDiff<ASTCDType1 extends ASTCDType, ASTCDType2 extends ASTCDType>
    extends CDElementDiff {
  CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

  protected final ASTCDType1 cd1Element;

  protected final ASTCDType2 cd2Element;

  private String ppModifier1,
      ppModifier1NC,
      ppName1,
      ppName1NC,
      ppExtended1,
      ppExtended1NC,
      ppInter1,
      ppInter1NC,
      ppModifier2,
      ppModifier2NC,
      ppName2,
      ppName2NC,
      ppExtended2,
      ppExtended2NC,
      ppInter2,
      ppInter2NC,
      cd1Print,
      cd2Print,
      cd1PrintNC,
      cd2PrintNC,
      keywordCD1,
      keywordCD2,
      keywordCD1NC,
      keywordCD2NC;

  protected List<CDMemberDiff<ASTCDAttribute>> matchedAttributesList;

  protected List<ASTCDAttribute> deletedAttributes;

  protected List<ASTCDAttribute> addedAttributes;

  protected List<CDMemberDiff<ASTCDMethod>> matchedMethodeList;

  protected List<ASTCDMethod> deletedMethods;

  protected List<ASTCDMethod> addedMethode;

  protected List<CDMemberDiff<ASTCDConstructor>> matchedConstructorList;

  protected List<ASTCDConstructor> deletedConstructor;

  protected List<ASTCDConstructor> addedConstructor;

  protected List<CDMemberDiff<ASTCDEnumConstant>> matchedEnumConstantList;

  protected List<ASTCDEnumConstant> deletedEnumConstants;

  protected List<ASTCDEnumConstant> addedEnumConstants;

  ICD4CodeArtifactScope scopecd1;

  ICD4CodeArtifactScope scopecd2;

  public List<CDMemberDiff<ASTCDEnumConstant>> getMatchedEnumConstantList() {
    return matchedEnumConstantList;
  }

  public List<ASTCDEnumConstant> getAddedEnumConstants() {
    return addedEnumConstants;
  }

  public List<ASTCDEnumConstant> getDeletedEnumConstants() {
    return deletedEnumConstants;
  }

  public List<CDMemberDiff<ASTCDAttribute>> getMatchedAttributesList() {
    return matchedAttributesList;
  }

  public List<ASTCDAttribute> getAddedAttributes() {
    return addedAttributes;
  }

  public List<ASTCDAttribute> getDeletedAttributes() {
    return deletedAttributes;
  }

  public List<CDMemberDiff<ASTCDMethod>> getMatchedMethodeList() {
    return matchedMethodeList;
  }

  public List<ASTCDMethod> getAddedMethode() {
    return addedMethode;
  }

  public List<ASTCDMethod> getDeletedMethods() {
    return deletedMethods;
  }

  public List<CDMemberDiff<ASTCDConstructor>> getMatchedConstructorList() {
    return matchedConstructorList;
  }

  public List<ASTCDConstructor> getAddedConstructor() {
    return addedConstructor;
  }

  public List<ASTCDConstructor> getDeletedConstructor() {
    return deletedConstructor;
  }

  public ASTCDType1 getCd1Element() {
    return cd1Element;
  }

  public ASTCDType2 getCd2Element() {
    return cd2Element;
  }

  public CDTypeDiff(
      ASTCDType1 cd1Element,
      ASTCDType2 cd2Element,
      ICD4CodeArtifactScope scopecd1,
      ICD4CodeArtifactScope scopecd2) {
    this.cd1Element = cd1Element;
    this.cd2Element = cd2Element;
    this.scopecd1 = scopecd1;
    this.scopecd2 = scopecd2;

    createDefaultDiffList(cd1Element, cd2Element);

    if ((cd1Element instanceof ASTCDClass) && (cd2Element instanceof ASTCDClass)) {
      keywordCD1 = keywordCD1NC = "class";
      keywordCD2 = keywordCD2NC = "class";
      createClassDiff((ASTCDClass) cd1Element, (ASTCDClass) cd2Element);
    } else if (cd1Element instanceof ASTCDInterface && cd2Element instanceof ASTCDInterface) {
      keywordCD1 = keywordCD1NC = "interface";
      keywordCD2 = keywordCD2NC = "interface";
      createInterfaceDiff((ASTCDInterface) cd1Element, (ASTCDInterface) cd2Element);

    } else if (cd1Element instanceof ASTCDEnum && cd2Element instanceof ASTCDEnum) {
      keywordCD1 = keywordCD1NC = "enum";
      keywordCD2 = keywordCD2NC = "enum";
      createEnumDiff((ASTCDEnum) cd1Element, (ASTCDEnum) cd2Element);

    } else if (cd1Element instanceof ASTCDInterface && cd2Element instanceof ASTCDClass) {
      keywordCD1 = COLOR_CHANGE + "interface" + RESET;
      keywordCD1NC = "interface";
      keywordCD2 = COLOR_CHANGE + "class" + RESET;
      keywordCD2NC = "class";
      createInterfaceClassDiff((ASTCDInterface) cd1Element, (ASTCDClass) cd2Element);
      this.interpretation
          .append("Interface")
          .append(": ")
          .append(CDSyntaxDiff.Interpretation.REPURPOSED)
          .append(" ");
      this.interpretationList.add(CDSyntaxDiff.Interpretation.REPURPOSED);

    } else if (cd1Element instanceof ASTCDClass && cd2Element instanceof ASTCDInterface) {
      keywordCD1 = COLOR_CHANGE + "class" + RESET;
      keywordCD1NC = "class";
      keywordCD2 = COLOR_CHANGE + "interface" + RESET;
      keywordCD2NC = "interface";
      createClassInterfaceDiff((ASTCDClass) cd1Element, (ASTCDInterface) cd2Element);
      this.interpretation
          .append("Class")
          .append(": ")
          .append(CDSyntaxDiff.Interpretation.REPURPOSED)
          .append(" ");
      this.interpretationList.add(CDSyntaxDiff.Interpretation.REPURPOSED);
    }

    // Set the diff lists for the current diff
    setCDMemberDiffLists(cd1Element, cd2Element, scopecd1, scopecd2);

    this.diffSize = calculateDiffSize();
    setStrings();
  }

  private void setCDMemberDiffLists(
      ASTCDType1 cd1Element,
      ASTCDType2 cd2Element,
      ICD4CodeArtifactScope scopecd1,
      ICD4CodeArtifactScope scopecd2) {
    Set<ASTCDType> superClassesCD1 =
        CDInheritanceHelper.getDirectSuperClasses(cd1Element, scopecd1);
    Set<ASTCDType> superClassesCD2 =
        CDInheritanceHelper.getDirectSuperClasses(cd2Element, scopecd2);

    List<ASTCDAttribute> attributeListCd1 =
        addInheritedAttributes(superClassesCD1, cd1Element.getCDAttributeList());
    List<ASTCDAttribute> attributeListCd2 =
        addInheritedAttributes(superClassesCD2, cd2Element.getCDAttributeList());

    List<ASTCDMethod> methodeListCd1 =
        addInheritedMethods(superClassesCD1, cd1Element.getCDMethodList());
    List<ASTCDMethod> methodeListCd2 =
        addInheritedMethods(superClassesCD2, cd2Element.getCDMethodList());

    // Create trivial matches for attributes/constructors/methods
    this.matchedAttributesList =
        getMatchingList(getElementDiffList(attributeListCd1, attributeListCd2));
    this.deletedAttributes = absentElementList(matchedAttributesList, attributeListCd1);
    this.addedAttributes = absentElementList(matchedAttributesList, attributeListCd2);

    this.matchedMethodeList = getMatchingList(getElementDiffList(methodeListCd1, methodeListCd2));
    this.deletedMethods = absentElementList(matchedMethodeList, methodeListCd1);
    this.addedMethode = absentElementList(matchedMethodeList, methodeListCd2);

    this.matchedConstructorList =
        getMatchingList(
            getElementDiffList(
                cd1Element.getCDConstructorList(), cd2Element.getCDConstructorList()));
    this.deletedConstructor =
        absentElementList(matchedConstructorList, cd1Element.getCDConstructorList());
    this.addedConstructor =
        absentElementList(matchedConstructorList, cd2Element.getCDConstructorList());
  }

  /**
   * Calculation of the diff size between the given classes, automaticly calculated on object
   * creation Name changes are weighted more and each member(attribute/methods/...) add at most one
   * to the size
   *
   * @return Diff size as double
   */
  private double calculateDiffSize() {
    // Diff size of signature
    double size = diffList.size();

    // Diff size of attributes (amounts to max ~ 1)
    for (CDMemberDiff<ASTCDAttribute> i : matchedAttributesList) {
      int count = 2;
      if (i.getCd1Element().isPresentInitial() || i.getCd2Element().isPresentInitial()) {
        for (ASTNodeDiff<? extends ASTNode, ? extends ASTNode> x : i.getDiffList()) {
          if ((x.getCd1Value().isPresent() && x.getCd1Value().get() instanceof ASTExpression)
              || (x.getCd2Value().isPresent() && x.getCd2Value().get() instanceof ASTExpression)) {
            count += 1;
          }
        }
      }
      size += i.getDiffSize() / count;
    }
    size += (deletedAttributes.size() + addedAttributes.size()) / 2.0;

    // Todo: Add Parameter Diff to size calculation
    // Diff size of methods
    for (CDMemberDiff<ASTCDMethod> i : matchedMethodeList) {
      if (i.getCd1Element().isPresentCDThrowsDeclaration()) {
        size += i.getDiffSize() / 4.0;
      } else {
        size += i.getDiffSize() / 3.0;
      }
    }
    size += deletedMethods.size() + addedMethode.size();

    // Diff size of constructors
    for (CDMemberDiff<ASTCDConstructor> i : matchedConstructorList) {
      if (i.getCd1Element().isPresentCDThrowsDeclaration()) {
        size += i.getDiffSize() / 4.0;
      } else {
        size += i.getDiffSize() / 3.0;
      }
    }
    size += deletedConstructor.size() + addedConstructor.size();
    size += addWeightToDiffSize(diffList);

    return size;
  }

  public void createClassDiff(ASTCDClass cd1Element, ASTCDClass cd2Element) {
    createDiffList(cd1Element, cd2Element);
  }

  public void createInterfaceDiff(ASTCDInterface cd1Element, ASTCDInterface cd2Element) {
    createDiffList(cd1Element, cd2Element);
  }

  public void createEnumDiff(ASTCDEnum cd1Element, ASTCDEnum cd2Element) {
    createDiffList(cd1Element, cd2Element);
  }

  public void createInterfaceClassDiff(ASTCDInterface cd1Element, ASTCDClass cd2Element) {
    createDiffList(cd1Element, cd2Element);
  }

  public void createClassInterfaceDiff(ASTCDClass cd1Element, ASTCDInterface cd2Element) {
    createDiffList(cd1Element, cd2Element);
  }

  private void createDefaultDiffList(ASTCDType1 cd1Element, ASTCDType2 cd2Element) {
    List<ASTNodeDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();
    interpretation.append("Interpretation: ");

    // Modifier, non-optional
    if (!(pp.prettyprint(cd1Element.getModifier()).length() < 1
        && pp.prettyprint(cd2Element.getModifier()).length() < 1)) {
      diffs.add(setModifier(cd1Element.getModifier(), cd2Element.getModifier()));
    }

    // Name, non-optional
    Optional<ASTCDType1> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDType2> cd2Name = Optional.of(cd2Element);
    ASTNodeDiff<ASTCDType1, ASTCDType2> className = new ASTNodeDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      className = new ASTNodeDiff<>(CDSyntaxDiff.Op.CHANGE, cd1Name, cd2Name);
    }

    ppName1 = getColorCode(className) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(className) + cd2Name.get().getName() + RESET;

    ppName1NC = cd1Name.get().getName();
    ppName2NC = cd2Name.get().getName();

    if (className.isPresent()) {
      diffs.add(className);
      if (className.getInterpretation().isPresent()) {
        interpretation
            .append("Name")
            .append(": ")
            .append(className.getInterpretation().get())
            .append(" ");
      }
    }
    if (cd1Name.get().getName().equals(cd2Name.get().getName())
        && !cd1Name
            .get()
            .getSymbol()
            .getInternalQualifiedName()
            .equals(cd2Name.get().getSymbol().getInternalQualifiedName())) {
      interpretation
          .append("Package")
          .append(": ")
          .append(CDSyntaxDiff.Interpretation.RELOCATION)
          .append(" ");
    }

    this.diffList = diffs;
  }

  private void createDiffList(ASTCDClass cd1Element, ASTCDInterface cd2Element) {
    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend =
        (cd1Element.isPresentCDExtendUsage())
            ? Optional.of(cd1Element.getCDExtendUsage())
            : Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend =
        (cd2Element.isPresentCDExtendUsage())
            ? Optional.of(cd2Element.getCDExtendUsage())
            : Optional.empty();
    ASTNodeDiff<ASTCDExtendUsage, ASTCDExtendUsage> extendedDiff =
        new ASTNodeDiff<>(cd1Extend, cd2Extend);

    cd1Extend.ifPresent(
        initial -> ppExtended1 = getColorCode(extendedDiff) + pp.prettyprint(initial) + RESET);
    cd2Extend.ifPresent(
        initial -> ppExtended2 = getColorCode(extendedDiff) + pp.prettyprint(initial) + RESET);
    cd1Extend.ifPresent(initial -> ppExtended1NC = pp.prettyprint(initial));
    cd2Extend.ifPresent(initial -> ppExtended2NC = pp.prettyprint(initial));

    if (extendedDiff.isPresent()) {
      diffList.add(extendedDiff);
      if (extendedDiff.getInterpretation().isPresent()) {
        interpretation
            .append("Extended")
            .append(": ")
            .append(extendedDiff.getInterpretation().get())
            .append(" ");
      }
    }
  }

  private void createDiffList(ASTCDInterface cd1Element, ASTCDInterface cd2Element) {
    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend =
        (cd1Element.isPresentCDExtendUsage())
            ? Optional.of(cd1Element.getCDExtendUsage())
            : Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend =
        (cd2Element.isPresentCDExtendUsage())
            ? Optional.of(cd2Element.getCDExtendUsage())
            : Optional.empty();
    ASTNodeDiff<ASTCDExtendUsage, ASTCDExtendUsage> extendedDiff =
        new ASTNodeDiff<>(cd1Extend, cd2Extend);

    cd1Extend.ifPresent(
        initial -> ppExtended1 = getColorCode(extendedDiff) + pp.prettyprint(initial) + RESET);
    cd2Extend.ifPresent(
        initial -> ppExtended2 = getColorCode(extendedDiff) + pp.prettyprint(initial) + RESET);
    cd1Extend.ifPresent(initial -> ppExtended1NC = pp.prettyprint(initial));
    cd2Extend.ifPresent(initial -> ppExtended2NC = pp.prettyprint(initial));

    if (extendedDiff.isPresent()) {
      diffList.add(extendedDiff);
      if (extendedDiff.getInterpretation().isPresent()) {
        interpretation
            .append("Extended")
            .append(": ")
            .append(extendedDiff.getInterpretation().get())
            .append(" ");
      }
    }
  }

  private void createDiffList(ASTCDEnum cd1Element, ASTCDEnum cd2Element) {
    // Implements, optional
    Optional<ASTMCObjectType> cd1Imple =
        (cd1Element.isPresentCDInterfaceUsage())
            ? Optional.of(cd1Element.getInterfaceList().get(0))
            : Optional.empty();
    Optional<ASTMCObjectType> cd2Imple =
        (cd2Element.isPresentCDInterfaceUsage())
            ? Optional.of(cd2Element.getInterfaceList().get(0))
            : Optional.empty();
    ASTNodeDiff<ASTMCObjectType, ASTMCObjectType> interfaceDiff =
        new ASTNodeDiff<>(cd1Imple, cd2Imple);

    cd1Imple.ifPresent(
        inter ->
            ppInter1 =
                getColorCode(interfaceDiff) + " implements " + pp.prettyprint(inter) + RESET);
    cd2Imple.ifPresent(
        inter ->
            ppInter2 =
                getColorCode(interfaceDiff) + " implements " + pp.prettyprint(inter) + RESET);
    cd1Imple.ifPresent(inter -> ppInter1NC = " implements " + pp.prettyprint(inter));
    cd2Imple.ifPresent(inter -> ppInter2NC = " implements " + pp.prettyprint(inter));

    if (interfaceDiff.isPresent()) {
      diffList.add(interfaceDiff);
      if (interfaceDiff.getInterpretation().isPresent()) {
        interpretation
            .append("Interface")
            .append(": ")
            .append(interfaceDiff.getInterpretation().get())
            .append(" ");
      }
    }
  }

  private void createDiffList(ASTCDClass cd1Element, ASTCDClass cd2Element) {
    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend =
        (cd1Element.isPresentCDExtendUsage())
            ? Optional.of(cd1Element.getCDExtendUsage())
            : Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend =
        (cd2Element.isPresentCDExtendUsage())
            ? Optional.of(cd2Element.getCDExtendUsage())
            : Optional.empty();
    ASTNodeDiff<ASTCDExtendUsage, ASTCDExtendUsage> extendedDiff =
        new ASTNodeDiff<>(cd1Extend, cd2Extend);

    cd1Extend.ifPresent(
        initial -> ppExtended1 = getColorCode(extendedDiff) + pp.prettyprint(initial) + RESET);
    cd2Extend.ifPresent(
        initial -> ppExtended2 = getColorCode(extendedDiff) + pp.prettyprint(initial) + RESET);
    cd1Extend.ifPresent(initial -> ppExtended1NC = pp.prettyprint(initial));
    cd2Extend.ifPresent(initial -> ppExtended2NC = pp.prettyprint(initial));

    if (extendedDiff.isPresent()) {
      diffList.add(extendedDiff);
      if (extendedDiff.getInterpretation().isPresent()) {
        interpretation
            .append("Extended")
            .append(": ")
            .append(extendedDiff.getInterpretation().get())
            .append(" ");
      }
    }

    // Implements, optional
    Optional<ASTMCObjectType> cd1Imple =
        (cd1Element.isPresentCDInterfaceUsage())
            ? Optional.of(cd1Element.getInterfaceList().get(0))
            : Optional.empty();
    Optional<ASTMCObjectType> cd2Imple =
        (cd2Element.isPresentCDInterfaceUsage())
            ? Optional.of(cd2Element.getInterfaceList().get(0))
            : Optional.empty();
    ASTNodeDiff<ASTMCObjectType, ASTMCObjectType> interfaceDiff =
        new ASTNodeDiff<>(cd1Imple, cd2Imple);

    cd1Imple.ifPresent(
        inter ->
            ppInter1 =
                getColorCode(interfaceDiff) + " implements " + pp.prettyprint(inter) + RESET);
    cd2Imple.ifPresent(
        inter ->
            ppInter2 =
                getColorCode(interfaceDiff) + " implements " + pp.prettyprint(inter) + RESET);
    cd1Imple.ifPresent(inter -> ppInter1NC = " implements " + pp.prettyprint(inter));
    cd2Imple.ifPresent(inter -> ppInter2NC = " implements " + pp.prettyprint(inter));

    if (interfaceDiff.isPresent()) {
      diffList.add(interfaceDiff);
      if (interfaceDiff.getInterpretation().isPresent()) {
        interpretation
            .append("Interface")
            .append(": ")
            .append(interfaceDiff.getInterpretation().get())
            .append(" ");
      }
    }
  }

  private void createDiffList(ASTCDInterface cd1Element, ASTCDClass cd2Element) {
    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend =
        (cd1Element.isPresentCDExtendUsage())
            ? Optional.of(cd1Element.getCDExtendUsage())
            : Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend =
        (cd2Element.isPresentCDExtendUsage())
            ? Optional.of(cd2Element.getCDExtendUsage())
            : Optional.empty();
    ASTNodeDiff<ASTCDExtendUsage, ASTCDExtendUsage> extendedDiff =
        new ASTNodeDiff<>(cd1Extend, cd2Extend);

    cd1Extend.ifPresent(
        initial -> ppExtended1 = getColorCode(extendedDiff) + pp.prettyprint(initial) + RESET);
    cd2Extend.ifPresent(
        initial -> ppExtended2 = getColorCode(extendedDiff) + pp.prettyprint(initial) + RESET);
    cd1Extend.ifPresent(initial -> ppExtended1NC = pp.prettyprint(initial));
    cd2Extend.ifPresent(initial -> ppExtended2NC = pp.prettyprint(initial));

    if (extendedDiff.isPresent()) {
      diffList.add(extendedDiff);
      if (extendedDiff.getInterpretation().isPresent()) {
        interpretation
            .append("Extended")
            .append(": ")
            .append(extendedDiff.getInterpretation().get())
            .append(" ");
      }
    }
  }

  /**
   * Help function to reduce code reusing. Create a modifier diff and sets print and interpretation
   * strings.
   *
   * @param cd1Modi Modifier of the first model
   * @param cd2Modi Modifier of the second model
   * @return ASTNodeDiff of type ASTModifier
   */
  protected ASTNodeDiff<ASTModifier, ASTModifier> setModifier(
      ASTModifier cd1Modi, ASTModifier cd2Modi) {
    ASTNodeDiff<ASTModifier, ASTModifier> modifier =
        new ASTNodeDiff<>(Optional.of(cd1Modi), Optional.of(cd2Modi));

    if (!(pp.prettyprint(cd1Modi).length() < 1)) {
      ppModifier1 = getColorCode(modifier) + pp.prettyprint(cd1Modi) + RESET;
      ppModifier1NC = pp.prettyprint(cd1Modi);
    }
    if (!(pp.prettyprint(cd2Modi).length() < 1)) {
      ppModifier2 = getColorCode(modifier) + pp.prettyprint(cd2Modi) + RESET;
      ppModifier2NC = pp.prettyprint(cd2Modi);
    }

    if (modifier.isPresent() && modifier.getOperation().isPresent()) {
      if (modifier.getInterpretation().isPresent()) {
        interpretation
            .append("Modifier")
            .append(": ")
            .append(modifier.getInterpretation().get())
            .append(" ");
      }
    }
    return modifier;
  }

  /**
   * Adds all inherited attributes, which have no local copy (overwrite).
   *
   * @param superClasses List of superclasses/interfaces for the current set of attributes
   * @param attributeList List of attributes from the element, which should be expanded
   * @return List of original attributes + inherited without conflicts
   */
  private List<ASTCDAttribute> addInheritedAttributes(
      Set<ASTCDType> superClasses, List<ASTCDAttribute> attributeList) {
    for (ASTCDType x : superClasses) {
      List<ASTCDAttribute> inheritedAttributes = x.getCDAttributeList();

      for (ASTCDAttribute inheritedAttribute : inheritedAttributes) {
        String name = inheritedAttribute.getName();
        boolean found = false;
        for (ASTCDAttribute localAttribute : attributeList) {
          if (localAttribute.getName().equals(name)) {
            found = true;
            break;
          }
        }
        if (!found) {
          attributeList.add(inheritedAttribute);
          if (!interpretationList.contains(CDSyntaxDiff.Interpretation.ATTRIBUTE_INHERITED)) {
            interpretationList.add(CDSyntaxDiff.Interpretation.ATTRIBUTE_INHERITED);
            interpretation.append(CDSyntaxDiff.Interpretation.ATTRIBUTE_INHERITED).append(" ");
          }
        }
      }
    }
    return attributeList;
  }

  /**
   * Adds all inherited methods, which have no local signature overwrites
   *
   * @param superClasses List of superclasses/interfaces for the current set of methods
   * @param methodeList List of methods from the element, which should be expanded
   * @return List of original attributes + inherited without conflicts
   */
  private List<ASTCDMethod> addInheritedMethods(
      Set<ASTCDType> superClasses, List<ASTCDMethod> methodeList) {
    List<ASTCDMethod> output = new ArrayList<>(methodeList);
    for (ASTCDType x : superClasses) {
      List<ASTCDMethod> inheritedMethods = x.getCDMethodList();

      for (ASTCDMethod inheritedMethod : inheritedMethods) {
        String name = inheritedMethod.getName();
        boolean found = false;
        for (ASTCDMethod localMethod : output) {
          if (localMethod.getName().equals(name)
              && inheritedMethod.getCDParameterList().size()
                  == localMethod.getCDParameterList().size()) {
            int counter = 0;
            for (int i = 0; i < inheritedMethod.getCDParameterList().size(); i++) {
              if (inheritedMethod
                  .getCDParameterList()
                  .get(i)
                  .getMCType()
                  .deepEquals(localMethod.getCDParameterList().get(i).getMCType())) {
                counter += 1;
              }
            }
            if (counter == inheritedMethod.getCDParameterList().size()) {
              found = true;
            }
          }
        }
        if (!found) {
          output.add(inheritedMethod);
          if (!interpretationList.contains(CDSyntaxDiff.Interpretation.METHOD_INHERITED)) {
            interpretationList.add(CDSyntaxDiff.Interpretation.METHOD_INHERITED);
            interpretation.append(CDSyntaxDiff.Interpretation.METHOD_INHERITED).append(" ");
          }
        }
      }
    }
    return output;
  }

  /**
   * Methode for calculating a list of elements which are not included in any match provided by the
   * matchs list.
   *
   * @param matchs List of matches between elements with the CDMemberDiff type
   * @param elementList List of elements to be reduced e.g. List of Attributes from a CD Class
   * @param <T> Type of the element, e.g. Classes
   * @return Reduced list of type provided as elementList
   */
  protected static <T extends ASTNode> List<T> absentElementList(
      List<CDMemberDiff<T>> matchs, List<T> elementList) {
    List<T> output = new ArrayList<>();
    for (T element : elementList) {
      boolean found = false;
      for (CDMemberDiff<T> diff : matchs) {
        if (diff.getCd1Element().equals(element) || diff.getCd2Element().equals(element)) {
          found = true;
          break;
        }
      }
      if (!found) {
        output.add(element);
      }
    }
    return output;
  }

  /**
   * Methode to reduce a given list of potential matches between elements to at most one match for
   * each entry
   *
   * @param elementsDiffList List of diffs between one element of the first model and every element
   *     of the same type from the second model
   * @param <T> Type of the element, e.g. Classes
   * @return Reduced list of matches for elements between two models
   */
  protected static <T extends ASTNode> List<CDMemberDiff<T>> getMatchingList(
      List<List<CDMemberDiff<T>>> elementsDiffList) {
    List<T> cd1matchedElements = new ArrayList<>();
    List<T> cd2matchedElements = new ArrayList<>();
    List<CDMemberDiff<T>> matchedElements = new ArrayList<>();

    for (List<CDMemberDiff<T>> currentElementList : elementsDiffList) {
      double threshold = 0;
      OptionalDouble optAverage =
          currentElementList.stream().mapToDouble(CDMemberDiff::getDiffSize).average();
      if (optAverage.isPresent()) {
        threshold = (1 / (double) (currentElementList.size() + 1)) + optAverage.getAsDouble() / 2;
      }
      if (!currentElementList.isEmpty()) {
        for (CDMemberDiff<T> currentCDMemberDiff : currentElementList) {
          T currentcd1Element = currentCDMemberDiff.getCd1Element();
          T currentcd2Element = currentCDMemberDiff.getCd2Element();
          if (!cd1matchedElements.contains(currentcd1Element)
              && !cd2matchedElements.contains(currentcd2Element)) {
            boolean found = false;
            for (List<CDMemberDiff<T>> nextCDMemberDiffList : elementsDiffList) {
              if (!nextCDMemberDiffList.equals(currentElementList)) {
                if (!nextCDMemberDiffList.isEmpty()) {
                  for (CDMemberDiff<T> nextCDMemberDiff : nextCDMemberDiffList) {
                    if (nextCDMemberDiff.getCd2Element().deepEquals(currentcd2Element)
                        && nextCDMemberDiff.getDiffSize() < currentCDMemberDiff.getDiffSize()) {
                      found = true;
                    }
                  }
                }
              }
            }
            if (!found && currentCDMemberDiff.getDiffSize() <= threshold) {
              matchedElements.add(currentCDMemberDiff);
              cd1matchedElements.add(currentcd1Element);
              cd2matchedElements.add(currentcd2Element);
              break;
            }
          }
        }
      }
    }
    return matchedElements;
  }

  /**
   * Help method for calculating the class diff because each class can contains multiple methods
   * which need to be matched
   *
   * @param cd1ElementList List of methods from the original model
   * @param cd2ElementList List of methods from the target(new) model
   * @return Returns a difflist for each method, ordered by diffsize (small diff values == similar)
   */
  protected static <T extends ASTNode> List<List<CDMemberDiff<T>>> getElementDiffList(
      List<T> cd1ElementList, List<T> cd2ElementList) {
    List<List<CDMemberDiff<T>>> diffs = new ArrayList<>();
    for (T cd1Element : cd1ElementList) {
      List<CDMemberDiff<T>> cd1ElementMatches = new ArrayList<>();
      for (T cd2Element : cd2ElementList) {
        cd1ElementMatches.add(new CDMemberDiff<>(cd1Element, cd2Element));
      }
      // Sort by size of diffs, ascending
      cd1ElementMatches.sort(Comparator.comparing(CDMemberDiff::getDiffSize));
      diffs.add(cd1ElementMatches);
    }
    return diffs;
  }

  /** Create Strings ready to be printed for both elements in the current diff */
  private void setStrings() {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    StringBuilder outputCD1 = new StringBuilder();
    StringBuilder outputCD2 = new StringBuilder();
    StringBuilder outputCD1NC = new StringBuilder();
    StringBuilder outputCD2NC = new StringBuilder();

    StringBuilder bodyCD1 = new StringBuilder();
    StringBuilder bodyCD2 = new StringBuilder();
    StringBuilder bodyCD1NC = new StringBuilder();
    StringBuilder bodyCD2NC = new StringBuilder();

    String signatureCD1 =
        combineWithoutNulls(Arrays.asList(ppModifier1, keywordCD1, ppName1, ppExtended1, ppInter1));
    String signatureCD2 =
        combineWithoutNulls(Arrays.asList(ppModifier2, keywordCD2, ppName2, ppExtended2, ppInter2));
    String signatureCD1NC =
        combineWithoutNulls(
            Arrays.asList(ppModifier1NC, keywordCD1NC, ppName1NC, ppExtended1NC, ppInter1NC));
    String signatureCD2NC =
        combineWithoutNulls(
            Arrays.asList(ppModifier2NC, keywordCD2NC, ppName2NC, ppExtended2NC, ppInter2NC));

    String bodyOffset = "     ";
    String bodyOffsetDel = "-    ";
    String bodyOffsetAdd = "+    ";
    String bodyOffsetChange = "~    ";

    Map<String, Integer> add = new HashMap<>();
    Map<String, Integer> matchDel = new HashMap<>();
    Map<String, Integer> addNC = new HashMap<>();
    Map<String, Integer> matchDelNC = new HashMap<>();

    for (CDMemberDiff<ASTCDAttribute> x : matchedAttributesList) {
      matchDel.put(
          x.printCD1Element(),
          Integer.valueOf(
              x.getCd1Element().get_SourcePositionStart().getLine()
                  + ""
                  + x.getCd1Element().get_SourcePositionStart().getColumn()));
      String tmp = bodyOffset + pp.prettyprint(x.getCd1Element());
      if (x.getDiffList().size() > 0) {
        tmp = bodyOffsetChange + pp.prettyprint(x.getCd1Element());
      }
      if (tmp.contains("\n")) {
        tmp = tmp.split("\n")[0];
      }
      matchDelNC.put(
          tmp,
          Integer.valueOf(
              x.getCd1Element().get_SourcePositionStart().getLine()
                  + ""
                  + x.getCd1Element().get_SourcePositionStart().getColumn()));
    }

    for (CDMemberDiff<ASTCDMethod> x : matchedMethodeList) {
      matchDel.put(
          x.printCD1Element(),
          Integer.valueOf(
              x.getCd1Element().get_SourcePositionStart().getLine()
                  + ""
                  + x.getCd1Element().get_SourcePositionStart().getColumn()));
      String tmp = bodyOffset + pp.prettyprint((ASTCDBasisNode) x.getCd1Element());
      if (x.getDiffList().size() > 0) {
        tmp = bodyOffsetChange + pp.prettyprint((ASTCDBasisNode) x.getCd1Element());
      }
      if (tmp.contains("\n")) {
        tmp = tmp.split("\n")[0];
      }
      matchDelNC.put(
          tmp,
          Integer.valueOf(
              x.getCd1Element().get_SourcePositionStart().getLine()
                  + ""
                  + x.getCd1Element().get_SourcePositionStart().getColumn()));
    }

    if (matchedEnumConstantList != null) {
      for (CDMemberDiff<ASTCDEnumConstant> x : matchedEnumConstantList) {
        matchDel.put(
            x.printCD1Element(),
            Integer.valueOf(
                x.getCd1Element().get_SourcePositionStart().getLine()
                    + ""
                    + x.getCd1Element().get_SourcePositionStart().getColumn()));
        String tmp = bodyOffset + pp.prettyprint(x.getCd1Element());
        if (x.getDiffList().size() > 0) {
          tmp = bodyOffsetChange + pp.prettyprint(x.getCd1Element());
        }
        if (tmp.contains("\n")) {
          tmp = tmp.split("\n")[0];
        }
        matchDelNC.put(
            tmp,
            Integer.valueOf(
                x.getCd1Element().get_SourcePositionStart().getLine()
                    + ""
                    + x.getCd1Element().get_SourcePositionStart().getColumn()));
      }
      for (CDMemberDiff<ASTCDEnumConstant> x : matchedEnumConstantList) {
        add.put(
            x.printCD2Element(),
            Integer.valueOf(
                x.getCd2Element().get_SourcePositionStart().getLine()
                    + ""
                    + x.getCd2Element().get_SourcePositionStart().getColumn()));
        String tmp = bodyOffset + pp.prettyprint(x.getCd2Element());
        if (x.getDiffList().size() > 0) {
          tmp = bodyOffsetChange + pp.prettyprint(x.getCd2Element());
        }
        if (tmp.contains("\n")) {
          tmp = tmp.split("\n")[0];
        }
        addNC.put(
            tmp,
            Integer.valueOf(
                x.getCd2Element().get_SourcePositionStart().getLine()
                    + ""
                    + x.getCd2Element().get_SourcePositionStart().getColumn()));
      }
      for (ASTCDEnumConstant x : getDeletedEnumConstants()) {
        StringBuilder delEnumConstant = new StringBuilder();
        String deletedEnumConstant = pp.prettyprint(x);
        if (deletedEnumConstant.contains("\n")) {
          deletedEnumConstant = deletedEnumConstant.split("\n")[0];
        }
        matchDelNC.put(
            bodyOffsetDel + deletedEnumConstant,
            Integer.valueOf(
                x.get_SourcePositionStart().getLine()
                    + ""
                    + x.get_SourcePositionStart().getColumn()));

        delEnumConstant.append(COLOR_DELETE).append(deletedEnumConstant).append(RESET);
        matchDel.put(
            delEnumConstant.toString(),
            Integer.valueOf(
                x.get_SourcePositionStart().getLine()
                    + ""
                    + x.get_SourcePositionStart().getColumn()));
      }
      for (ASTCDEnumConstant x : addedEnumConstants) {
        StringBuilder addEnumConst = new StringBuilder();
        String addedEnumConstant = pp.prettyprint(x);
        if (addedEnumConstant.contains("\n")) {
          addedEnumConstant = addedEnumConstant.split("\n")[0];
        }
        addNC.put(
            bodyOffsetAdd + addedEnumConstant,
            Integer.valueOf(
                x.get_SourcePositionStart().getLine()
                    + ""
                    + x.get_SourcePositionStart().getColumn()));

        addEnumConst.append(COLOR_ADD).append(addedEnumConstant).append(RESET);
        add.put(
            addEnumConst.toString(),
            Integer.valueOf(
                x.get_SourcePositionStart().getLine()
                    + ""
                    + x.get_SourcePositionStart().getColumn()));
      }
    }

    for (CDMemberDiff<ASTCDAttribute> x : matchedAttributesList) {
      add.put(
          x.printCD2Element(),
          Integer.valueOf(
              x.getCd2Element().get_SourcePositionStart().getLine()
                  + ""
                  + x.getCd2Element().get_SourcePositionStart().getColumn()));
      String tmp = bodyOffset + pp.prettyprint(x.getCd2Element());
      if (x.getDiffList().size() > 0) {
        tmp = bodyOffsetChange + pp.prettyprint(x.getCd2Element());
      }
      if (tmp.contains("\n")) {
        tmp = tmp.split("\n")[0];
      }
      addNC.put(
          tmp,
          Integer.valueOf(
              x.getCd2Element().get_SourcePositionStart().getLine()
                  + ""
                  + x.getCd2Element().get_SourcePositionStart().getColumn()));
    }

    for (CDMemberDiff<ASTCDMethod> x : matchedMethodeList) {
      add.put(
          x.printCD2Element(),
          Integer.valueOf(
              x.getCd2Element().get_SourcePositionStart().getLine()
                  + ""
                  + x.getCd2Element().get_SourcePositionStart().getColumn()));
      String tmp = bodyOffset + pp.prettyprint((ASTCDBasisNode) x.getCd2Element());
      if (x.getDiffList().size() > 0) {
        tmp = bodyOffsetChange + pp.prettyprint((ASTCDBasisNode) x.getCd2Element());
      }
      if (tmp.contains("\n")) {
        tmp = tmp.split("\n")[0];
      }
      addNC.put(
          tmp,
          Integer.valueOf(
              x.getCd2Element().get_SourcePositionStart().getLine()
                  + ""
                  + x.getCd2Element().get_SourcePositionStart().getColumn()));
    }

    for (ASTCDMethod x : deletedMethods) {
      StringBuilder delMethods = new StringBuilder();
      String deletedMethode = pp.prettyprint((ASTCDBasisNode) x);
      if (deletedMethode.contains("\n")) {
        deletedMethode = deletedMethode.split("\n")[0];
      }
      matchDelNC.put(
          bodyOffsetDel + deletedMethode,
          Integer.valueOf(
              x.get_SourcePositionStart().getLine()
                  + ""
                  + x.get_SourcePositionStart().getColumn()));

      delMethods.append(COLOR_DELETE).append(deletedMethode).append(RESET);
      matchDel.put(
          delMethods.toString(),
          Integer.valueOf(
              x.get_SourcePositionStart().getLine()
                  + ""
                  + x.get_SourcePositionStart().getColumn()));
    }
    for (ASTCDAttribute x : deletedAttributes) {
      StringBuilder delAttri = new StringBuilder();
      String deletedAttribute = pp.prettyprint(x);
      if (deletedAttribute.contains("\n")) {
        deletedAttribute = deletedAttribute.split("\n")[0];
      }
      matchDelNC.put(
          bodyOffsetDel + deletedAttribute,
          Integer.valueOf(
              x.get_SourcePositionStart().getLine()
                  + ""
                  + x.get_SourcePositionStart().getColumn()));

      delAttri.append(COLOR_DELETE).append(deletedAttribute).append(RESET);
      matchDel.put(
          delAttri.toString(),
          Integer.valueOf(
              x.get_SourcePositionStart().getLine()
                  + ""
                  + x.get_SourcePositionStart().getColumn()));
    }

    for (ASTCDMethod x : addedMethode) {
      StringBuilder addMeth = new StringBuilder();
      String addedMethode = pp.prettyprint((ASTCDBasisNode) x);
      if (addedMethode.contains("\n")) {
        addedMethode = addedMethode.split("\n")[0];
      }
      addNC.put(
          bodyOffsetAdd + addedMethode,
          Integer.valueOf(
              x.get_SourcePositionStart().getLine()
                  + ""
                  + x.get_SourcePositionStart().getColumn()));

      addMeth.append(COLOR_ADD).append(addedMethode).append(RESET);
      add.put(
          addMeth.toString(),
          Integer.valueOf(
              x.get_SourcePositionStart().getLine()
                  + ""
                  + x.get_SourcePositionStart().getColumn()));
    }
    for (ASTCDAttribute x : addedAttributes) {
      StringBuilder addAttri = new StringBuilder();
      String addedAttribute = pp.prettyprint(x);
      if (addedAttribute.contains("\n")) {
        addedAttribute = addedAttribute.split("\n")[0];
      }
      addNC.put(
          bodyOffsetAdd + addedAttribute,
          Integer.valueOf(
              x.get_SourcePositionStart().getLine()
                  + ""
                  + x.get_SourcePositionStart().getColumn()));

      addAttri.append(COLOR_ADD).append(addedAttribute).append(RESET);
      add.put(
          addAttri.toString(),
          Integer.valueOf(
              x.get_SourcePositionStart().getLine()
                  + ""
                  + x.get_SourcePositionStart().getColumn()));
    }

    Map<Integer, String> matchAndDelete =
        matchDel.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(
                Collectors.toMap(
                    Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    matchAndDelete.forEach(
        (k, v) -> bodyCD1.append(bodyOffset).append(v).append(System.lineSeparator()));

    Map<Integer, String> matchAndDeleteNC =
        matchDelNC.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(
                Collectors.toMap(
                    Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    matchAndDeleteNC.forEach((k, v) -> bodyCD1NC.append(v).append(System.lineSeparator()));

    Map<Integer, String> matchAndAdd =
        add.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(
                Collectors.toMap(
                    Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    matchAndAdd.forEach(
        (k, v) -> bodyCD2.append(bodyOffset).append(v).append(System.lineSeparator()));

    Map<Integer, String> matchAndAddNC =
        addNC.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(
                Collectors.toMap(
                    Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    matchAndAddNC.forEach((k, v) -> bodyCD2NC.append(v).append(System.lineSeparator()));

    outputCD1.append(signatureCD1);
    if (bodyCD1.toString().length() > 0) {
      outputCD1.append("{ ").append(System.lineSeparator()).append(bodyCD1).append("}");
    } else {
      outputCD1.append(";");
    }

    outputCD1NC.append(signatureCD1NC);
    if (bodyCD1NC.toString().length() > 0) {
      outputCD1NC.append("{ ").append(System.lineSeparator()).append(bodyCD1NC).append("}");
    } else {
      outputCD1NC.append(";");
    }

    cd1Print = outputCD1.toString();
    cd1PrintNC = outputCD1NC.toString();

    outputCD2.append(signatureCD2);
    if (bodyCD2.toString().length() > 0) {
      outputCD2.append("{ ").append(System.lineSeparator()).append(bodyCD2).append("}");
    } else {
      outputCD2.append(";");
    }

    outputCD2NC.append(signatureCD2NC);
    if (bodyCD2NC.toString().length() > 0) {
      outputCD2NC.append("{ ").append(System.lineSeparator()).append(bodyCD2NC).append("}");
    } else {
      outputCD2NC.append(";");
    }

    cd2Print = outputCD2.toString();
    cd2PrintNC = outputCD2NC.toString();
  }

  /** Print function for the CDTypeDiff, used to output the diffs appropriately formated */
  public String printCD1() {
    return cd1Print;
  }

  public String printCD1NC() {
    return cd1PrintNC;
  }

  public String printCD2() {
    return cd2Print;
  }

  public String printCD2NC() {
    return cd2PrintNC;
  }
}
