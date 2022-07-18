package de.monticore.syntaxdiff;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDExtendUsage;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.syntaxdiff.FieldDiff;
import de.monticore.syntaxdiff.SyntaxDiff;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Map.Entry;


/**
 * Diff Type for Classes
 * Use the constructor to create a diff between two classes
 * This diff type contains information extracted from the provided classes
 */
public class ClassDiff extends AbstractDiffType{
  CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
  private String ppModifier1, ppName1, ppExtended1,ppInter1
    , ppModifier2, ppName2, ppExtended2, ppInter2, cd1Print, cd2Print;

  protected final ASTCDClass cd1Element;

  protected final ASTCDClass cd2Element;

  protected List<ElementDiff<ASTCDAttribute>> matchedAttributesList;
  protected List<ASTCDAttribute> deleletedAttributes;
  protected List<ASTCDAttribute> addedAttributes;

  protected List<ElementDiff<ASTCDMethod>> matchedMethodeList;
  protected List<ASTCDMethod> deleletedMethodes;
  protected List<ASTCDMethod> addedMethode;

  protected List<ElementDiff<ASTCDConstructor>> matchedConstructorList;
  protected List<ASTCDConstructor> deleletedConstructor;
  protected List<ASTCDConstructor> addedConstructor;

  public List<ElementDiff<ASTCDAttribute>> getMatchedAttributesList() {
    return matchedAttributesList;
  }

  public List<ASTCDAttribute> getDeleletedAttributes() {
    return deleletedAttributes;
  }

  public List<ASTCDAttribute> getAddedAttributes() {
    return addedAttributes;
  }

  public ASTCDClass getCd1Element() {
    return cd1Element;
  }

  public ASTCDClass getCd2Element() {
    return cd2Element;
  }


  /**
   * Constructor of the class diff type
   * @param cd1Element Class from the original model
   * @param cd2Element Class from the target(new) model
   */
  public ClassDiff(ASTCDClass cd1Element, ASTCDClass cd2Element) {
    this.cd1Element = cd1Element;
    this.cd2Element = cd2Element;

    // Set the required parts for diff size calculation
    classDiff(cd1Element, cd2Element);

    this.diffSize = calculateDiffSize();

    setStrings();
  }
  /**
   * Calculation of the diff size between the given classes, automaticly calculated on object creation
   * Name changes are weighted more and each member(attribute/methodes/...) add at most one to the size
   * @return Diff size as double
   */
  private double calculateDiffSize(){
    //Diff size of signature
    double size = diffList.size();

    // Diff size of attributes (amounts to max ~ 1)
    for (ElementDiff<ASTCDAttribute> i : matchedAttributesList){
      if (i.getCd1Element().isPresentInitial()){
        size += i.getDiffSize()/4.0;
      }else {
        size += i.getDiffSize()/3.0;
      }
    }
    size += deleletedAttributes.size() + addedAttributes.size();

    //Todo: Add Parameter Diff to size calculation
    //Diff size of methodes
    for (ElementDiff<ASTCDMethod> i : matchedMethodeList){
      if (i.getCd1Element().isPresentCDThrowsDeclaration()){
        size += i.getDiffSize()/4.0;
      }else {
        size += i.getDiffSize()/3.0;
      }
    }
    size += deleletedMethodes.size() + addedMethode.size();

    // Diff size of constructors
    for (ElementDiff<ASTCDConstructor> i : matchedConstructorList){
      if (i.getCd1Element().isPresentCDThrowsDeclaration()){
        size += i.getDiffSize()/4.0;
      }else {
        size += i.getDiffSize()/3.0;
      }
    }
    size += deleletedConstructor.size() + addedConstructor.size();
    size += addWeightToDiffSize(diffList);


    return size;
  }

