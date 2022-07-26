package de.monticore.cddiff.syntaxdiff;

import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.cddiff.alloycddiff.AlloyCDDiff;
import de.monticore.ast.ASTNode;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDAssociationNode;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterfaceAndEnumNode;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Paths;
import java.util.*;

/**
 * Main class of the syntax diff calculation, combines all diff types and provide multiple functions
 * to access and print
 */
public class SyntaxDiff implements CDSyntaxDiff {

  protected final String outputPath = "target/git-diff";

  protected List<CDTypeDiff<ASTCDClass, ASTCDClass>> matchedClassList;

  protected List<ASTCDClass> deletedClasses;

  protected List<ASTCDClass> addedClasses;

  protected List<CDTypeDiff<ASTCDInterface, ASTCDInterface>> matchedInterfacesList;

  protected List<ASTCDInterface> deletedInterfaces;

  protected List<ASTCDInterface> addedInterfaces;

  protected List<CDTypeDiff<ASTCDEnum, ASTCDEnum>> matchedEnumList;

  protected List<ASTCDEnum> deletedEnum;

  protected List<ASTCDEnum> addedEnum;

  protected List<CDAssociationDiff> matchedAssos;

  protected List<ASTCDAssociation> deletedAssos;

  protected List<ASTCDAssociation> addedAssos;

  protected List<CDTypeDiff<ASTCDInterface, ASTCDClass>> matchedInterfaceClassList;

  protected List<CDTypeDiff<ASTCDClass, ASTCDInterface>> matchedClassInterfaceList;

  public enum Op {CHANGE, ADD, DELETE}

  public enum Interpretation {
    REDUCED, REFINED, REVERSED, SUPERTYPE, SUBTYPE, RENAME, RELOCATION, REFINEMENT, EXPANSION,
    INCOMPARABLE, DEFAULTVALUECHANGED, ABSTRACTION, REPURPOSED, INHERITED, EQUALINTERVAL,
    SCOPECHANGE, ROLECHANGE, DEFAULTVALUE_ADDED, DELETED, EQUAL, TYPECHANGE, RESTRICTION,
    RESTRICT_INTERVAL, EXPAND_INTERVAL, EQUAL_INTERVAL
  }

  protected StringBuilder outPutAll;

  protected StringBuilder cd1Colored;

  protected StringBuilder cd2Colored;

  public List<CDTypeDiff<ASTCDClass, ASTCDClass>> getMatchedClassList() {
    return matchedClassList;
  }

  public List<ASTCDClass> getAddedClasses() {
    return addedClasses;
  }

  public List<CDTypeDiff<ASTCDClass, ASTCDInterface>> getMatchedClassInterfaceList() {
    return matchedClassInterfaceList;
  }

  public List<CDTypeDiff<ASTCDInterface, ASTCDClass>> getMatchedInterfaceClassList() {
    return matchedInterfaceClassList;
  }

  public List<ASTCDEnum> getAddedEnums() {
    return addedEnum;
  }

  public List<ASTCDEnum> getDeletedEnums() {
    return deletedEnum;
  }

  public List<ASTCDClass> getDeletedClasses() {
    return deletedClasses;
  }

  public List<CDAssociationDiff> getMatchedAssos() {
    return matchedAssos;
  }

  public List<ASTCDAssociation> getAddedAssos() {
    return addedAssos;
  }

  public List<ASTCDAssociation> getDeletedAssos() {
    return deletedAssos;
  }

  public List<CDTypeDiff<ASTCDInterface, ASTCDInterface>> getMatchedInterfaces() {
    return matchedInterfacesList;
  }

  public List<ASTCDInterface> getAddedInterfaces() {
    return addedInterfaces;
  }

  public List<ASTCDInterface> getDeletedInterfaces() {
    return deletedInterfaces;
  }

  public List<CDTypeDiff<ASTCDEnum, ASTCDEnum>> getMatchedEnumList() {
    return matchedEnumList;
  }

