package de.monticore.cddiff.syntaxdiff;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.umlmodifier._ast.ASTModifier;
import org.checkerframework.checker.nullness.Opt;

import java.util.*;
import java.util.stream.Collectors;

public class CDTypeDiff<ASTCDType1 extends ASTCDType, ASTCDType2 extends ASTCDType>
    extends AbstractDiffType {
  CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

  protected final ASTCDType1 cd1Element;

  protected final ASTCDType2 cd2Element;

  private String ppModifier1, ppName1, ppExtended1, ppInter1, ppModifier2, ppName2, ppExtended2,
      ppInter2, cd1Print, cd2Print, keywordCD1, keywordCD2;

  protected List<ElementDiff<ASTCDAttribute>> matchedAttributesList;

  protected List<ASTCDAttribute> deleletedAttributes;

  protected List<ASTCDAttribute> addedAttributes;

  protected List<ElementDiff<ASTCDMethod>> matchedMethodeList;

  protected List<ASTCDMethod> deleletedMethods;

  protected List<ASTCDMethod> addedMethode;

  protected List<ElementDiff<ASTCDConstructor>> matchedConstructorList;

  protected List<ASTCDConstructor> deleletedConstructor;

  protected List<ASTCDConstructor> addedConstructor;

  protected List<ElementDiff<ASTCDEnumConstant>> matchedEnumConstantList;

  protected List<ASTCDEnumConstant> deleletedEnumConstants;

  protected List<ASTCDEnumConstant> addedEnumConstants;

  ICD4CodeArtifactScope scopecd1;

  ICD4CodeArtifactScope scopecd2;

  public List<ElementDiff<ASTCDEnumConstant>> getMatchedEnumConstantList() {
    return matchedEnumConstantList;
  }

  public List<ASTCDEnumConstant> getAddedEnumConstants() {
    return addedEnumConstants;
  }

  public List<ASTCDEnumConstant> getDeleletedEnumConstants() {
    return deleletedEnumConstants;
  }

  public List<ElementDiff<ASTCDAttribute>> getMatchedAttributesList() {
    return matchedAttributesList;
  }

  public List<ASTCDAttribute> getAddedAttributes() {
    return addedAttributes;
  }

  public List<ASTCDAttribute> getDeleletedAttributes() {
    return deleletedAttributes;
  }

  public List<ElementDiff<ASTCDMethod>> getMatchedMethodeList() {
    return matchedMethodeList;
  }

  public List<ASTCDMethod> getAddedMethode() {
    return addedMethode;
  }

  public List<ASTCDMethod> getDeleletedMethods() {
    return deleletedMethods;
  }

  public List<ElementDiff<ASTCDConstructor>> getMatchedConstructorList() {
    return matchedConstructorList;
  }

  public List<ASTCDConstructor> getAddedConstructor() {
    return addedConstructor;
  }

  public List<ASTCDConstructor> getDeleletedConstructor() {
    return deleletedConstructor;
  }

  public ASTCDType1 getCd1Element() {
    return cd1Element;
  }

  public ASTCDType2 getCd2Element() {
    return cd2Element;
  }

  public CDTypeDiff(ASTCDType1 cd1Element, ASTCDType2 cd2Element, ICD4CodeArtifactScope scopecd1, ICD4CodeArtifactScope scopecd2) {
    this.cd1Element = cd1Element;
    this.cd2Element = cd2Element;
    this.scopecd1 = scopecd1;
    this.scopecd2 = scopecd2;

    createDefaultDiffList(cd1Element, cd2Element);

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
      keywordCD1 = COLOR_CHANGE + "interface" + RESET;
      keywordCD2 = COLOR_CHANGE + "class" + RESET;
      createInterfaceClassDiff((ASTCDInterface) cd1Element, (ASTCDClass) cd2Element);
      this.interpretation.append("Interface")
          .append(": ")
          .append(SyntaxDiff.Interpretation.REPURPOSED)
          .append(" ");
      this.interpretationList.add(SyntaxDiff.Interpretation.REPURPOSED);

    }
    else if (cd1Element instanceof ASTCDClass && cd2Element instanceof ASTCDInterface) {
      keywordCD1 = COLOR_CHANGE + "class" + RESET;
      keywordCD2 = COLOR_CHANGE + "interface" + RESET;
      createClassInterfaceDiff((ASTCDClass) cd1Element, (ASTCDInterface) cd2Element);
      this.interpretation.append("Class")
          .append(": ")
          .append(SyntaxDiff.Interpretation.REPURPOSED)
          .append(" ");
      this.interpretationList.add(SyntaxDiff.Interpretation.REPURPOSED);
    }

    // Set the diff lists for the current diff
    setCDMemberDiffLists(cd1Element, cd2Element, scopecd1, scopecd2);

    this.diffSize = calculateDiffSize();
    setStrings();
  }

  private void setCDMemberDiffLists(ASTCDType1 cd1Element, ASTCDType2 cd2Element, ICD4CodeArtifactScope scopecd1, ICD4CodeArtifactScope scopecd2) {
    Set<ASTCDType> superClassesCD1 = CDInheritanceHelper.getDirectSuperClasses(cd1Element, scopecd1);
    Set<ASTCDType> superClassesCD2 = CDInheritanceHelper.getDirectSuperClasses(cd2Element, scopecd2);

    List<ASTCDAttribute> attributeListCd1 = addInheritedAttributes(superClassesCD1, cd1Element.getCDAttributeList());
    List<ASTCDAttribute> attributeListCd2 = addInheritedAttributes(superClassesCD2, cd2Element.getCDAttributeList());

    List<ASTCDMethod> methodeListCd1 = addInheritedMethods(superClassesCD1, cd1Element.getCDMethodList());
    List<ASTCDMethod> methodeListCd2 = addInheritedMethods(superClassesCD2, cd2Element.getCDMethodList());

    // Create trivial matches for attributes/constructors/methods
    this.matchedAttributesList = getMatchingList(getElementDiffList(attributeListCd1, attributeListCd2));
    this.deleletedAttributes = absentElementList(matchedAttributesList, attributeListCd1);
    this.addedAttributes = absentElementList(matchedAttributesList, attributeListCd2);

    this.matchedMethodeList = getMatchingList(getElementDiffList(methodeListCd1, methodeListCd2));
    this.deleletedMethods = absentElementList(matchedMethodeList, methodeListCd1);
    this.addedMethode = absentElementList(matchedMethodeList, methodeListCd2);

    this.matchedConstructorList = getMatchingList(
      getElementDiffList(cd1Element.getCDConstructorList(), cd2Element.getCDConstructorList()));
    this.deleletedConstructor = absentElementList(matchedConstructorList,
      cd1Element.getCDConstructorList());
    this.addedConstructor = absentElementList(matchedConstructorList,
      cd2Element.getCDConstructorList());
  }

  /**
   * Calculation of the diff size between the given classes, automaticly calculated on object
   * creation Name changes are weighted more and each member(attribute/methods/...) add at most one
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
    //Diff size of methods
    for (ElementDiff<ASTCDMethod> i : matchedMethodeList) {
      if (i.getCd1Element().isPresentCDThrowsDeclaration()) {
        size += i.getDiffSize() / 4.0;
      }
      else {
        size += i.getDiffSize() / 3.0;
      }
    }
    size += deleletedMethods.size() + addedMethode.size();

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

  private void createDefaultDiffList(ASTCDType1 cd1Element, ASTCDType2 cd2Element) {
    List<FieldDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();
    interpretation.append("Interpretation: ");

    // Modifier, non-optional
    if (!(pp.prettyprint(cd1Element.getModifier()).length() < 1
      && pp.prettyprint(cd2Element.getModifier()).length() < 1)){
      diffs.add(setModifier(cd1Element.getModifier(), cd2Element.getModifier()));
    }

    // Name, non-optional
    Optional<ASTCDType1> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDType2> cd2Name = Optional.of(cd2Element);
    FieldDiff<ASTCDType1, ASTCDType2> className = new FieldDiff<>(null, cd1Name, cd2Name);

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

    this.diffList = diffs;
  }

  private void createDiffList(ASTCDClass cd1Element, ASTCDInterface cd2Element) {
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
      diffList.add(classExtended);
      if (classExtended.getInterpretation().isPresent()) {
        interpretation.append("Extended")
            .append(": ")
            .append(classExtended.getInterpretation().get())
            .append(" ");
      }
    }
  }

  private void createDiffList(ASTCDInterface cd1Element, ASTCDInterface cd2Element) {
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
      diffList.add(interfaceExtended);
      if (interfaceExtended.getInterpretation().isPresent()) {
        interpretation.append("Extended")
            .append(": ")
            .append(interfaceExtended.getInterpretation().get())
            .append(" ");
      }
    }
  }

  private void createDiffList(ASTCDEnum cd1Element, ASTCDEnum cd2Element) {
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
      diffList.add(classInterfaceuse);
      if (classInterfaceuse.getInterpretation().isPresent()) {
        interpretation.append("Interface")
            .append(": ")
            .append(classInterfaceuse.getInterpretation().get())
            .append(" ");
      }
    }
  }

  private void createDiffList(ASTCDClass cd1Element, ASTCDClass cd2Element) {
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
      diffList.add(classExtended);
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
      diffList.add(classInterfaceuse);
      if (classInterfaceuse.getInterpretation().isPresent()) {
        interpretation.append("Interface")
            .append(": ")
            .append(classInterfaceuse.getInterpretation().get())
            .append(" ");
      }
    }
  }

  private void createDiffList(ASTCDInterface cd1Element, ASTCDClass cd2Element) {
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
      diffList.add(classExtended);
      if (classExtended.getInterpretation().isPresent()) {
        interpretation.append("Extended")
            .append(": ")
            .append(classExtended.getInterpretation().get())
            .append(" ");
      }
    }
  }

  /**
   * Help function to reduce code reusing. Create a modifier diff and sets print and interpretation strings.
   * @param cd1Modi Modifier of the first model
   * @param cd2Modi Modifier of the second model
   * @return FieldDiff of type ASTModifier
   */

  protected FieldDiff<ASTModifier, ASTModifier> setModifier (ASTModifier cd1Modi, ASTModifier cd2Modi){
    FieldDiff<ASTModifier, ASTModifier> modifier = new FieldDiff<>(Optional.of(cd1Modi), Optional.of(cd2Modi));

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

  /**
   * Adds all inherited attributes, which have no local copy (overwrite).
   * @param superClasses List of superclasses/interfaces for the current set of attributes
   * @param attributeList List of attributes from the element, which should be expanded
   * @return List of original attributes + inherited without conflicts
   */
  private List<ASTCDAttribute> addInheritedAttributes (Set<ASTCDType> superClasses, List<ASTCDAttribute> attributeList){
    for (ASTCDType x : superClasses){
      List<ASTCDAttribute> inheritedAttributes = x.getCDAttributeList();

      for (ASTCDAttribute inheritedAttribute : inheritedAttributes){
        String name = inheritedAttribute.getName();
        boolean found = false;
        for (ASTCDAttribute localAttribute : attributeList){
          if (localAttribute.getName().equals(name)){
            found = true;
            break;
          }
        }
        if (!found){
          attributeList.add(inheritedAttribute);
        }
      }
    }
    return attributeList;
  }

  /**
   * Adds all inherited methods, which have no local signature overwrites
   * @param superClasses List of superclasses/interfaces for the current set of methods
   * @param methodeList List of methods from the element, which should be expanded
   * @return List of original attributes + inherited without conflicts
   */
  //Todo: Add check for signature
  private List<ASTCDMethod> addInheritedMethods (Set<ASTCDType> superClasses, List<ASTCDMethod> methodeList){
    List<ASTCDMethod> output = new ArrayList<>(methodeList);
    for (ASTCDType x : superClasses){
      List<ASTCDMethod> inheritedMethods = x.getCDMethodList();

      for (ASTCDMethod inheritedMethod : inheritedMethods){
        String name = inheritedMethod.getName();
        boolean found = false;
        for (ASTCDMethod localMethod : output){
          if (localMethod.getName().equals(name)
            && !(inheritedMethod.getCDParameterList().size() == localMethod.getCDParameterList().size())){
            //Todo: Add check for signature when size is equal
            found = true;
            break;
          }
        }
        if (!found){
          output.add(inheritedMethod);
        }
      }
    }
    return output;
  }

  /**
   * Create Strings ready to be printed for both elements in the current diff
   */
  private void setStrings() {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    StringBuilder outputCD1 = new StringBuilder();
    StringBuilder outputCD2 = new StringBuilder();

    StringBuilder bodyCD1 = new StringBuilder();
    StringBuilder bodyCD2 = new StringBuilder();

    String signatureCD1 = combineWithoutNulls(
        Arrays.asList(ppModifier1, keywordCD1, ppName1, ppExtended1, ppInter1));
    String signatureCD2 = combineWithoutNulls(
        Arrays.asList(ppModifier2, keywordCD2, ppName2, ppExtended2, ppInter2));

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
      for (ASTCDEnumConstant x : addedEnumConstants) {
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

    for (ASTCDMethod x : deleletedMethods) {
      StringBuilder delMethods = new StringBuilder();
      String deletedMethode = pp.prettyprint((ASTCDBasisNode) x);
      if (deletedMethode.contains("\n")) {
        deletedMethode = deletedMethode.split("\n")[0];
      }
      delMethods.append(COLOR_DELETE).append(deletedMethode).append(RESET);
      matchDel.put(delMethods.toString(), Integer.valueOf(
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
   * Print function for the CDTypeDiff, used to output the diffs appropriately formated
   */
  public String printCD1() {
    return cd1Print;
  }

  public String printCD2() {
    return cd2Print;
  }

}
