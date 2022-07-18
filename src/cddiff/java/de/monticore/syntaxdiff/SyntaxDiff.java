package de.monticore.syntaxdiff;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDAssociationNode;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.ow2cw.CDInheritanceHelper;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import smetana.core.__array_of_double__;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main class of the syntax diff calculation, combines all diff types and provide multiple functions to access and print
 */
public class SyntaxDiff implements CDSyntaxDiff{

  List<ClassDiff> matchedClassList;
  List<ASTCDClass> deletedClasses;
  List<ASTCDClass> addedClasses;

  List<AssoDiff> matchedAssos;
  List<ASTCDAssociation> deletedAssos;
  List<ASTCDAssociation> addedAssos;

  List<InterfaceDiff> matchedInterfacesList;
  List<ASTCDInterface> deletedInterfaces;
  List<ASTCDInterface> addedInterfaces;

  public enum Op { CHANGE, ADD, DELETE}
  public enum Interpretation {REDUCED, REFINED, REVERSED, SUPERTYPE, SUBTYPE, RENAME, RELOCATION, REFINEMENT
    , EXPANSION, INCOMPARABLE, DEFAULTVALUECHANGED, ABSTRACTION, REPURPOSED, INHERITED, EQUALINTERVAL
    , SCOPECHANGE, ROLECHANGE, DEFAULTVALUE_ADDED, DELETED, EQUAL, TYPECHANGE, RESTRICTION, RESTRICT_INTERVAL
    , EXPAND_INTERVAL, EQUAL_INTERVAL}

  protected final String cd1Name, cd2Name, cd1Path, cd2Path;

  StringBuilder outPutAll;
  StringBuilder cd1Colored;
  StringBuilder cd2Colored;

  public List<ClassDiff> getMatchedClassList() {
    return matchedClassList;
  }
  public List<ASTCDClass> getAddedClasses() {
    return addedClasses;
  }

  public List<ASTCDClass> getDeletedClasses() {
    return deletedClasses;
  }

  public List<AssoDiff> getMatchedAssos() {
    return matchedAssos;
  }

  public List<ASTCDAssociation> getAddedAssos() {
    return addedAssos;
  }

  public List<ASTCDAssociation> getDeletedAssos() {
    return deletedAssos;
  }

  @Override
  public List<InterfaceDiff> getMatchedInterfaces() {
    return matchedInterfacesList;
  }

  @Override
  public List<ASTCDInterface> getAddedInterfaces() {
    return addedInterfaces;
  }

  @Override
  public List<ASTCDInterface> getDeletedInterfaces() {
    return deletedInterfaces;
  }

  @Override
  public List<ASTCDEnum> getAddedEnums() {
    return null;
  }

  @Override
  public List<ASTCDEnum> getDeletedEnums() {
    return null;
  }

  public String getCd1Name() {
    return cd1Name;
  }

  public String getCd2Name() {
    return cd2Name;
  }

  public String getCd1Path() {
    return cd1Path;
  }

  public String getCd2Path() {
    return cd2Path;
  }

