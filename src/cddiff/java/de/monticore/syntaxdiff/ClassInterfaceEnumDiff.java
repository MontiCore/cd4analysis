package de.monticore.syntaxdiff;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDExtendUsage;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.*;
import java.util.stream.Collectors;

public class ClassInterfaceEnumDiff<ASTNodeType1 extends ASTNode, ASTNodeType2 extends ASTNode>
    extends AbstractDiffType {
  CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

  protected final ASTNodeType1 cd1Element;

  protected final ASTNodeType2 cd2Element;

  private String ppModifier1, ppName1, ppExtended1, ppInter1, ppModifier2, ppName2, ppExtended2,
      ppInter2, cd1Print, cd2Print, keywordCD1, keywordCD2;

  protected List<ElementDiff<ASTCDAttribute>> matchedAttributesList;

  protected List<ASTCDAttribute> deleletedAttributes;

  protected List<ASTCDAttribute> addedAttributes;

  protected List<ElementDiff<ASTCDMethod>> matchedMethodeList;

  protected List<ASTCDMethod> deleletedMethodes;

  protected List<ASTCDMethod> addedMethode;

  protected List<ElementDiff<ASTCDConstructor>> matchedConstructorList;

  protected List<ASTCDConstructor> deleletedConstructor;

  protected List<ASTCDConstructor> addedConstructor;

  protected List<ElementDiff<ASTCDEnumConstant>> matchedEnumConstantList;

  protected List<ASTCDEnumConstant> deleletedEnumConstants;

  protected List<ASTCDEnumConstant> addedEnumConstant;

  public List<ASTCDEnumConstant> getAddedEnumConstant() {
    return addedEnumConstant;
  }

  public List<ASTCDEnumConstant> getDeleletedEnumConstants() {
    return deleletedEnumConstants;
  }

  public List<ElementDiff<ASTCDAttribute>> getMatchedAttributesList() {
    return matchedAttributesList;
  }

  public List<ASTCDAttribute> getDeleletedAttributes() {
    return deleletedAttributes;
  }

  public List<ASTCDAttribute> getAddedAttributes() {
    return addedAttributes;
  }

  public ASTNodeType1 getCd1Element() {
    return cd1Element;
  }

  public ASTNodeType2 getCd2Element() {
    return cd2Element;
  }

  public ClassInterfaceEnumDiff(ASTNodeType1 cd1Element, ASTNodeType2 cd2Element) {
    this.cd1Element = cd1Element;
    this.cd2Element = cd2Element;

    if ((cd1Element instanceof ASTCDClass) && (cd2Element instanceof ASTCDClass)) {
      keywordCD1 = "class";
      keywordCD2 = "class";
      createClassDiff((ASTCDClass) cd1Element, (ASTCDClass) cd2Element);

    }
    else if (cd1Element instanceof ASTCDInterface && cd2Element instanceof ASTCDInterface) {
      keywordCD1 = "interface";
      keywordCD2 = "interface";
      createInterfaceDiff((ASTCDInterface) cd1Element, (ASTCDInterface) cd2Element);

    }
    else if (cd1Element instanceof ASTCDEnum && cd2Element instanceof ASTCDEnum) {
      keywordCD1 = "enum";
      keywordCD2 = "enum";
      createEnumDiff((ASTCDEnum) cd1Element, (ASTCDEnum) cd2Element);

    }
    else if (cd1Element instanceof ASTCDInterface && cd2Element instanceof ASTCDClass) {
      keywordCD1 = "interface";
      keywordCD2 = "class";
      createInterfaceClassDiff((ASTCDInterface) cd1Element, (ASTCDClass) cd2Element);
      this.interpretation.append("Interface")
          .append(": ")
          .append(SyntaxDiff.Interpretation.REPURPOSED)
          .append(" ");
      this.interpretationList.add(SyntaxDiff.Interpretation.REPURPOSED);

    }
    else if (cd1Element instanceof ASTCDClass && cd2Element instanceof ASTCDInterface) {
      keywordCD1 = "class";
      keywordCD2 = "interface";
      createClassInterfaceDiff((ASTCDClass) cd1Element, (ASTCDInterface) cd2Element);
      this.interpretation.append("Class")
          .append(": ")
          .append(SyntaxDiff.Interpretation.REPURPOSED)
          .append(" ");
      this.interpretationList.add(SyntaxDiff.Interpretation.REPURPOSED);
    }

    this.diffSize = calculateDiffSize();
    setStrings();
  }

  /**
   * Calculation of the diff size between the given classes, automaticly calculated on object
   * creation Name changes are weighted more and each member(attribute/methodes/...) add at most one
   * to the size
   *
   * @return Diff size as double
   */
  private double calculateDiffSize() {
    //Diff size of signature
    double size = diffList.size();

    // Diff size of attributes (amounts to max ~ 1)
    for (ElementDiff<ASTCDAttribute> i : matchedAttributesList) {
      if (i.getCd1Element().isPresentInitial()) {
        size += i.getDiffSize() / 4.0;
      }
      else {
        size += i.getDiffSize() / 3.0;
      }
    }
    size += deleletedAttributes.size() + addedAttributes.size();

    //Todo: Add Parameter Diff to size calculation
    //Diff size of methodes
    for (ElementDiff<ASTCDMethod> i : matchedMethodeList) {
      if (i.getCd1Element().isPresentCDThrowsDeclaration()) {
        size += i.getDiffSize() / 4.0;
      }
      else {
        size += i.getDiffSize() / 3.0;
      }
    }
    size += deleletedMethodes.size() + addedMethode.size();

    // Diff size of constructors
    for (ElementDiff<ASTCDConstructor> i : matchedConstructorList) {
      if (i.getCd1Element().isPresentCDThrowsDeclaration()) {
        size += i.getDiffSize() / 4.0;
      }
      else {
        size += i.getDiffSize() / 3.0;
      }
    }
    size += deleletedConstructor.size() + addedConstructor.size();
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

  private void createDiffList(ASTCDClass cd1Element, ASTCDInterface cd2Element) {
    List<FieldDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();
    interpretation.append("Interpretation: ");

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Element.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Element.getModifier());
    FieldDiff<ASTModifier, ASTModifier> attributeModifier = new FieldDiff<>(cd1Modi, cd2Modi);

    ppModifier1 = getColorCode(attributeModifier) + pp.prettyprint(cd1Modi.get()) + RESET;
    ppModifier2 = getColorCode(attributeModifier) + pp.prettyprint(cd2Modi.get()) + RESET;

    if (attributeModifier.isPresent()) {
      diffs.add(attributeModifier);
      if (attributeModifier.getInterpretation().isPresent()) {
        interpretation.append("Modifier")
            .append(": ")
            .append(attributeModifier.getInterpretation().get())
            .append(" ");
      }
    }
    // Name, non-optional
    Optional<ASTCDClass> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDInterface> cd2Name = Optional.of(cd2Element);
    FieldDiff<ASTCDClass, ASTCDInterface> className = new FieldDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      className = new FieldDiff<>(SyntaxDiff.Op.CHANGE, cd1Name, cd2Name);
    }

    ppName1 = getColorCode(className) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(className) + cd2Name.get().getName() + RESET;

    if (className.isPresent()) {
      diffs.add(className);
      if (className.getInterpretation().isPresent()) {
        interpretation.append("Name")
            .append(": ")
            .append(className.getInterpretation().get())
            .append(" ");
      }
    }
    if (cd1Name.get().getName().equals(cd2Name.get().getName()) && !cd1Name.get()
        .getSymbol()
        .getFullName()
        .equals(cd2Name.get().getSymbol().getFullName())) {
      interpretation.append("Package")
          .append(": ")
          .append(SyntaxDiff.Interpretation.RELOCATION)
          .append(" ");
    }

    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend = (cd1Element.isPresentCDExtendUsage()) ?
        Optional.of(cd1Element.getCDExtendUsage()) :
        Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend = (cd2Element.isPresentCDExtendUsage()) ?
        Optional.of(cd2Element.getCDExtendUsage()) :
        Optional.empty();
    FieldDiff<ASTCDExtendUsage, ASTCDExtendUsage> classExtended = new FieldDiff<>(cd1Extend,
        cd2Extend);

    cd1Extend.ifPresent(
        initial -> ppExtended1 = getColorCode(classExtended) + pp.prettyprint(initial) + RESET);
    cd2Extend.ifPresent(
        initial -> ppExtended2 = getColorCode(classExtended) + pp.prettyprint(initial) + RESET);

    if (classExtended.isPresent()) {
      diffs.add(classExtended);
      if (classExtended.getInterpretation().isPresent()) {
        interpretation.append("Extended")
            .append(": ")
            .append(classExtended.getInterpretation().get())
            .append(" ");
      }
    }

    // Todo: add inherited methods and attributes

    // Set the difflist to signature diffs(if any)
    this.diffList = diffs;

    // Create trivial matches for attributes/constructors/methods
    this.matchedAttributesList = getMatchingList(
        getElementDiffList(cd1Element.getCDAttributeList(), cd2Element.getCDAttributeList()));
    this.deleletedAttributes = absentElementList(matchedAttributesList,
        cd1Element.getCDAttributeList());
    this.addedAttributes = absentElementList(matchedAttributesList,
        cd2Element.getCDAttributeList());

    this.matchedMethodeList = getMatchingList(
        getElementDiffList(cd1Element.getCDMethodList(), cd2Element.getCDMethodList()));
    this.deleletedMethodes = absentElementList(matchedMethodeList, cd1Element.getCDMethodList());
    this.addedMethode = absentElementList(matchedMethodeList, cd2Element.getCDMethodList());

    this.matchedConstructorList = getMatchingList(
        getElementDiffList(cd1Element.getCDConstructorList(), cd2Element.getCDConstructorList()));
    this.deleletedConstructor = absentElementList(matchedConstructorList,
        cd1Element.getCDConstructorList());
    this.addedConstructor = absentElementList(matchedConstructorList,
        cd2Element.getCDConstructorList());
  }

  private void createDiffList(ASTCDInterface cd1Element, ASTCDInterface cd2Element) {
    List<FieldDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();
    interpretation.append("Interpretation: ");

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Element.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Element.getModifier());
    FieldDiff<ASTModifier, ASTModifier> attributeModifier = new FieldDiff<>(cd1Modi, cd2Modi);

    ppModifier1 = getColorCode(attributeModifier) + pp.prettyprint(cd1Modi.get()) + RESET;
    ppModifier2 = getColorCode(attributeModifier) + pp.prettyprint(cd2Modi.get()) + RESET;

    if (attributeModifier.isPresent()) {
      diffs.add(attributeModifier);
      if (attributeModifier.getInterpretation().isPresent()) {
        interpretation.append("Modifier")
            .append(": ")
            .append(attributeModifier.getInterpretation().get())
            .append(" ");
      }
    }

    // Name, non-optional
    Optional<ASTCDInterface> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDInterface> cd2Name = Optional.of(cd2Element);
    FieldDiff<ASTCDInterface, ASTCDInterface> interfaceName = new FieldDiff<>(null, cd1Name,
        cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      interfaceName = new FieldDiff<>(SyntaxDiff.Op.CHANGE, cd1Name, cd2Name);
    }

    ppName1 = getColorCode(interfaceName) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(interfaceName) + cd2Name.get().getName() + RESET;

    if (interfaceName.isPresent()) {
      diffs.add(interfaceName);
      if (interfaceName.getInterpretation().isPresent()) {
        interpretation.append("Name")
            .append(": ")
            .append(interfaceName.getInterpretation().get())
            .append(" ");
      }
    }
    if (cd1Name.get().getName().equals(cd2Name.get().getName()) && !cd1Name.get()
        .getSymbol()
        .getFullName()
        .equals(cd2Name.get().getSymbol().getFullName())) {
      interpretation.append("Package")
          .append(": ")
          .append(SyntaxDiff.Interpretation.RELOCATION)
          .append(" ");
    }

    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend = (cd1Element.isPresentCDExtendUsage()) ?
        Optional.of(cd1Element.getCDExtendUsage()) :
        Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend = (cd2Element.isPresentCDExtendUsage()) ?
        Optional.of(cd2Element.getCDExtendUsage()) :
        Optional.empty();
    FieldDiff<ASTCDExtendUsage, ASTCDExtendUsage> interfaceExtended = new FieldDiff<>(cd1Extend,
        cd2Extend);

    cd1Extend.ifPresent(
        initial -> ppExtended1 = getColorCode(interfaceExtended) + pp.prettyprint(initial) + RESET);
    cd2Extend.ifPresent(
        initial -> ppExtended2 = getColorCode(interfaceExtended) + pp.prettyprint(initial) + RESET);

    if (interfaceExtended.isPresent()) {
      diffs.add(interfaceExtended);
      if (interfaceExtended.getInterpretation().isPresent()) {
        interpretation.append("Extended")
            .append(": ")
            .append(interfaceExtended.getInterpretation().get())
            .append(" ");
      }
    }

    // Todo: add inherited methods and attributes

    // Set the difflist to signature diffs(if any)
    this.diffList = diffs;

    // Create trivial matches for attributes/constructors/methods
    this.matchedAttributesList = getMatchingList(
        getElementDiffList(cd1Element.getCDAttributeList(), cd2Element.getCDAttributeList()));
    this.deleletedAttributes = absentElementList(matchedAttributesList,
        cd1Element.getCDAttributeList());
    this.addedAttributes = absentElementList(matchedAttributesList,
        cd2Element.getCDAttributeList());

    this.matchedMethodeList = getMatchingList(
        getElementDiffList(cd1Element.getCDMethodList(), cd2Element.getCDMethodList()));
    this.deleletedMethodes = absentElementList(matchedMethodeList, cd1Element.getCDMethodList());
    this.addedMethode = absentElementList(matchedMethodeList, cd2Element.getCDMethodList());

    this.matchedConstructorList = getMatchingList(
        getElementDiffList(cd1Element.getCDConstructorList(), cd2Element.getCDConstructorList()));
    this.deleletedConstructor = absentElementList(matchedConstructorList,
        cd1Element.getCDConstructorList());
    this.addedConstructor = absentElementList(matchedConstructorList,
        cd2Element.getCDConstructorList());
  }

  private void createDiffList(ASTCDEnum cd1Element, ASTCDEnum cd2Element) {
    List<FieldDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();
    interpretation.append("Interpretation: ");

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Element.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Element.getModifier());
    FieldDiff<ASTModifier, ASTModifier> attributeModifier = new FieldDiff<>(cd1Modi, cd2Modi);

    ppModifier1 = getColorCode(attributeModifier) + pp.prettyprint(cd1Modi.get()) + RESET;
    ppModifier2 = getColorCode(attributeModifier) + pp.prettyprint(cd2Modi.get()) + RESET;

    if (attributeModifier.isPresent()) {
      diffs.add(attributeModifier);
      if (attributeModifier.getInterpretation().isPresent()) {
        interpretation.append("Enum modifier")
            .append(": ")
            .append(attributeModifier.getInterpretation().get())
            .append(" ");
      }
    }

    // Name, non-optional
    Optional<ASTCDEnum> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDEnum> cd2Name = Optional.of(cd2Element);
    FieldDiff<ASTCDEnum, ASTCDEnum> name = new FieldDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      name = new FieldDiff<>(SyntaxDiff.Op.CHANGE, cd1Name, cd2Name);
    }

    ppName1 = getColorCode(name) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(name) + cd2Name.get().getName() + RESET;

    if (name.isPresent()) {
      diffs.add(name);
      if (name.getInterpretation().isPresent()) {
        interpretation.append("Name")
            .append(": ")
            .append(name.getInterpretation().get())
            .append(" ");
      }
    }
    if (cd1Name.get().getName().equals(cd2Name.get().getName()) && !cd1Name.get()
        .getSymbol()
        .getFullName()
        .equals(cd2Name.get().getSymbol().getFullName())) {
      interpretation.append("Package")
          .append(": ")
          .append(SyntaxDiff.Interpretation.RELOCATION)
          .append(" ");
    }

    // Implements, optional
    Optional<ASTMCObjectType> cd1Imple = (cd1Element.isPresentCDInterfaceUsage()) ?
        Optional.of(cd1Element.getInterfaceList().get(0)) :
        Optional.empty();
    Optional<ASTMCObjectType> cd2Imple = (cd2Element.isPresentCDInterfaceUsage()) ?
        Optional.of(cd2Element.getInterfaceList().get(0)) :
        Optional.empty();
    FieldDiff<ASTMCObjectType, ASTMCObjectType> classInterfaceuse = new FieldDiff<>(cd1Imple,
        cd2Imple);

    cd1Imple.ifPresent(
        inter -> ppInter1 = getColorCode(classInterfaceuse) + pp.prettyprint(inter) + RESET);
    cd2Imple.ifPresent(
        inter -> ppInter2 = getColorCode(classInterfaceuse) + pp.prettyprint(inter) + RESET);

    if (classInterfaceuse.isPresent()) {
      diffs.add(classInterfaceuse);
      if (classInterfaceuse.getInterpretation().isPresent()) {
        interpretation.append("Interface")
            .append(": ")
            .append(classInterfaceuse.getInterpretation().get())
            .append(" ");
      }
    }
    this.diffList = diffs;

    // Create trivial matches for attributes/constructors/methods
    this.matchedAttributesList = getMatchingList(
        getElementDiffList(cd1Element.getCDAttributeList(), cd2Element.getCDAttributeList()));
    this.deleletedAttributes = absentElementList(matchedAttributesList,
        cd1Element.getCDAttributeList());
    this.addedAttributes = absentElementList(matchedAttributesList,
        cd2Element.getCDAttributeList());

    this.matchedMethodeList = getMatchingList(
        getElementDiffList(cd1Element.getCDMethodList(), cd2Element.getCDMethodList()));
    this.deleletedMethodes = absentElementList(matchedMethodeList, cd1Element.getCDMethodList());
    this.addedMethode = absentElementList(matchedMethodeList, cd2Element.getCDMethodList());

    this.matchedConstructorList = getMatchingList(
        getElementDiffList(cd1Element.getCDConstructorList(), cd2Element.getCDConstructorList()));
    this.deleletedConstructor = absentElementList(matchedConstructorList,
        cd1Element.getCDConstructorList());
    this.addedConstructor = absentElementList(matchedConstructorList,
        cd2Element.getCDConstructorList());

    this.matchedEnumConstantList = getMatchingList(
        getElementDiffList(cd1Element.getCDEnumConstantList(), cd2Element.getCDEnumConstantList()));
    this.deleletedEnumConstants = absentElementList(matchedEnumConstantList,
        cd1Element.getCDEnumConstantList());
    this.addedEnumConstant = absentElementList(matchedEnumConstantList,
        cd2Element.getCDEnumConstantList());
  }

  private void createDiffList(ASTCDClass cd1Element, ASTCDClass cd2Element) {
    List<FieldDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();
    interpretation.append("Interpretation: ");

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Element.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Element.getModifier());
    FieldDiff<ASTModifier, ASTModifier> attributeModifier = new FieldDiff<>(cd1Modi, cd2Modi);

    ppModifier1 = getColorCode(attributeModifier) + pp.prettyprint(cd1Modi.get()) + RESET;
    ppModifier2 = getColorCode(attributeModifier) + pp.prettyprint(cd2Modi.get()) + RESET;

    if (attributeModifier.isPresent()) {
      diffs.add(attributeModifier);
      if (attributeModifier.getInterpretation().isPresent()) {
        interpretation.append("Modifier")
            .append(": ")
            .append(attributeModifier.getInterpretation().get())
            .append(" ");
      }
    }

    // Name, non-optional
    Optional<ASTCDClass> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDClass> cd2Name = Optional.of(cd2Element);
    FieldDiff<ASTCDClass, ASTCDClass> className = new FieldDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      className = new FieldDiff<>(SyntaxDiff.Op.CHANGE, cd1Name, cd2Name);
    }

    ppName1 = getColorCode(className) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(className) + cd2Name.get().getName() + RESET;

    if (className.isPresent()) {
      diffs.add(className);
      if (className.getInterpretation().isPresent()) {
        interpretation.append("Name")
            .append(": ")
            .append(className.getInterpretation().get())
            .append(" ");
      }
    }
    if (cd1Name.get().getName().equals(cd2Name.get().getName()) && !cd1Name.get()
        .getSymbol()
        .getFullName()
        .equals(cd2Name.get().getSymbol().getFullName())) {
      interpretation.append("Package")
          .append(": ")
          .append(SyntaxDiff.Interpretation.RELOCATION)
          .append(" ");
    }

    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend = (cd1Element.isPresentCDExtendUsage()) ?
        Optional.of(cd1Element.getCDExtendUsage()) :
        Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend = (cd2Element.isPresentCDExtendUsage()) ?
        Optional.of(cd2Element.getCDExtendUsage()) :
        Optional.empty();
    FieldDiff<ASTCDExtendUsage, ASTCDExtendUsage> classExtended = new FieldDiff<>(cd1Extend,
        cd2Extend);

    cd1Extend.ifPresent(
        initial -> ppExtended1 = getColorCode(classExtended) + pp.prettyprint(initial) + RESET);
    cd2Extend.ifPresent(
        initial -> ppExtended2 = getColorCode(classExtended) + pp.prettyprint(initial) + RESET);

    if (classExtended.isPresent()) {
      diffs.add(classExtended);
      if (classExtended.getInterpretation().isPresent()) {
        interpretation.append("Extended")
            .append(": ")
            .append(classExtended.getInterpretation().get())
            .append(" ");
      }
    }

    // Implements, optional
    Optional<ASTMCObjectType> cd1Imple = (cd1Element.isPresentCDInterfaceUsage()) ?
        Optional.of(cd1Element.getInterfaceList().get(0)) :
        Optional.empty();
    Optional<ASTMCObjectType> cd2Imple = (cd2Element.isPresentCDInterfaceUsage()) ?
        Optional.of(cd2Element.getInterfaceList().get(0)) :
        Optional.empty();
    FieldDiff<ASTMCObjectType, ASTMCObjectType> classInterfaceuse = new FieldDiff<>(cd1Imple,
        cd2Imple);

    cd1Imple.ifPresent(
        inter -> ppInter1 = getColorCode(classInterfaceuse) + pp.prettyprint(inter) + RESET);
    cd2Imple.ifPresent(
        inter -> ppInter2 = getColorCode(classInterfaceuse) + pp.prettyprint(inter) + RESET);

    if (classInterfaceuse.isPresent()) {
      diffs.add(classInterfaceuse);
      if (classInterfaceuse.getInterpretation().isPresent()) {
        interpretation.append("Interface")
            .append(": ")
            .append(classInterfaceuse.getInterpretation().get())
            .append(" ");
      }
    }

    // Todo: add inherited methods and attributes

    // Set the difflist to signature diffs(if any)
    this.diffList = diffs;

    // Create trivial matches for attributes/constructors/methods
    this.matchedAttributesList = getMatchingList(
        getElementDiffList(cd1Element.getCDAttributeList(), cd2Element.getCDAttributeList()));
    this.deleletedAttributes = absentElementList(matchedAttributesList,
        cd1Element.getCDAttributeList());
    this.addedAttributes = absentElementList(matchedAttributesList,
        cd2Element.getCDAttributeList());

    this.matchedMethodeList = getMatchingList(
        getElementDiffList(cd1Element.getCDMethodList(), cd2Element.getCDMethodList()));
    this.deleletedMethodes = absentElementList(matchedMethodeList, cd1Element.getCDMethodList());
    this.addedMethode = absentElementList(matchedMethodeList, cd2Element.getCDMethodList());

    this.matchedConstructorList = getMatchingList(
        getElementDiffList(cd1Element.getCDConstructorList(), cd2Element.getCDConstructorList()));
    this.deleletedConstructor = absentElementList(matchedConstructorList,
        cd1Element.getCDConstructorList());
    this.addedConstructor = absentElementList(matchedConstructorList,
        cd2Element.getCDConstructorList());

  }

  private void createDiffList(ASTCDInterface cd1Element, ASTCDClass cd2Element) {
    List<FieldDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();
    interpretation.append("Interpretation: ");

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Element.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Element.getModifier());
    FieldDiff<ASTModifier, ASTModifier> attributeModifier = new FieldDiff<>(cd1Modi, cd2Modi);

    ppModifier1 = getColorCode(attributeModifier) + pp.prettyprint(cd1Modi.get()) + RESET;
    ppModifier2 = getColorCode(attributeModifier) + pp.prettyprint(cd2Modi.get()) + RESET;

    if (attributeModifier.isPresent()) {
      diffs.add(attributeModifier);
      if (attributeModifier.getInterpretation().isPresent()) {
        interpretation.append("Modifier")
            .append(": ")
            .append(attributeModifier.getInterpretation().get())
            .append(" ");
      }
    }
    // Name, non-optional
    Optional<ASTCDInterface> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDClass> cd2Name = Optional.of(cd2Element);
    FieldDiff<ASTCDInterface, ASTCDClass> className = new FieldDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())) {
      className = new FieldDiff<>(SyntaxDiff.Op.CHANGE, cd1Name, cd2Name);
    }

    ppName1 = getColorCode(className) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(className) + cd2Name.get().getName() + RESET;

    if (className.isPresent()) {
      diffs.add(className);
      if (className.getInterpretation().isPresent()) {
        interpretation.append("Name")
            .append(": ")
            .append(className.getInterpretation().get())
            .append(" ");
      }
    }
    if (cd1Name.get().getName().equals(cd2Name.get().getName()) && !cd1Name.get()
        .getSymbol()
        .getFullName()
        .equals(cd2Name.get().getSymbol().getFullName())) {
      interpretation.append("Package")
          .append(": ")
          .append(SyntaxDiff.Interpretation.RELOCATION)
          .append(" ");
    }

    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend = (cd1Element.isPresentCDExtendUsage()) ?
        Optional.of(cd1Element.getCDExtendUsage()) :
        Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend = (cd2Element.isPresentCDExtendUsage()) ?
        Optional.of(cd2Element.getCDExtendUsage()) :
        Optional.empty();
    FieldDiff<ASTCDExtendUsage, ASTCDExtendUsage> classExtended = new FieldDiff<>(cd1Extend,
        cd2Extend);

    cd1Extend.ifPresent(
        initial -> ppExtended1 = getColorCode(classExtended) + pp.prettyprint(initial) + RESET);
    cd2Extend.ifPresent(
        initial -> ppExtended2 = getColorCode(classExtended) + pp.prettyprint(initial) + RESET);

    if (classExtended.isPresent()) {
      diffs.add(classExtended);
      if (classExtended.getInterpretation().isPresent()) {
        interpretation.append("Extended")
            .append(": ")
            .append(classExtended.getInterpretation().get())
            .append(" ");
      }
    }

    // Todo: add inherited methods and attributes

    // Set the difflist to signature diffs(if any)
    this.diffList = diffs;

    // Create trivial matches for attributes/constructors/methods
    this.matchedAttributesList = getMatchingList(
        getElementDiffList(cd1Element.getCDAttributeList(), cd2Element.getCDAttributeList()));
    this.deleletedAttributes = absentElementList(matchedAttributesList,
        cd1Element.getCDAttributeList());
    this.addedAttributes = absentElementList(matchedAttributesList,
        cd2Element.getCDAttributeList());

    this.matchedMethodeList = getMatchingList(
        getElementDiffList(cd1Element.getCDMethodList(), cd2Element.getCDMethodList()));
    this.deleletedMethodes = absentElementList(matchedMethodeList, cd1Element.getCDMethodList());
    this.addedMethode = absentElementList(matchedMethodeList, cd2Element.getCDMethodList());

    this.matchedConstructorList = getMatchingList(
        getElementDiffList(cd1Element.getCDConstructorList(), cd2Element.getCDConstructorList()));
    this.deleletedConstructor = absentElementList(matchedConstructorList,
        cd1Element.getCDConstructorList());
    this.addedConstructor = absentElementList(matchedConstructorList,
        cd2Element.getCDConstructorList());

  }

  private void setStrings() {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    StringBuilder outputCD1 = new StringBuilder();
    StringBuilder outputCD2 = new StringBuilder();

    StringBuilder bodyCD1 = new StringBuilder();
    StringBuilder bodyCD2 = new StringBuilder();

    String signatureCD1 = combineWithoutNulls(
        Arrays.asList(ppModifier1, RESET + keywordCD1, ppName1, ppExtended1, ppInter1));
    String signatureCD2 = combineWithoutNulls(
        Arrays.asList(ppModifier2, RESET + keywordCD2, ppName2, ppExtended2, ppInter2));

    //Map<String, Integer> matchDel = new HashMap();
    Map<String, Integer> add = new HashMap<>();
    Map<String, Integer> matchDel = new HashMap<>();

    for (ElementDiff<ASTCDAttribute> x : matchedAttributesList) {
      matchDel.put(x.printCD1Element(), Integer.valueOf(
          x.getCd1Element().get_SourcePositionStart().getLine() + "" + x.getCd1Element()
              .get_SourcePositionStart()
              .getColumn()));
    }

    for (ElementDiff<ASTCDMethod> x : matchedMethodeList) {
      matchDel.put(x.printCD1Element(), Integer.valueOf(
          x.getCd1Element().get_SourcePositionStart().getLine() + "" + x.getCd1Element()
              .get_SourcePositionStart()
              .getColumn()));
    }

    if (matchedEnumConstantList != null) {
      for (ElementDiff<ASTCDEnumConstant> x : matchedEnumConstantList) {
        matchDel.put(x.printCD1Element(), Integer.valueOf(
            x.getCd1Element().get_SourcePositionStart().getLine() + "" + x.getCd1Element()
                .get_SourcePositionStart()
                .getColumn()));
      }
      for (ElementDiff<ASTCDEnumConstant> x : matchedEnumConstantList) {
        add.put(x.printCD2Element(), Integer.valueOf(
            x.getCd2Element().get_SourcePositionStart().getLine() + "" + x.getCd2Element()
                .get_SourcePositionStart()
                .getColumn()));
      }
      for (ASTCDEnumConstant x : getDeleletedEnumConstants()) {
        StringBuilder delEnumConstant = new StringBuilder();
        String deletedEnumConstant = pp.prettyprint(x);
        if (deletedEnumConstant.contains("\n")) {
          deletedEnumConstant = deletedEnumConstant.split("\n")[0];
        }
        delEnumConstant.append(COLOR_DELETE).append(deletedEnumConstant).append(RESET);
        matchDel.put(delEnumConstant.toString(), Integer.valueOf(
            x.get_SourcePositionStart().getLine() + "" + x.get_SourcePositionStart().getColumn()));
      }
      for (ASTCDEnumConstant x : addedEnumConstant) {
        StringBuilder addEnumConst = new StringBuilder();
        String addedEnumConstant = pp.prettyprint(x);
        if (addedEnumConstant.contains("\n")) {
          addedEnumConstant = addedEnumConstant.split("\n")[0];
        }
        addEnumConst.append(COLOR_ADD).append(addedEnumConstant).append(RESET);
        add.put(addEnumConst.toString(), Integer.valueOf(
            x.get_SourcePositionStart().getLine() + "" + x.get_SourcePositionStart().getColumn()));
      }
    }

    for (ElementDiff<ASTCDAttribute> x : matchedAttributesList) {
      add.put(x.printCD2Element(), Integer.valueOf(
          x.getCd2Element().get_SourcePositionStart().getLine() + "" + x.getCd2Element()
              .get_SourcePositionStart()
              .getColumn()));
    }

    for (ElementDiff<ASTCDMethod> x : matchedMethodeList) {
      add.put(x.printCD2Element(), Integer.valueOf(
          x.getCd2Element().get_SourcePositionStart().getLine() + "" + x.getCd2Element()
              .get_SourcePositionStart()
              .getColumn()));
    }

    for (ASTCDMethod x : deleletedMethodes) {
      StringBuilder delMethodes = new StringBuilder();
      String deletedMethode = pp.prettyprint((ASTCDBasisNode) x);
      if (deletedMethode.contains("\n")) {
        deletedMethode = deletedMethode.split("\n")[0];
      }
      delMethodes.append(COLOR_DELETE).append(deletedMethode).append(RESET);
      matchDel.put(delMethodes.toString(), Integer.valueOf(
          x.get_SourcePositionStart().getLine() + "" + x.get_SourcePositionStart().getColumn()));
    }
    for (ASTCDAttribute x : deleletedAttributes) {
      StringBuilder delAttri = new StringBuilder();
      String deletedAttribute = pp.prettyprint(x);
      if (deletedAttribute.contains("\n")) {
        deletedAttribute = deletedAttribute.split("\n")[0];
      }
      delAttri.append(COLOR_DELETE).append(deletedAttribute).append(RESET);
      matchDel.put(delAttri.toString(), Integer.valueOf(
          x.get_SourcePositionStart().getLine() + "" + x.get_SourcePositionStart().getColumn()));
    }

    for (ASTCDMethod x : addedMethode) {
      StringBuilder addMeth = new StringBuilder();
      String addedMethode = pp.prettyprint((ASTCDBasisNode) x);
      if (addedMethode.contains("\n")) {
        addedMethode = addedMethode.split("\n")[0];
      }
      addMeth.append(COLOR_ADD).append(addedMethode).append(RESET);
      add.put(addMeth.toString(), Integer.valueOf(
          x.get_SourcePositionStart().getLine() + "" + x.get_SourcePositionStart().getColumn()));
    }
    for (ASTCDAttribute x : addedAttributes) {
      StringBuilder addAttri = new StringBuilder();
      String addedAttribute = pp.prettyprint(x);
      if (addedAttribute.contains("\n")) {
        addedAttribute = addedAttribute.split("\n")[0];
      }
      addAttri.append(COLOR_ADD).append(addedAttribute).append(RESET);
      add.put(addAttri.toString(), Integer.valueOf(
          x.get_SourcePositionStart().getLine() + "" + x.get_SourcePositionStart().getColumn()));
    }

    Map<Integer, String> matchAndDelete = matchDel.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue())
        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1,
            LinkedHashMap::new));
    matchAndDelete.forEach(
        (k, v) -> bodyCD1.append("     ").append(v).append(System.lineSeparator()));

    Map<Integer, String> matchAndAdd = add.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue())
        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1,
            LinkedHashMap::new));
    matchAndAdd.forEach((k, v) -> bodyCD2.append("     ").append(v).append(System.lineSeparator()));

    outputCD1.append(signatureCD1);
    if (bodyCD1.toString().length() > 0) {
      outputCD1.append("{ ").append(System.lineSeparator()).append(bodyCD1).append("}");
    }
    else {
      outputCD1.append(";");
    }

    cd1Print = outputCD1.toString();

    outputCD2.append(signatureCD2);
    if (bodyCD2.toString().length() > 0) {
      outputCD2.append("{ ").append(System.lineSeparator()).append(bodyCD2).append("}");
    }
    else {
      outputCD2.append(";");
    }
    cd2Print = outputCD2.toString();
  }

  /**
   * Print function for the class diff, used to output the diffs appropriately formated
   */
  public String printCD1() {
    return cd1Print;
  }

  public String printCD2() {
    return cd2Print;
  }

}