  /**
   * Main method of this class, calculates the differences between both classes using checks between every field
   * and Members contained in the class like attributes and methods
   * @param cd1Class Class from the original model
   * @param cd2Class Class from the target(new) model
   */
  private void classDiff(ASTCDClass cd1Class, ASTCDClass cd2Class) {
    List<FieldDiff<? extends ASTNode>> diffs = new ArrayList<>();
    interpretation.append("Interpretation: ");

    // Modifier, non-optional
    Optional<ASTModifier> cd1Modi = Optional.of(cd1Element.getModifier());
    Optional<ASTModifier> cd2Modi = Optional.of(cd2Element.getModifier());
    FieldDiff<ASTModifier> attributeModifier = new FieldDiff<>(cd1Modi, cd2Modi);

    ppModifier1 = getColorCode(attributeModifier) + pp.prettyprint(cd1Modi.get()) + RESET;
    ppModifier2 = getColorCode(attributeModifier) + pp.prettyprint(cd2Modi.get()) + RESET;

    if (attributeModifier.isPresent()){
      diffs.add(attributeModifier);
      if (attributeModifier.getInterpretation().isPresent()){
        interpretation.append("Class modifier").append(": ").append(attributeModifier.getInterpretation().get()).append(" ");
      }
    }

    // Name, non-optional
    Optional<ASTCDClass> cd1Name = Optional.of(cd1Element);
    Optional<ASTCDClass> cd2Name = Optional.of(cd2Element);
    FieldDiff<ASTCDClass> className = new FieldDiff<>(null, cd1Name, cd2Name);

    if (!cd1Name.get().getName().equals(cd2Name.get().getName())){
      className = new FieldDiff<>(SyntaxDiff.Op.CHANGE, cd1Name, cd2Name);
    }

    ppName1 = getColorCode(className) + cd1Name.get().getName() + RESET;
    ppName2 = getColorCode(className) + cd2Name.get().getName() + RESET;

    if (className.isPresent()){
      diffs.add(className);
      if (className.getInterpretation().isPresent()){
        interpretation.append("Classname").append(": ").append(className.getInterpretation().get()).append(" ");
      }
    }



    // Extended, optional
    Optional<ASTCDExtendUsage> cd1Extend = (cd1Class.isPresentCDExtendUsage()) ? Optional.of(cd1Class.getCDExtendUsage()) : Optional.empty();
    Optional<ASTCDExtendUsage> cd2Extend = (cd2Class.isPresentCDExtendUsage()) ? Optional.of(cd2Class.getCDExtendUsage()) : Optional.empty();
    FieldDiff<ASTCDExtendUsage> classExtended = new FieldDiff<>(cd1Extend, cd2Extend);

    cd1Extend.ifPresent(initial -> ppExtended1 = getColorCode(classExtended) + pp.prettyprint(initial)+ RESET);
    cd2Extend.ifPresent(initial -> ppExtended2 = getColorCode(classExtended) + pp.prettyprint(initial)+ RESET);

    if (classExtended.isPresent()){
      diffs.add(classExtended);
      if (classExtended.getInterpretation().isPresent()){
        interpretation.append("Extended").append(": ").append(classExtended.getInterpretation().get()).append(" ");
      }
    }

    // Implements, optional
    Optional<ASTMCObjectType> cd1Imple = (cd1Class.isPresentCDInterfaceUsage()) ? Optional.of(cd1Class.getInterfaceList().get(0)) : Optional.empty();
    Optional<ASTMCObjectType> cd2Imple = (cd2Class.isPresentCDInterfaceUsage()) ? Optional.of(cd2Class.getInterfaceList().get(0)) : Optional.empty();
    FieldDiff<ASTMCObjectType> classInterfaceuse = new FieldDiff<>(cd1Imple, cd2Imple);

    cd1Imple.ifPresent(inter -> ppInter1 = getColorCode(classInterfaceuse) + pp.prettyprint(inter)+ RESET);
    cd2Imple.ifPresent(inter -> ppInter2 = getColorCode(classInterfaceuse) + pp.prettyprint(inter)+ RESET);

    if (classInterfaceuse.isPresent()){
      diffs.add(classInterfaceuse);
      if (classInterfaceuse.getInterpretation().isPresent()){
        interpretation.append("Interface").append(": ").append(classInterfaceuse.getInterpretation().get()).append(" ");
      }
    }

    // Todo: add inherited methods and attributes

    // CDMember diffs, members are: Attributes, Methods, Constructors

    // Set the difflist to signature diffs(if any)
    this.diffList = diffs;

    // Create trivial matches for attributes/constructors/methods
    this.matchedAttributesList = getMatchingList(getElementDiffList(cd1Class.getCDAttributeList(), cd2Class.getCDAttributeList()));
    this.deleletedAttributes = absentElementList(matchedAttributesList, cd1Class.getCDAttributeList());
    this.addedAttributes = absentElementList(matchedAttributesList, cd2Class.getCDAttributeList());

    this.matchedMethodeList = getMatchingList(getElementDiffList(cd1Class.getCDMethodList(), cd2Class.getCDMethodList()));
    this.deleletedMethodes = absentElementList(matchedMethodeList, cd1Class.getCDMethodList());
    this.addedMethode= absentElementList(matchedMethodeList, cd2Class.getCDMethodList());

    this.matchedConstructorList = getMatchingList(getElementDiffList(cd1Class.getCDConstructorList(), cd2Class.getCDConstructorList()));
    this.deleletedConstructor = absentElementList(matchedConstructorList, cd1Class.getCDConstructorList());
    this.addedConstructor = absentElementList(matchedConstructorList, cd2Class.getCDConstructorList());

  }