  // Todo: Add json file creator
  public SyntaxDiff(ASTCDCompilationUnit cd1, ASTCDCompilationUnit cd2){
    /*
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd1);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd2);

    ICD4CodeArtifactScope scopeCD1 = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd1);
    ICD4CodeArtifactScope scopeCD2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd2);
   */


    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    // Trafo to make in-class declarations of compositions appear in the association list
    new CD4CodeDirectCompositionTrafo().transform(cd1);
    new CD4CodeDirectCompositionTrafo().transform(cd2);
    final String BOLD_RED = "\033[1;31m";
    final String BOLD_GREEN = "\033[1;32m";
    final String RESET = "\033[0m";

    cd1Name = cd1.getCDDefinition().getName();
    cd2Name = cd2.getCDDefinition().getName();
    cd1Path = "Path CD1"; //Todo: Wo bekomme ich den Pfad her?
    cd2Path = "Path CD2";

    // Create Lists for each type of CDElement to check

    // Classes
    List<ASTCDClass> cd1ClassesList = cd1.getCDDefinition().getCDClassesList();
    List<ASTCDClass> cd2ClassesList = cd2.getCDDefinition().getCDClassesList();

    // Interfaces
    List<ASTCDInterface> cd1InterfacesList = cd1.getCDDefinition().getCDInterfacesList();
    List<ASTCDInterface> cd2InterfacesList = cd2.getCDDefinition().getCDInterfacesList();

    // Associations
    List<ASTCDAssociation> cd1AssociationsList = cd1.getCDDefinition().getCDAssociationsList();
    List<ASTCDAssociation> cd2AssociationsList = cd2.getCDDefinition().getCDAssociationsList();

    // Create Class Match

    this.matchedClassList = getClassMatchingList(getClassDiffList(cd1ClassesList, cd2ClassesList));
    this.deletedClasses = absentClassList(getMatchedClassList(), cd1ClassesList);
    this.addedClasses = absentClassList(getMatchedClassList(), cd2ClassesList);

    // Create Interface Match

    this.matchedInterfacesList = getInterfaceMatchingList(getInterfacesDiffList(cd1InterfacesList, cd2InterfacesList));
    this.deletedInterfaces = absentInterfaceList(getMatchedInterfaces(), cd1InterfacesList);
    this.addedInterfaces = absentInterfaceList(getMatchedInterfaces(), cd2InterfacesList);

    StringBuilder initial = new StringBuilder();
    StringBuilder classPrints = new StringBuilder();
    StringBuilder interfacePrints = new StringBuilder();
    StringBuilder assoPrints = new StringBuilder();

    //Map<String, Integer> outputGathering = new HashMap();

    //System.out.println("Scope: ");
    //System.out.println(cd1.getEnclosingScope().getRealPackageName());

    initial.append(System.lineSeparator())
      .append("In the following the syntax diff between ")
      .append(cd1.getCDDefinition().getName())
      .append(" and ")
      .append(cd2.getCDDefinition().getName())
      .append(" is created")
      .append(System.lineSeparator())
      .append(System.lineSeparator());


    for (ClassDiff x : matchedClassList) {
      StringBuilder tmp = new StringBuilder();
      tmp.append("CD1 (").append(cd1.getCDDefinition().getName()).append(") Line: ")
        .append(x.getCd1Element().get_SourcePositionStart().getLine()).append("-").append(x.getCd1Element().get_SourcePositionEnd().getLine())
        .append(System.lineSeparator())
        .append(x.printCD1()).append(System.lineSeparator())
        .append("CD2 (").append(cd2.getCDDefinition().getName()).append(") Line: ")
        .append(x.getCd2Element().get_SourcePositionStart().getLine()).append("-").append(x.getCd2Element().get_SourcePositionEnd().getLine())
        .append(System.lineSeparator())
        .append(x.printCD2()).append(System.lineSeparator())
        .append(x.getInterpretation())
        .append(System.lineSeparator()).append(System.lineSeparator());
      classPrints.append(tmp);
      //outputGathering.put(tmp.toString(),x.getBreakingChange());
    }

    for (InterfaceDiff x : matchedInterfacesList) {
      StringBuilder tmp = new StringBuilder();
      tmp.append("CD1 (").append(cd1.getCDDefinition().getName()).append(") Line: ")
        .append(x.getCd1Element().get_SourcePositionStart().getLine()).append("-").append(x.getCd1Element().get_SourcePositionEnd().getLine())
        .append(System.lineSeparator())
        .append(x.printCD1()).append(System.lineSeparator())
        .append("CD2 (").append(cd2.getCDDefinition().getName()).append(") Line: ")
        .append(x.getCd2Element().get_SourcePositionStart().getLine()).append("-").append(x.getCd2Element().get_SourcePositionEnd().getLine())
        .append(System.lineSeparator())
        .append(x.printCD2()).append(System.lineSeparator())
        .append(x.getInterpretation())
        .append(System.lineSeparator()).append(System.lineSeparator());
      interfacePrints.append(tmp);
      //outputGathering.put(tmp.toString(),x.getBreakingChange());
    }

    // Create Association Match
    this.matchedAssos = getAssoMatchingList(getAssoDiffList(cd1AssociationsList, cd2AssociationsList));
    this.deletedAssos = absentAssoList(matchedAssos, cd1AssociationsList);
    this.addedAssos = absentAssoList(matchedAssos, cd2AssociationsList);


    for (AssoDiff x : matchedAssos) {
      StringBuilder tmp = new StringBuilder();
      tmp.append("CD1 (").append(cd1.getCDDefinition().getName()).append(") and CD2 (")
        .append(cd2.getCDDefinition().getName()).append(")").append(System.lineSeparator())
        .append("CD1: ")
        .append(x.getCd1Element().get_SourcePositionStart().getLine())
        .append("  ").append(x.printCD1()).append(System.lineSeparator())
        .append("CD2: ")
        .append(x.getCd2Element().get_SourcePositionStart().getLine())
        .append("  ")
        .append(x.printCD2()).append(System.lineSeparator())
        .append(x.getInterpretation())
        .append(System.lineSeparator()).append(System.lineSeparator());
      assoPrints.append(tmp);
      //outputGathering.put(tmp.toString(),x.getBreakingChange());
    }


    if (!deletedClasses.isEmpty()){
      classPrints.append("Line Deleted Classes from CD1 (")
        .append(cd1.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());

      for (ASTCDClass a : deletedClasses) {
        classPrints.append("CD1: ").append(a.get_SourcePositionStart().getLine())
          .append("   ")
          .append(BOLD_RED)
          .append(pp.prettyprint(a))
          .append(RESET);
      }
    }

    if (!addedClasses.isEmpty()){
      classPrints.append("Line Added Classes to CD2 (")
        .append(cd2.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());

      for (ASTCDClass a : addedClasses) {
        classPrints.append("CD2: ").append(a.get_SourcePositionStart().getLine())
          .append("   ")
          .append(BOLD_GREEN)
          .append(pp.prettyprint(a))
          .append(RESET);
      }
    }
    if (!deletedAssos.isEmpty()){
      assoPrints.append("Line Deleted Associations from CD1 (")
        .append(cd1.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());

      for (ASTCDAssociationNode asso : deletedAssos){
        assoPrints.append("CD1: ").append(asso.get_SourcePositionStart().getLine())
          .append("   ")
          .append(BOLD_RED)
          .append(pp.prettyprint(asso))
          .append(RESET);
      }
    }

    if (!addedAssos.isEmpty()){
      assoPrints.append("Line Added Associations to CD2 (")
        .append(cd2.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());

      for (ASTCDAssociationNode asso : addedAssos){
        assoPrints.append("CD2: ").append(asso.get_SourcePositionStart().getLine())
          .append("   ")
          .append(BOLD_GREEN)
          .append(pp.prettyprint(asso))
          .append(RESET);
      }
    }

    if (!deletedInterfaces.isEmpty()){
      interfacePrints.append("Line Deleted Interface from CD1 (")
        .append(cd1.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());

      for (ASTCDInterface a : deletedInterfaces) {
        interfacePrints.append("CD1: ").append(a.get_SourcePositionStart().getLine())
          .append("   ")
          .append(BOLD_RED)
          .append(pp.prettyprint((ASTCDBasisNode) a))
          .append(RESET);
      }
    }

    if (!addedInterfaces.isEmpty()){
      interfacePrints.append("CD2: ").append("Line Added Interface to CD2 (")
        .append(cd2.getCDDefinition().getName())
        .append(") :")
        .append(System.lineSeparator());

      for (ASTCDInterface a : addedInterfaces) {
        interfacePrints.append(a.get_SourcePositionStart().getLine())
          .append("   ")
          .append(BOLD_GREEN)
          .append(pp.prettyprint((ASTCDBasisNode) a))
          .append(RESET);
      }
    }
    StringBuilder outPutAll = new StringBuilder();
    outPutAll.append(initial);

    /*
    Map<Integer, String> matchAndDelete = outputGathering.entrySet().stream()
      .sorted(Map.Entry.comparingByValue())
      .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1, LinkedHashMap::new));
    matchAndDelete.forEach((k, v) -> outPutAll.append(v));
*/

    outPutAll.append(classPrints)
      .append(interfacePrints)
      .append(assoPrints);

    this.outPutAll = outPutAll;
  }