  // Todo: Add json file creator
  public SyntaxDiff(ASTCDCompilationUnit cd1, ASTCDCompilationUnit cd2) {

    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    // Trafo to make in-class declarations of compositions appear in the association list
    new CD4CodeDirectCompositionTrafo().transform(cd1);
    new CD4CodeDirectCompositionTrafo().transform(cd2);
    final String BOLD_RED = "\033[1;31m";
    final String BOLD_GREEN = "\033[1;32m";
    final String RESET = "\033[0m";

    computeSemDiff(cd2.deepClone(), cd1.deepClone());

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

    // Enum
    List<ASTCDEnum> cd1EnumList = cd1.getCDDefinition().getCDEnumsList();
    List<ASTCDEnum> cd2EnumList = cd2.getCDDefinition().getCDEnumsList();

    // Create Class Match

    this.matchedClassList = getMatchingList(getDiffList(cd1ClassesList, cd2ClassesList));
    this.deletedClasses = absentElementList(getMatchedClassList(), cd1ClassesList);
    this.addedClasses = absentElementList(getMatchedClassList(), cd2ClassesList);

    // Create Interface Match

    this.matchedInterfacesList = getMatchingList(getDiffList(cd1InterfacesList, cd2InterfacesList));
    this.deletedInterfaces = absentElementList(getMatchedInterfaces(), cd1InterfacesList);
    this.addedInterfaces = absentElementList(getMatchedInterfaces(), cd2InterfacesList);

    // Create Enum Match

    this.matchedEnumList = getMatchingList(getDiffList(cd1EnumList, cd2EnumList));
    this.deletedEnum = absentElementList(getMatchedEnumList(), cd1EnumList);
    this.addedEnum = absentElementList(getMatchedEnumList(), cd2EnumList);

    // Create Association Match
    this.matchedAssos = getAssoMatchingList(
        getAssoDiffList(cd1AssociationsList, cd2AssociationsList));
    this.deletedAssos = absentAssoList(matchedAssos, cd1AssociationsList);
    this.addedAssos = absentAssoList(matchedAssos, cd2AssociationsList);

    // Interface to Class

    this.matchedInterfaceClassList = getMatchingList(getDiffList(deletedInterfaces, addedClasses));
    this.deletedInterfaces = absentElementList(matchedInterfaceClassList, deletedInterfaces);
    this.addedClasses = absentElementList(matchedInterfaceClassList, addedClasses);

    // Class to Interface

    this.matchedClassInterfaceList = getMatchingList(getDiffList(deletedClasses, addedInterfaces));
    this.deletedClasses = absentElementList(matchedClassInterfaceList, deletedClasses);
    this.addedInterfaces = absentElementList(matchedClassInterfaceList, addedInterfaces);

    StringBuilder initial = new StringBuilder();
    StringBuilder classPrints = new StringBuilder();
    StringBuilder interfacePrints = new StringBuilder();
    StringBuilder enumPrints = new StringBuilder();
    StringBuilder assoPrints = new StringBuilder();
    StringBuilder interfaceClassPrints = new StringBuilder();

    List<Pair<Integer, String>> breakingSort = new ArrayList<>();

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

    for (CDTypeDiff<ASTCDClass, ASTCDClass> x : matchedClassList) {
      StringBuilder tmp = new StringBuilder();
      tmp.append("CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") Line: ")
          .append(x.getCd1Element().get_SourcePositionStart().getLine())
          .append("-")
          .append(x.getCd1Element().get_SourcePositionEnd().getLine())
          .append(System.lineSeparator())
          .append(x.printCD1())
          .append(System.lineSeparator())
          .append("CD2 (")
          .append(cd2.getCDDefinition().getName())
          .append(") Line: ")
          .append(x.getCd2Element().get_SourcePositionStart().getLine())
          .append("-")
          .append(x.getCd2Element().get_SourcePositionEnd().getLine())
          .append(System.lineSeparator())
          .append(x.printCD2())
          .append(System.lineSeparator())
          .append(x.getInterpretation())
          .append(System.lineSeparator())
          .append(System.lineSeparator());
      classPrints.append(tmp);
      //outputGathering.put(tmp.toString(),x.getBreakingChange());
    }

    for (CDTypeDiff<ASTCDInterface, ASTCDInterface> x : matchedInterfacesList) {
      StringBuilder tmp = new StringBuilder();
      tmp.append("CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") Line: ")
          .append(x.getCd1Element().get_SourcePositionStart().getLine())
          .append("-")
          .append(x.getCd1Element().get_SourcePositionEnd().getLine())
          .append(System.lineSeparator())
          .append(x.printCD1())
          .append(System.lineSeparator())
          .append("CD2 (")
          .append(cd2.getCDDefinition().getName())
          .append(") Line: ")
          .append(x.getCd2Element().get_SourcePositionStart().getLine())
          .append("-")
          .append(x.getCd2Element().get_SourcePositionEnd().getLine())
          .append(System.lineSeparator())
          .append(x.printCD2())
          .append(System.lineSeparator())
          .append(x.getInterpretation())
          .append(System.lineSeparator())
          .append(System.lineSeparator());
      interfacePrints.append(tmp);
      //outputGathering.put(tmp.toString(),x.getBreakingChange());
    }

    for (CDTypeDiff<ASTCDEnum, ASTCDEnum> x : matchedEnumList) {
      StringBuilder tmp = new StringBuilder();
      tmp.append("CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") Line: ")
          .append(x.getCd1Element().get_SourcePositionStart().getLine())
          .append("-")
          .append(x.getCd1Element().get_SourcePositionEnd().getLine())
          .append(System.lineSeparator())
          .append(x.printCD1())
          .append(System.lineSeparator())
          .append("CD2 (")
          .append(cd2.getCDDefinition().getName())
          .append(") Line: ")
          .append(x.getCd2Element().get_SourcePositionStart().getLine())
          .append("-")
          .append(x.getCd2Element().get_SourcePositionEnd().getLine())
          .append(System.lineSeparator())
          .append(x.printCD2())
          .append(System.lineSeparator())
          .append(x.getInterpretation())
          .append(System.lineSeparator())
          .append(System.lineSeparator());
      enumPrints.append(tmp);
      //outputGathering.put(tmp.toString(),x.getBreakingChange());
    }
    for (CDTypeDiff<ASTCDInterface, ASTCDClass> x : matchedInterfaceClassList) {
      StringBuilder tmp = new StringBuilder();
      tmp.append("CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") Line: ")
          .append(x.getCd1Element().get_SourcePositionStart().getLine())
          .append("-")
          .append(x.getCd1Element().get_SourcePositionEnd().getLine())
          .append(System.lineSeparator())
          .append(x.printCD1())
          .append(System.lineSeparator())
          .append("CD2 (")
          .append(cd2.getCDDefinition().getName())
          .append(") Line: ")
          .append(x.getCd2Element().get_SourcePositionStart().getLine())
          .append("-")
          .append(x.getCd2Element().get_SourcePositionEnd().getLine())
          .append(System.lineSeparator())
          .append(x.printCD2())
          .append(System.lineSeparator())
          .append(x.getInterpretation())
          .append(System.lineSeparator())
          .append(System.lineSeparator());
      interfaceClassPrints.append(tmp);
      //outputGathering.put(tmp.toString(),x.getBreakingChange());
    }

    for (CDAssociationDiff x : matchedAssos) {
      StringBuilder tmp = new StringBuilder();
      tmp.append("CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") and CD2 (")
          .append(cd2.getCDDefinition().getName())
          .append(")")
          .append(System.lineSeparator())
          .append("CD1: ")
          .append(x.getCd1Element().get_SourcePositionStart().getLine())
          .append("  ")
          .append(x.printCD1())
          .append(System.lineSeparator())
          .append("CD2: ")
          .append(x.getCd2Element().get_SourcePositionStart().getLine())
          .append("  ")
          .append(x.printCD2())
          .append(System.lineSeparator())
          .append(x.getInterpretation())
          .append(System.lineSeparator())
          .append(System.lineSeparator());
      assoPrints.append(tmp);
      //outputGathering.put(tmp.toString(),x.getBreakingChange());
    }

    if (!deletedClasses.isEmpty()) {
      classPrints.append("Line Deleted Classes from CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());

      for (ASTCDClass a : deletedClasses) {
        classPrints.append("CD1: ")
            .append(a.get_SourcePositionStart().getLine())
            .append("   ")
            .append(BOLD_RED)
            .append(pp.prettyprint(a))
            .append(RESET);
      }
    }

    if (!addedClasses.isEmpty()) {
      classPrints.append("Line Added Classes to CD2 (")
          .append(cd2.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());

      for (ASTCDClass a : addedClasses) {
        classPrints.append("CD2: ")
            .append(a.get_SourcePositionStart().getLine())
            .append("   ")
            .append(BOLD_GREEN)
            .append(pp.prettyprint(a))
            .append(RESET);
      }
    }

    if (!deletedEnum.isEmpty()) {
      enumPrints.append("Line Deleted Enums from CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());

      for (ASTCDEnum a : deletedEnum) {
        enumPrints.append("CD1: ")
            .append(a.get_SourcePositionStart().getLine())
            .append("   ")
            .append(BOLD_RED)
            .append(pp.prettyprint((ASTCDInterfaceAndEnumNode) a))
            .append(RESET);
      }
    }

    if (!addedEnum.isEmpty()) {
      enumPrints.append("Line Added Enums to CD2 (")
          .append(cd2.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());

      for (ASTCDEnum a : addedEnum) {
        enumPrints.append("CD2: ")
            .append(a.get_SourcePositionStart().getLine())
            .append("   ")
            .append(BOLD_GREEN)
            .append(pp.prettyprint((ASTCDInterfaceAndEnumNode) a))
            .append(RESET);
      }
    }

    if (!deletedAssos.isEmpty()) {
      assoPrints.append("Line Deleted Associations from CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());

      for (ASTCDAssociationNode asso : deletedAssos) {
        assoPrints.append("CD1: ")
            .append(asso.get_SourcePositionStart().getLine())
            .append("   ")
            .append(BOLD_RED)
            .append(pp.prettyprint(asso))
            .append(RESET);
      }
    }

    if (!addedAssos.isEmpty()) {
      assoPrints.append("Line Added Associations to CD2 (")
          .append(cd2.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());

      for (ASTCDAssociationNode asso : addedAssos) {
        assoPrints.append("CD2: ")
            .append(asso.get_SourcePositionStart().getLine())
            .append("   ")
            .append(BOLD_GREEN)
            .append(pp.prettyprint(asso))
            .append(RESET);
      }
    }

    if (!deletedInterfaces.isEmpty()) {
      interfacePrints.append("Line Deleted Interface from CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());

      for (ASTCDInterface a : deletedInterfaces) {
        interfacePrints.append("CD1: ")
            .append(a.get_SourcePositionStart().getLine())
            .append("   ")
            .append(BOLD_RED)
            .append(pp.prettyprint((ASTCDBasisNode) a))
            .append(RESET);
      }
    }

    if (!addedInterfaces.isEmpty()) {
      interfacePrints.append("CD2: ")
          .append("Line Added Interface to CD2 (")
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
      .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e1,
      LinkedHashMap::new));
    matchAndDelete.forEach((k, v) -> outPutAll.append(v));
*/

    outPutAll.append(classPrints)
        .append(interfacePrints)
        .append(enumPrints)
        .append(interfaceClassPrints)
        .append(assoPrints);

    this.outPutAll = outPutAll;
  }

  private void computeSemDiff(ASTCDCompilationUnit cd1, ASTCDCompilationUnit cd2) {

    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(cd1, cd2);

    int cd1size = cd1.getCDDefinition().getCDClassesList().size() + cd1.getCDDefinition()
        .getCDInterfacesList()
        .size();

    int cd2size = cd2.getCDDefinition().getCDClassesList().size() + cd2.getCDDefinition()
        .getCDInterfacesList()
        .size();
    int diffsizeSem = Math.max(20, 2 * Math.max(cd1size, cd2size));
    Optional<AlloyDiffSolution> optS = AlloyCDDiff.cddiff(cd1, cd2, diffsizeSem,
        CDSemantics.MULTI_INSTANCE_CLOSED_WORLD, outputPath);

    // test if solution is present
    if (!optS.isPresent()) {
      return;
    }
    AlloyDiffSolution sol = optS.get();

    // limit number of generated diff-witnesses
    sol.setSolutionLimit(1);
    sol.setLimited(true);

    // generate diff-witnesses in outputPath
    sol.generateSolutionsToPath(Paths.get(outputPath));
  }

  public void print() {
    System.out.println(outPutAll);
  }

  public void printCD1() {
    System.out.println(cd1Colored);
  }

  public void printCD2() {
    System.out.println(cd2Colored);
  }

  /**
   * Returns a list of association diffs between all associations from the two associations provided
   * as input
   *
   * @param cd1AssoList List of associations in the original Model
   * @param cd2AssoList List of associations in the target(new) Model
   * @return Returns a list for each association, ordered by diffsize (small diff values == similar)
   */
  public static List<List<CDAssociationDiff>> getAssoDiffList(List<ASTCDAssociation> cd1AssoList,
      List<ASTCDAssociation> cd2AssoList) {
    List<List<CDAssociationDiff>> assoMatches = new ArrayList<>();

    for (ASTCDAssociation cd1Asso : cd1AssoList) {

      // Create a new list for each Association
      List<CDAssociationDiff> cd1AssoMatches = new ArrayList<>();
      for (ASTCDAssociation cd2Asso : cd2AssoList) {
        // Diff list for the compared assos
        cd1AssoMatches.add(new CDAssociationDiff(cd1Asso, cd2Asso));
      }
      // Sort by size of diffs, ascending
      cd1AssoMatches.sort(Comparator.comparing(CDAssociationDiff::getDiffSize));

      assoMatches.add(cd1AssoMatches);
    }
    return assoMatches;
  }

  public static <T1 extends ASTNode, T2 extends ASTNode> List<List<CDTypeDiff<T1, T2>>> getDiffList(
      List<T1> cd1List, List<T2> cd2List) {
    List<List<CDTypeDiff<T1, T2>>> matches = new ArrayList<>();

    for (T1 cd1Element : cd1List) {

      // Create a new list for each class
      List<CDTypeDiff<T1, T2>> cd1diffs = new ArrayList<>();
      for (T2 cd2Element : cd2List) {
        // Diff list for the compared classes
        cd1diffs.add(new CDTypeDiff<T1, T2>(cd1Element, cd2Element));
      }
      // Sort by size of diffs, ascending
      cd1diffs.sort(Comparator.comparing(CDTypeDiff<T1, T2>::getDiffSize));

      matches.add(cd1diffs);
    }
    return matches;
  }

  protected static <T1 extends ASTNode, T2 extends ASTNode> List<CDTypeDiff<T1, T2>> getMatchingList(
      List<List<CDTypeDiff<T1, T2>>> elementsDiffList) {
    List<T1> cd1matchedElements = new ArrayList<>();
    List<T2> cd2matchedElements = new ArrayList<>();
    List<CDTypeDiff<T1, T2>> matchedElements = new ArrayList<>();

    for (List<CDTypeDiff<T1, T2>> currentElementList : elementsDiffList) {
      double threshold = 0;
      OptionalDouble optAverage = currentElementList.stream()
          .mapToDouble(CDTypeDiff<T1, T2>::getDiffSize)
          .average();
      if (optAverage.isPresent()) {
        threshold = optAverage.getAsDouble() / 2;
      }
      if (!currentElementList.isEmpty()) {
        for (CDTypeDiff<T1, T2> currentElementDiff : currentElementList) {
          T1 currentcd1Element = currentElementDiff.getCd1Element();
          T2 currentcd2Element = currentElementDiff.getCd2Element();
          if (!cd1matchedElements.contains(currentcd1Element) && !cd2matchedElements.contains(
              currentcd2Element)) {
            boolean found = false;
            for (List<CDTypeDiff<T1, T2>> nextElementDiffList : elementsDiffList) {
              if (!nextElementDiffList.equals(currentElementList)) {
                if (!nextElementDiffList.isEmpty()) {
                  for (CDTypeDiff<T1, T2> nextElementDiff : nextElementDiffList) {
                    if (nextElementDiff.getCd2Element().deepEquals(currentcd2Element)
                        && nextElementDiff.getDiffSize() < currentElementDiff.getDiffSize()) {
                      found = true;
                    }
                  }
                }
              }
            }
            if (!found && (currentElementDiff.getDiffSize() < threshold || threshold == 0)) {
              matchedElements.add(currentElementDiff);
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

  protected static List<CDAssociationDiff> getAssoMatchingList(List<List<CDAssociationDiff>> elementsDiffList) {
    List<ASTCDAssociation> cd1matchedElements = new ArrayList<>();
    List<ASTCDAssociation> cd2matchedElements = new ArrayList<>();
    List<CDAssociationDiff> matchedElements = new ArrayList<>();

    for (List<CDAssociationDiff> currentElementList : elementsDiffList) {
      double threshold = 0;
      OptionalDouble optAverage = currentElementList.stream()
          .mapToDouble(CDAssociationDiff::getDiffSize)
          .average();
      if (optAverage.isPresent()) {
        threshold = (1 / (double) (currentElementList.size() + 1)) + optAverage.getAsDouble() / 1.5;
      }
      if (!currentElementList.isEmpty()) {
        for (CDAssociationDiff currentElementDiff : currentElementList) {
          ASTCDAssociation cd1Element = currentElementDiff.getCd1Element();
          ASTCDAssociation cd2Element = currentElementDiff.getCd2Element();
          if (!cd1matchedElements.contains(cd1Element) && !cd2matchedElements.contains(
              cd2Element)) {
            boolean found = false;
            for (List<CDAssociationDiff> nextElementDiffList : elementsDiffList) {
              if (!nextElementDiffList.equals(currentElementList)) {
                if (!nextElementDiffList.isEmpty()) {
                  for (CDAssociationDiff nextElementDiff : nextElementDiffList) {
                    if (nextElementDiff.getCd2Element().deepEquals(cd2Element)
                        && nextElementDiff.getDiffSize() < currentElementDiff.getDiffSize()) {
                      found = true;
                    }
                  }
                }
              }
            }
            if (!found && currentElementDiff.getDiffSize() <= threshold) {
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

  protected static List<ASTCDAssociation> absentAssoList(List<CDAssociationDiff> matchs,
      List<ASTCDAssociation> elementList) {
    List<ASTCDAssociation> output = new ArrayList<>();
    for (ASTCDAssociation element : elementList) {
      boolean found = false;
      for (CDAssociationDiff diff : matchs) {
        if (diff.getCd1Element().deepEquals(element) || diff.getCd2Element().deepEquals(element)) {
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

  protected static <T1 extends ASTNode, T2 extends ASTNode, T> List<T> absentElementList(
      List<CDTypeDiff<T1, T2>> matchs, List<T> elementList) {
    List<T> output = new ArrayList<>();
    for (T element : elementList) {
      boolean found = false;
      for (CDTypeDiff<T1, T2> diff : matchs) {
        if (diff.getCd1Element().deepEquals(element) || diff.getCd2Element().deepEquals(element)) {
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