  private void setStrings() {
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    StringBuilder outputCD1 = new StringBuilder();
    StringBuilder outputCD2 = new StringBuilder();


    StringBuilder bodyCD1 = new StringBuilder();
    StringBuilder bodyCD2 = new StringBuilder();

    String signatureCD1 = combineWithoutNulls(Arrays.asList(ppModifier1, RESET + "class", ppName1, ppExtended1, ppInter1));
    String signatureCD2 = combineWithoutNulls(Arrays.asList(ppModifier2, RESET + "class", ppName2, ppExtended2, ppInter2));

    Map<String, Integer> matchDel = new HashMap();
    Map<String, Integer> add = new HashMap();

    for (ElementDiff<ASTCDAttribute> x : matchedAttributesList) {
      matchDel.put(x.printCD1Element(), x.getCd1Element().get_SourcePositionStart().getLine());
    }

    for (ElementDiff<ASTCDMethod> x : matchedMethodeList) {
      matchDel.put(x.printCD1Element(), x.getCd1Element().get_SourcePositionStart().getLine());
    }
    for (ElementDiff<ASTCDAttribute> x : matchedAttributesList) {
      add.put(x.printCD2Element(), x.getCd2Element().get_SourcePositionStart().getLine());
    }

    for (ElementDiff<ASTCDMethod> x : matchedMethodeList) {
      add.put(x.printCD2Element(), x.getCd2Element().get_SourcePositionStart().getLine());
    }

    for (ASTCDMethod x : deleletedMethodes) {
      StringBuilder delMethodes = new StringBuilder();
      String deletedMethode = pp.prettyprint((ASTCDBasisNode) x);
      if (deletedMethode.contains("\n")) {
        deletedMethode = deletedMethode.split("\n")[0];
      }
      delMethodes.append(COLOR_DELETE)
        .append(deletedMethode)
        .append(RESET);
      matchDel.put(delMethodes.toString(), x.get_SourcePositionStart().getLine());
    }
    for (ASTCDAttribute x : deleletedAttributes) {
      StringBuilder delAttri = new StringBuilder();
      String deletedAttribute = pp.prettyprint(x);
      if (deletedAttribute.contains("\n")) {
        deletedAttribute = deletedAttribute.split("\n")[0];
      }
      delAttri.append(COLOR_DELETE)
        .append(deletedAttribute)
        .append(RESET);
      matchDel.put(delAttri.toString(), x.get_SourcePositionStart().getLine());
    }


    for (ASTCDMethod x : addedMethode) {
      StringBuilder addMeth = new StringBuilder();
      String addedMethode = pp.prettyprint((ASTCDBasisNode) x);
      if (addedMethode.contains("\n")) {
        addedMethode = addedMethode.split("\n")[0];
      }
      addMeth.append(COLOR_ADD)
        .append(addedMethode)
        .append(RESET);
      add.put(addMeth.toString(), x.get_SourcePositionStart().getLine());
    }
    for (ASTCDAttribute x : addedAttributes) {
      StringBuilder addAttri = new StringBuilder();
      String addedAttribute = pp.prettyprint(x);
      if (addedAttribute.contains("\n")) {
        addedAttribute = addedAttribute.split("\n")[0];
      }
      addAttri.append(COLOR_ADD)
        .append(addedAttribute)
        .append(RESET);
      add.put(addAttri.toString(), x.get_SourcePositionStart().getLine());
    }


    Map<Integer, String> matchAndDelete = matchDel.entrySet().stream()
      .sorted(Entry.comparingByValue())
      .collect(Collectors.toMap(Entry::getValue, Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    matchAndDelete.forEach((k, v) -> bodyCD1.append("     ").append(v).append(System.lineSeparator()));

    Map<Integer, String> matchAndAdd = add.entrySet().stream()
      .sorted(Entry.comparingByValue())
      .collect(Collectors.toMap(Entry::getValue, Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    matchAndAdd.forEach((k, v) -> bodyCD2.append("     ").append(v).append(System.lineSeparator()));


    outputCD1.append(signatureCD1);
    if (bodyCD1.toString().length() > 0) {
      outputCD1.append("{ ")
        .append(System.lineSeparator())
        .append(bodyCD1)
        .append("}");
    } else {
      outputCD1.append(";");
    }

    cd1Print = outputCD1.toString();

    outputCD2.append(signatureCD2);
    if (bodyCD2.toString().length() > 0) {
      outputCD2.append("{ ")
        .append(System.lineSeparator())
        .append(bodyCD2)
        .append("}");
    } else {
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