  public void print(){
    System.out.println(outPutAll);
  }
  public void printCD1(){
    System.out.println(cd1Colored);
  }
  public void printCD2(){
    System.out.println(cd2Colored);
  }

  /**
   * Returns a list of association diffs between all associations from the two associations provided as input
   * @param cd1AssoList List of associations in the original Model
   * @param cd2AssoList List of associations in the target(new) Model
   * @return Returns a list for each association, ordered by diffsize (small diff values == similar)
   */
  public static List<List<AssoDiff>> getAssoDiffList(List<ASTCDAssociation> cd1AssoList, List<ASTCDAssociation> cd2AssoList) {
    List<List<AssoDiff>> assoMatches = new ArrayList<>();

    for (ASTCDAssociation cd1Asso : cd1AssoList) {

      // Create a new list for each Association
      List<AssoDiff> cd1AssoMatches = new ArrayList<>();
      for (ASTCDAssociation cd2Asso : cd2AssoList) {
        // Diff list for the compared assos
        cd1AssoMatches.add(new AssoDiff(cd1Asso, cd2Asso));
      }
      // Sort by size of diffs, ascending
      cd1AssoMatches.sort(Comparator.comparing(AssoDiff::getDiffSize));

      assoMatches.add(cd1AssoMatches);
    }
    return assoMatches;
  }

  /**
   * Returns a list of class diffs between all classes from the two classes provided as input
   * @param cd1ClassList List of classes in the original Model
   * @param cd2ClassList List of classes in the target(new) Model
   * @return Returns a list for each class, ordered by diffsize (small diff values == similar)
   */
  public static List<List<ClassDiff>> getClassDiffList(List<ASTCDClass> cd1ClassList, List<ASTCDClass> cd2ClassList) {
    List<List<ClassDiff>> classMatches = new ArrayList<>();

    for (ASTCDClass cd1Class : cd1ClassList) {

      // Create a new list for each class
      List<ClassDiff> cd1ClassDiffs = new ArrayList<>();
      for (ASTCDClass cd2Class : cd2ClassList) {
        // Diff list for the compared classes
        cd1ClassDiffs.add(new ClassDiff(cd1Class, cd2Class));
      }
      // Sort by size of diffs, ascending
      cd1ClassDiffs.sort(Comparator.comparing(ClassDiff::getDiffSize));

      classMatches.add(cd1ClassDiffs);
    }
    return classMatches;
  }
  protected static List<ClassDiff> getClassMatchingList(List<List<ClassDiff>> elementsDiffList){
    List<ASTCDClass> cd1matchedElements = new ArrayList<>();
    List<ASTCDClass> cd2matchedElements = new ArrayList<>();
    List<ClassDiff> matchedElements = new ArrayList<>();

    for (List<ClassDiff> currentElementList: elementsDiffList){
      double threshold = 0;
      OptionalDouble optAverage = currentElementList.stream()
        .mapToDouble(ClassDiff::getDiffSize)
        .average();
      if (optAverage.isPresent()) {
        threshold = optAverage.getAsDouble() / 2;
      }
      if (!currentElementList.isEmpty()) {
        ClassDiff currentElementDiff = currentElementList.get(0);
        ASTCDClass cd1Element = currentElementDiff.getCd1Element();
        ASTCDClass cd2Element = currentElementDiff.getCd2Element();
        if (!cd1matchedElements.contains(cd1Element) && !cd2matchedElements.contains(cd2Element)){
          // Todo: Check if there is a match to the target attribute with a smaller diff size
          if (currentElementDiff.getDiffSize() <= threshold){
            matchedElements.add(currentElementDiff);
            cd1matchedElements.add(cd1Element);
            cd2matchedElements.add(cd2Element);
          }
        }
      }
    }
    return matchedElements;
  }

  public static List<List<InterfaceDiff>> getInterfacesDiffList(List<ASTCDInterface> cd1List, List<ASTCDInterface> cd2List) {
    List<List<InterfaceDiff>> matches = new ArrayList<>();

    for (ASTCDInterface cd1Class : cd1List) {

      // Create a new list for each class
      List<InterfaceDiff> cd1diffs = new ArrayList<>();
      for (ASTCDInterface cd2Element : cd2List) {
        // Diff list for the compared classes
        cd1diffs.add(new InterfaceDiff(cd1Class, cd2Element));
      }
      // Sort by size of diffs, ascending
      cd1diffs.sort(Comparator.comparing(InterfaceDiff::getDiffSize));

      matches.add(cd1diffs);
    }
    return matches;
  }
  protected static List<InterfaceDiff> getInterfaceMatchingList(List<List<InterfaceDiff>> elementsDiffList){
    List<ASTCDInterface> cd1matchedElements = new ArrayList<>();
    List<ASTCDInterface> cd2matchedElements = new ArrayList<>();
    List<InterfaceDiff> matchedElements = new ArrayList<>();

    for (List<InterfaceDiff> currentElementList: elementsDiffList){
      double threshold = 0;
      OptionalDouble optAverage = currentElementList.stream()
        .mapToDouble(InterfaceDiff::getDiffSize)
        .average();
      if (optAverage.isPresent()) {
        threshold = optAverage.getAsDouble() / 2;
      }
      if (!currentElementList.isEmpty()) {
        InterfaceDiff currentElementDiff = currentElementList.get(0);
        ASTCDInterface cd1Element = currentElementDiff.getCd1Element();
        ASTCDInterface cd2Element = currentElementDiff.getCd2Element();
        if (!cd1matchedElements.contains(cd1Element) && !cd2matchedElements.contains(cd2Element)){
          // Todo: Check if there is a match to the target attribute with a smaller diff size
          if (currentElementDiff.getDiffSize() <= threshold){
            matchedElements.add(currentElementDiff);
            cd1matchedElements.add(cd1Element);
            cd2matchedElements.add(cd2Element);
          }
        }
      }
    }
    return matchedElements;
  }
  protected static List<AssoDiff> getAssoMatchingList(List<List<AssoDiff>> elementsDiffList){
    List<ASTCDAssociation> cd1matchedElements = new ArrayList<>();
    List<ASTCDAssociation> cd2matchedElements = new ArrayList<>();
    List<AssoDiff> matchedElements = new ArrayList<>();

    for (List<AssoDiff> currentElementList: elementsDiffList){
      double threshold = 0;
      OptionalDouble optAverage = currentElementList.stream()
        .mapToDouble(AssoDiff::getDiffSize)
        .average();
      if (optAverage.isPresent()) {
        threshold = (1 / (double) (currentElementList.size()+1))+optAverage.getAsDouble() / 1.5;
      }
      if (!currentElementList.isEmpty()) {
        for (AssoDiff currentElementDiff : currentElementList){
          ASTCDAssociation cd1Element = currentElementDiff.getCd1Element();
          ASTCDAssociation cd2Element = currentElementDiff.getCd2Element();
          if (!cd1matchedElements.contains(cd1Element) && !cd2matchedElements.contains(cd2Element)){
            // Todo: Check if there is a match to the target attribute with a smaller diff size
            if (currentElementDiff.getDiffSize() <= threshold){
              matchedElements.add(currentElementDiff);
              cd1matchedElements.add(cd1Element);
              cd2matchedElements.add(cd2Element);
              break;
            }
          }
        }
      }
    }
    return matchedElements;
  }
  protected static List<ASTCDClass> absentClassList(List<ClassDiff> matchs, List<ASTCDClass> elementList){
    List<ASTCDClass> output = new ArrayList<>();
    for (ASTCDClass element: elementList){
      boolean found = false;
      for (ClassDiff diff : matchs){
        if (diff.getCd1Element().deepEquals(element) || diff.getCd2Element().deepEquals(element)){
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
  protected static List<ASTCDAssociation> absentAssoList(List<AssoDiff> matchs, List<ASTCDAssociation> elementList){
    List<ASTCDAssociation> output = new ArrayList<>();
    for (ASTCDAssociation element: elementList){
      boolean found = false;
      for (AssoDiff diff : matchs){
        if (diff.getCd1Element().deepEquals(element) || diff.getCd2Element().deepEquals(element)){
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
  protected static List<ASTCDInterface> absentInterfaceList(List<InterfaceDiff> matchs, List<ASTCDInterface> elementList){
    List<ASTCDInterface> output = new ArrayList<>();
    for (ASTCDInterface element: elementList){
      boolean found = false;
      for (InterfaceDiff diff : matchs){
        if (diff.getCd1Element().deepEquals(element) || diff.getCd2Element().deepEquals(element)){
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

}

