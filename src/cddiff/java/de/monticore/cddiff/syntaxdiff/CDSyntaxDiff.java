/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syntaxdiff;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDAssociationNode;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterfaceAndEnumNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import java.util.*;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Main class of the syntax diff calculation, combines all diff types and provide multiple functions
 * to access and print the calculated diffs and matchs.
 */
public class CDSyntaxDiff {

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

  public enum Op {
    CHANGE,
    ADD,
    DELETE
  }

  public enum Interpretation {
    REDUCED,
    REFINED,
    REVERSED,
    SUPERTYPE,
    SUBTYPE,
    RENAME,
    RELOCATION,
    REFINEMENT,
    EXPANSION,
    INCOMPARABLE,
    DEFAULTVALUECHANGED,
    ABSTRACTION,
    REPURPOSED,
    INHERITED,
    EQUALINTERVAL,
    SCOPECHANGE,
    ROLECHANGE,
    DEFAULTVALUE_ADDED,
    DELETED,
    EQUAL,
    TYPECHANGE,
    RESTRICTION,
    RESTRICT_INTERVAL,
    EXPAND_INTERVAL,
    EQUAL_INTERVAL,
    BREAKINGCHANGE,
    ASSOCIATION_INHERITED,
    ATTRIBUTE_INHERITED,
    METHOD_INHERITED
  }

  protected StringBuilder outPutAll;

  protected StringBuilder cd1Colored;

  protected StringBuilder cd2Colored;

  protected StringBuilder outPutAllNC;

  protected ASTCDCompilationUnit cd1;

  protected ASTCDCompilationUnit cd2;

  CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

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

  /**
   * Constructor for the syntax diff. Creates an object which contain all analysable information
   * regarding the syntax of the provided class diagrams. Note that each match and interpretation is
   * calculated from CD1 -> CD2!
   *
   * @param cd1 Old version of the class diagram
   * @param cd2 New version of the class diagram
   */
  public CDSyntaxDiff(ASTCDCompilationUnit cd1, ASTCDCompilationUnit cd2) {

    this.cd1 = cd1;
    this.cd2 = cd2;

    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    // Trafo to make in-class declarations of compositions appear in the association list
    new CD4CodeDirectCompositionTrafo().transform(cd1);
    new CD4CodeDirectCompositionTrafo().transform(cd2);

    // Color Codes for colored console output
    final String BOLD_RED = "\033[1;31m";
    final String BOLD_GREEN = "\033[1;32m";
    final String RESET = "\033[0m";

    // Create scopes for both class diagrams
    ICD4CodeArtifactScope scopecd1 = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd1);
    ICD4CodeArtifactScope scopecd2 = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd2);

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

    this.matchedClassList =
        getCDTypeMatchingList(getDiffList(cd1ClassesList, cd2ClassesList, scopecd1, scopecd2));
    this.deletedClasses = absentCDTypeList(getMatchedClassList(), cd1ClassesList);
    this.addedClasses = absentCDTypeList(getMatchedClassList(), cd2ClassesList);

    // Create Interface Match

    this.matchedInterfacesList =
        getCDTypeMatchingList(
            getDiffList(cd1InterfacesList, cd2InterfacesList, scopecd1, scopecd2));
    this.deletedInterfaces = absentCDTypeList(getMatchedInterfaces(), cd1InterfacesList);
    this.addedInterfaces = absentCDTypeList(getMatchedInterfaces(), cd2InterfacesList);

    // Create Enum Match

    this.matchedEnumList =
        getCDTypeMatchingList(getDiffList(cd1EnumList, cd2EnumList, scopecd1, scopecd2));
    this.deletedEnum = absentCDTypeList(getMatchedEnumList(), cd1EnumList);
    this.addedEnum = absentCDTypeList(getMatchedEnumList(), cd2EnumList);

    // Create Association Match
    this.matchedAssos =
        getAssoMatchingList(getAssoDiffList(cd1AssociationsList, cd2AssociationsList));
    this.deletedAssos = absentAssoList(matchedAssos, cd1AssociationsList);
    this.addedAssos = absentAssoList(matchedAssos, cd2AssociationsList);

    // Interface changed to Class

    this.matchedInterfaceClassList =
        getCDTypeMatchingList(getDiffList(deletedInterfaces, addedClasses, scopecd1, scopecd2));
    this.deletedInterfaces = absentCDTypeList(matchedInterfaceClassList, deletedInterfaces);
    this.addedClasses = absentCDTypeList(matchedInterfaceClassList, addedClasses);

    // Class changed to Interface

    this.matchedClassInterfaceList =
        getCDTypeMatchingList(getDiffList(deletedClasses, addedInterfaces, scopecd1, scopecd2));
    this.deletedClasses = absentCDTypeList(matchedClassInterfaceList, deletedClasses);
    this.addedInterfaces = absentCDTypeList(matchedClassInterfaceList, addedInterfaces);

    StringBuilder initial = new StringBuilder();
    StringBuilder classPrints = new StringBuilder();
    StringBuilder classPrintsNC = new StringBuilder();
    StringBuilder interfacePrints = new StringBuilder();
    StringBuilder interfacePrintsNC = new StringBuilder();
    StringBuilder enumPrints = new StringBuilder();
    StringBuilder enumPrintsNC = new StringBuilder();
    StringBuilder assoPrints = new StringBuilder();
    StringBuilder assoPrintsNC = new StringBuilder();

    List<Pair<Integer, String>> breakingSort = new ArrayList<>();
    List<Pair<Integer, String>> breakingSortNC = new ArrayList<>();
    List<Pair<Integer, String>> onlyCD1Sort = new ArrayList<>();
    List<Pair<Integer, String>> onlyCD2Sort = new ArrayList<>();

    // System.out.println("Scope: ");
    // System.out.println(cd1.getEnclosingScope().getRealPackageName());

    initial
        .append(System.lineSeparator())
        .append("In the following the syntax diff between ")
        .append(cd1.getCDDefinition().getName())
        .append(" and ")
        .append(cd2.getCDDefinition().getName())
        .append(" is created")
        .append(System.lineSeparator())
        .append(System.lineSeparator());

    for (CDTypeDiff<ASTCDClass, ASTCDClass> x : matchedClassList) {
      StringBuilder tmp = createMatchString(x, true);
      StringBuilder tmpNC = createMatchString(x, false);

      breakingSort.add(Pair.of(x.getBreakingChange(), tmp.toString()));
      breakingSortNC.add(Pair.of(x.getBreakingChange(), tmpNC.toString()));
      onlyCD1Sort.add(Pair.of(x.getCd1Element().get_SourcePositionStart().getLine(), x.printCD1()));
      onlyCD2Sort.add(Pair.of(x.getCd2Element().get_SourcePositionStart().getLine(), x.printCD2()));
    }

    for (CDTypeDiff<ASTCDInterface, ASTCDInterface> x : matchedInterfacesList) {
      StringBuilder tmp = createMatchString(x, true);
      StringBuilder tmpNC = createMatchString(x, false);

      breakingSort.add(Pair.of(x.getBreakingChange(), tmp.toString()));
      breakingSortNC.add(Pair.of(x.getBreakingChange(), tmpNC.toString()));
      onlyCD1Sort.add(Pair.of(x.getCd1Element().get_SourcePositionStart().getLine(), x.printCD1()));
      onlyCD2Sort.add(Pair.of(x.getCd2Element().get_SourcePositionStart().getLine(), x.printCD2()));
    }

    for (CDTypeDiff<ASTCDEnum, ASTCDEnum> x : matchedEnumList) {
      StringBuilder tmp = createMatchString(x, true);
      StringBuilder tmpNC = createMatchString(x, false);

      breakingSort.add(Pair.of(x.getBreakingChange(), tmp.toString()));
      breakingSortNC.add(Pair.of(x.getBreakingChange(), tmpNC.toString()));
      onlyCD1Sort.add(Pair.of(x.getCd1Element().get_SourcePositionStart().getLine(), x.printCD1()));
      onlyCD2Sort.add(Pair.of(x.getCd2Element().get_SourcePositionStart().getLine(), x.printCD2()));
    }
    for (CDTypeDiff<ASTCDInterface, ASTCDClass> x : matchedInterfaceClassList) {
      StringBuilder tmp = createMatchString(x, true);
      StringBuilder tmpNC = createMatchString(x, false);

      breakingSort.add(Pair.of(x.getBreakingChange(), tmp.toString()));
      breakingSortNC.add(Pair.of(x.getBreakingChange(), tmpNC.toString()));
      onlyCD1Sort.add(Pair.of(x.getCd1Element().get_SourcePositionStart().getLine(), x.printCD1()));
      onlyCD2Sort.add(Pair.of(x.getCd2Element().get_SourcePositionStart().getLine(), x.printCD2()));
    }
    for (CDTypeDiff<ASTCDClass, ASTCDInterface> x : matchedClassInterfaceList) {
      StringBuilder tmp = createMatchString(x, true);
      StringBuilder tmpNC = createMatchString(x, false);

      breakingSort.add(Pair.of(x.getBreakingChange(), tmp.toString()));
      breakingSortNC.add(Pair.of(x.getBreakingChange(), tmpNC.toString()));
      onlyCD1Sort.add(Pair.of(x.getCd1Element().get_SourcePositionStart().getLine(), x.printCD1()));
      onlyCD2Sort.add(Pair.of(x.getCd2Element().get_SourcePositionStart().getLine(), x.printCD2()));
    }

    for (CDAssociationDiff x : matchedAssos) {
      StringBuilder tmp = createMatchString(x, true);
      StringBuilder tmpNC = createMatchString(x, false);

      breakingSort.add(Pair.of(x.getBreakingChange(), tmp.toString()));
      breakingSortNC.add(Pair.of(x.getBreakingChange(), tmpNC.toString()));
      onlyCD1Sort.add(Pair.of(x.getCd1Element().get_SourcePositionStart().getLine(), x.printCD1()));
      onlyCD2Sort.add(Pair.of(x.getCd2Element().get_SourcePositionStart().getLine(), x.printCD2()));
    }

    if (!deletedClasses.isEmpty()) {
      classPrints
          .append("Line Deleted Classes from CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());
      classPrintsNC
          .append("Line Deleted Classes from CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());

      for (ASTCDClass x : deletedClasses) {
        String tmp = BOLD_RED + pp.prettyprint(x) + RESET;
        classPrints
            .append("CD1: ")
            .append(x.get_SourcePositionStart().getLine())
            .append("   ")
            .append(tmp);
        classPrintsNC
            .append("CD1: ")
            .append(x.get_SourcePositionStart().getLine())
            .append("   ")
            .append(pp.prettyprint(x));
        onlyCD1Sort.add(Pair.of(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!addedClasses.isEmpty()) {
      classPrints
          .append("Line Added Classes to CD2 (")
          .append(cd2.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());
      classPrintsNC
          .append("Line Added Classes to CD2 (")
          .append(cd2.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());

      for (ASTCDClass x : addedClasses) {
        String tmp = BOLD_GREEN + pp.prettyprint(x) + RESET;
        classPrints
            .append("CD2: ")
            .append(x.get_SourcePositionStart().getLine())
            .append("   ")
            .append(tmp);
        classPrintsNC
            .append("CD2: ")
            .append(x.get_SourcePositionStart().getLine())
            .append("   ")
            .append(pp.prettyprint(x));
        onlyCD2Sort.add(Pair.of(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!deletedEnum.isEmpty()) {
      enumPrints
          .append("Line Deleted Enums from CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());
      enumPrintsNC
          .append("Line Deleted Enums from CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());

      for (ASTCDEnum x : deletedEnum) {
        String tmp = BOLD_RED + pp.prettyprint((ASTCDInterfaceAndEnumNode) x) + RESET;
        enumPrints
            .append("CD1: ")
            .append(x.get_SourcePositionStart().getLine())
            .append("   ")
            .append(tmp);
        enumPrintsNC
            .append("CD1: ")
            .append(x.get_SourcePositionStart().getLine())
            .append("   ")
            .append(pp.prettyprint((ASTCDInterfaceAndEnumNode) x));
        onlyCD1Sort.add(Pair.of(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!addedEnum.isEmpty()) {
      enumPrints
          .append("Line Added Enums to CD2 (")
          .append(cd2.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());
      enumPrintsNC
          .append("Line Added Enums to CD2 (")
          .append(cd2.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());

      for (ASTCDEnum x : addedEnum) {
        String tmp = BOLD_GREEN + pp.prettyprint((ASTCDInterfaceAndEnumNode) x) + RESET;
        enumPrints
            .append("CD2: ")
            .append(x.get_SourcePositionStart().getLine())
            .append("   ")
            .append(tmp);
        enumPrintsNC
            .append("CD2: ")
            .append(x.get_SourcePositionStart().getLine())
            .append("   ")
            .append(pp.prettyprint((ASTCDInterfaceAndEnumNode) x));
        onlyCD2Sort.add(Pair.of(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!deletedAssos.isEmpty()) {
      assoPrints
          .append("Line Deleted Associations from CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());
      assoPrintsNC
          .append("Line Deleted Associations from CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());

      for (ASTCDAssociationNode x : deletedAssos) {
        String tmp = BOLD_RED + pp.prettyprint(x) + RESET;
        assoPrints
            .append("CD1: ")
            .append(x.get_SourcePositionStart().getLine())
            .append("   ")
            .append(tmp);
        assoPrintsNC
            .append("CD1: ")
            .append(x.get_SourcePositionStart().getLine())
            .append("   ")
            .append(pp.prettyprint(x));
        onlyCD1Sort.add(Pair.of(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!addedAssos.isEmpty()) {
      assoPrints
          .append("Line Added Associations to CD2 (")
          .append(cd2.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());
      assoPrintsNC
          .append("Line Added Associations to CD2 (")
          .append(cd2.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());

      for (ASTCDAssociationNode x : addedAssos) {
        String tmp = BOLD_GREEN + pp.prettyprint(x) + RESET;
        assoPrints
            .append("CD2: ")
            .append(x.get_SourcePositionStart().getLine())
            .append("   ")
            .append(tmp);
        assoPrintsNC
            .append("CD2: ")
            .append(x.get_SourcePositionStart().getLine())
            .append("   ")
            .append(pp.prettyprint(x));
        onlyCD2Sort.add(Pair.of(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!deletedInterfaces.isEmpty()) {
      interfacePrints
          .append("Line Deleted Interface from CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());
      interfacePrintsNC
          .append("Line Deleted Interface from CD1 (")
          .append(cd1.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());

      for (ASTCDInterface x : deletedInterfaces) {
        String tmp = BOLD_RED + pp.prettyprint((ASTCDBasisNode) x) + RESET;
        interfacePrints
            .append("CD1: ")
            .append(x.get_SourcePositionStart().getLine())
            .append("   ")
            .append(tmp);
        interfacePrintsNC
            .append("CD1: ")
            .append(x.get_SourcePositionStart().getLine())
            .append("   ")
            .append(pp.prettyprint((ASTCDBasisNode) x));
        onlyCD1Sort.add(Pair.of(x.get_SourcePositionStart().getLine(), tmp));
      }
    }

    if (!addedInterfaces.isEmpty()) {
      interfacePrints
          .append("CD2: ")
          .append("Line Added Interface to CD2 (")
          .append(cd2.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());
      interfacePrintsNC
          .append("CD2: ")
          .append("Line Added Interface to CD2 (")
          .append(cd2.getCDDefinition().getName())
          .append(") :")
          .append(System.lineSeparator());

      for (ASTCDInterface x : addedInterfaces) {
        String tmp = BOLD_GREEN + pp.prettyprint((ASTCDBasisNode) x) + RESET;
        interfacePrints.append(x.get_SourcePositionStart().getLine()).append("   ").append(tmp);
        interfacePrintsNC
            .append(x.get_SourcePositionStart().getLine())
            .append("   ")
            .append(pp.prettyprint((ASTCDBasisNode) x));
        onlyCD2Sort.add(Pair.of(x.get_SourcePositionStart().getLine(), tmp));
      }
    }
    StringBuilder outPutAll = new StringBuilder();
    outPutAll.append(initial);

    StringBuilder outPutAllNC = new StringBuilder();
    outPutAllNC.append(initial);

    /*
    outPutAll.append(classPrints)
        .append(interfacePrints)
        .append(enumPrints)
        .append(interfaceClassPrints)
        .append(assoPrints);
    */

    onlyCD1Sort.sort(Comparator.comparing(p -> +p.getLeft()));
    StringBuilder outPutCD1 = new StringBuilder();
    outPutCD1.append("classdiagram ").append(cd1.getCDDefinition().getName()).append(" {");
    for (Pair<Integer, String> x : onlyCD1Sort) {
      outPutCD1.append(System.lineSeparator()).append(x.getValue());
    }
    outPutCD1.append(System.lineSeparator()).append("}");
    this.cd1Colored = outPutCD1;

    onlyCD2Sort.sort(Comparator.comparing(p -> +p.getLeft()));
    StringBuilder outPutCD2 = new StringBuilder();
    outPutCD2.append("classdiagram ").append(cd2.getCDDefinition().getName()).append(" {");
    for (Pair<Integer, String> x : onlyCD2Sort) {
      outPutCD2.append(System.lineSeparator()).append(x.getValue());
    }
    outPutCD2.append(System.lineSeparator()).append("}");
    this.cd2Colored = outPutCD2;

    // Sort by breaking score which indicates the impact of the recognized change
    breakingSort.sort(Comparator.comparing(p -> -p.getLeft()));
    breakingSortNC.sort(Comparator.comparing(p -> -p.getLeft()));

    for (Pair<Integer, String> x : breakingSort) {
      outPutAll.append(x.getValue());
    }
    outPutAll.append(classPrints);
    outPutAll.append(interfacePrints);
    outPutAll.append(assoPrints);
    outPutAll.append(enumPrints);
    this.outPutAll = outPutAll;

    for (Pair<Integer, String> x : breakingSortNC) {
      outPutAllNC.append(x.getValue());
    }
    outPutAllNC.append(classPrintsNC);
    outPutAllNC.append(interfacePrintsNC);
    outPutAllNC.append(assoPrintsNC);
    outPutAllNC.append(enumPrintsNC);
    this.outPutAllNC = outPutAllNC;
  }

  private <T1 extends ASTCDType, T2 extends ASTCDType> StringBuilder createMatchString(
      CDTypeDiff<T1, T2> x, boolean coloured) {
    StringBuilder tmp = new StringBuilder();
    // Header for first element
    tmp.append("CD1 (")
        .append(cd1.getCDDefinition().getName())
        .append(") Line: ")
        .append(x.getCd1Element().get_SourcePositionStart().getLine())
        .append("-")
        .append(x.getCd1Element().get_SourcePositionEnd().getLine())
        .append(System.lineSeparator());

    // Add either coloured or plain
    if (coloured) {
      tmp.append(x.printCD1());
    } else {
      tmp.append(x.printCD1NC());
    }
    // Header for second element
    tmp.append(System.lineSeparator())
        .append("CD2 (")
        .append(cd2.getCDDefinition().getName())
        .append(") Line: ")
        .append(x.getCd2Element().get_SourcePositionStart().getLine())
        .append("-")
        .append(x.getCd2Element().get_SourcePositionEnd().getLine())
        .append(System.lineSeparator());

    // Add either coloured or plain
    if (coloured) {
      tmp.append(x.printCD2());
    } else {
      tmp.append(x.printCD2NC());
    }

    tmp.append(System.lineSeparator())
        .append(x.getInterpretation())
        .append(System.lineSeparator())
        .append(System.lineSeparator());
    return tmp;
  }

  private StringBuilder createMatchString(CDAssociationDiff x, boolean coloured) {
    StringBuilder tmp = new StringBuilder();
    // Header
    tmp.append("CD1 (")
        .append(cd1.getCDDefinition().getName())
        .append(") and CD2 (")
        .append(cd2.getCDDefinition().getName())
        .append(")")
        .append(System.lineSeparator())
        .append("CD1: ")
        .append(x.getCd1Element().get_SourcePositionStart().getLine())
        .append("  ");

    // Add either coloured or plain
    if (coloured) {
      tmp.append(x.printCD1());
    } else {
      tmp.append(x.printCD1NC());
    }
    // Header for second element
    tmp.append(System.lineSeparator())
        .append("CD2: ")
        .append(x.getCd2Element().get_SourcePositionStart().getLine())
        .append("  ");

    // Add either coloured or plain
    if (coloured) {
      tmp.append(x.printCD2());
    } else {
      tmp.append(x.printCD2NC());
    }

    tmp.append(System.lineSeparator())
        .append(x.getInterpretation())
        .append(System.lineSeparator())
        .append(System.lineSeparator());
    return tmp;
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

  public void printNoColour() {
    System.out.println(outPutAllNC);
  }

  /**
   * Returns a list of potential association matchs between all associations from the two provided
   * lists
   *
   * @param cd1AssoList List of associations in the original(old) Model
   * @param cd2AssoList List of associations in the target(new) Model
   * @return Returns a list for each association, ordered by diffsize (small diff values == similar)
   */
  public List<List<CDAssociationDiff>> getAssoDiffList(
      List<ASTCDAssociation> cd1AssoList, List<ASTCDAssociation> cd2AssoList) {
    List<List<CDAssociationDiff>> assoMatches = new ArrayList<>();

    for (ASTCDAssociation cd1Asso : cd1AssoList) {

      // Create a new list for each Association
      List<CDAssociationDiff> cd1AssoMatches = new ArrayList<>();
      for (ASTCDAssociation cd2Asso : cd2AssoList) {
        // Diff list for the compared assos
        CDAssociationDiff tmp = new CDAssociationDiff(cd1Asso, cd2Asso);
        if (checkInherited(tmp)) {
          tmp.addDiffSize(-1);
          List<Interpretation> tmpList = new ArrayList<>();
          for (CDSyntaxDiff.Interpretation inter : tmp.getInterpretationList()) {
            if (inter.equals(Interpretation.RENAME)) {
              tmpList.add(Interpretation.ASSOCIATION_INHERITED);

            } else {
              tmpList.add(inter);
            }
          }
          tmp.setInterpretationList(tmpList);
          StringBuilder tmpBuilder = tmp.getInterpretation();
          tmpBuilder.append(Interpretation.ASSOCIATION_INHERITED);
          tmp.setInterpretation(tmpBuilder);
        }
        cd1AssoMatches.add(tmp);
      }
      // Sort by size of diffs, ascending
      cd1AssoMatches.sort(Comparator.comparing(CDAssociationDiff::getDiffSize));

      assoMatches.add(cd1AssoMatches);
    }
    return assoMatches;
  }

  /**
   * Checks if the provided association diff contains a inherited associations e.g asso: A--C and
   * B--C with class A; in CD1 and class A extends B; in CD2
   *
   * @param diff Provide any association diff
   * @return True if the association is inherited in the new version
   */
  protected boolean checkInherited(CDAssociationDiff diff) {
    boolean found = false;
    for (ASTNodeDiff<? extends ASTNode, ? extends ASTNode> x : diff.getDiffList()) {
      if (x.getCd1Value().isPresent()
          && x.getCd1Value().get() instanceof ASTMCQualifiedName
          && x.getCd2Value().isPresent()
          && x.getCd2Value().get() instanceof ASTMCQualifiedName) {
        // name diff found
        ASTMCQualifiedName cd1name = (ASTMCQualifiedName) x.getCd1Value().get();
        ASTMCQualifiedName cd2name = (ASTMCQualifiedName) x.getCd2Value().get();
        for (CDTypeDiff<ASTCDClass, ASTCDClass> classDiff : getMatchedClassList()) {
          if (classDiff.getCd1Element().getName().equals(cd1name.getQName())) {
            if (classDiff.getCd2Element().isPresentCDExtendUsage()) {
              if (pp.prettyprint(classDiff.getCd2Element().getCDExtendUsage().getSuperclass(0))
                  .equals(cd2name.getQName())) {
                found = true;
                break;
              }
            }
          }
        }
      }
    }
    return found;
  }

  /**
   * Create a list of potential matchs between each element of both provided lists. Note that this
   * list need to be reduced as the result is basically cd1List x cd2List
   *
   * @param cd1List List of elements from the old model
   * @param cd2List List of elements from the new model
   * @param scopecd1 Scope of the first model
   * @param scopecd2 Scope of the second model
   * @return List of potential matchs: cd1List x cd2List
   * @param <T1> Type of the first element.
   * @param <T2> Type of the second element.
   */
  public <T1 extends ASTCDType, T2 extends ASTCDType> List<List<CDTypeDiff<T1, T2>>> getDiffList(
      List<T1> cd1List,
      List<T2> cd2List,
      ICD4CodeArtifactScope scopecd1,
      ICD4CodeArtifactScope scopecd2) {
    List<List<CDTypeDiff<T1, T2>>> matches = new ArrayList<>();

    for (T1 cd1Element : cd1List) {
      // Create a new list for each class
      List<CDTypeDiff<T1, T2>> cd1diffs = new ArrayList<>();
      for (T2 cd2Element : cd2List) {
        // Diff list for the compared classes
        cd1diffs.add(new CDTypeDiff<>(cd1Element, cd2Element, scopecd1, scopecd2));
      }
      // Sort by size of diffs, ascending
      cd1diffs.sort(Comparator.comparing(CDTypeDiff<T1, T2>::getDiffSize));

      matches.add(cd1diffs);
    }
    return matches;
  }

  /**
   * Create matchings between all elements provided in the input list. Provides for each element
   * from CD1 or CD2 only the smallest match.
   *
   * @param elementsDiffList List of all potential matchs between elements.
   * @return Reduced list of matchs, only one for each pair.
   * @param <T1> Type of the first element.
   * @param <T2> Type of the second element.
   */
  protected <T1 extends ASTCDType, T2 extends ASTCDType>
      List<CDTypeDiff<T1, T2>> getCDTypeMatchingList(
          List<List<CDTypeDiff<T1, T2>>> elementsDiffList) {
    List<T1> cd1matchedElements = new ArrayList<>();
    List<T2> cd2matchedElements = new ArrayList<>();
    List<CDTypeDiff<T1, T2>> matchedElements = new ArrayList<>();

    for (List<CDTypeDiff<T1, T2>> currentElementList : elementsDiffList) {
      double threshold = 0;
      OptionalDouble optAverage =
          currentElementList.stream().mapToDouble(CDTypeDiff<T1, T2>::getDiffSize).average();
      if (optAverage.isPresent()) {
        threshold = optAverage.getAsDouble() * 0.7;
      }
      if (!currentElementList.isEmpty()) {
        for (CDTypeDiff<T1, T2> currentElementDiff : currentElementList) {
          T1 currentcd1Element = currentElementDiff.getCd1Element();
          T2 currentcd2Element = currentElementDiff.getCd2Element();
          if (!cd1matchedElements.contains(currentcd1Element)
              && !cd2matchedElements.contains(currentcd2Element)) {
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

  /**
   * Create matchings between all associations provided in the input list. Provides for each
   * association from CD1 or CD2 only the smallest match.
   *
   * @param elementsDiffList List of all potential matchs between associations.
   * @return Reduced list of matchs, only one for each pair
   */
  protected List<CDAssociationDiff> getAssoMatchingList(
      List<List<CDAssociationDiff>> elementsDiffList) {
    List<ASTCDAssociation> cd1matchedElements = new ArrayList<>();
    List<ASTCDAssociation> cd2matchedElements = new ArrayList<>();
    List<CDAssociationDiff> matchedElements = new ArrayList<>();

    for (List<CDAssociationDiff> currentElementList : elementsDiffList) {
      double threshold = 0;
      OptionalDouble optAverage =
          currentElementList.stream().mapToDouble(CDAssociationDiff::getDiffSize).average();
      if (optAverage.isPresent()) {
        threshold = (1 / (double) (currentElementList.size() + 1)) + optAverage.getAsDouble() / 1.5;
      }
      if (!currentElementList.isEmpty()) {
        for (CDAssociationDiff currentElementDiff : currentElementList) {
          ASTCDAssociation cd1Element = currentElementDiff.getCd1Element();
          ASTCDAssociation cd2Element = currentElementDiff.getCd2Element();
          if (!cd1matchedElements.contains(cd1Element)
              && !cd2matchedElements.contains(cd2Element)) {
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

  /**
   * Create a list of associations which are not part of the matchs list.
   *
   * @param matchs List of matchs between associations
   * @param elementList List of associations to be reduced
   * @return List of associations, reduced by all associations which are part of the match list.
   */
  protected List<ASTCDAssociation> absentAssoList(
      List<CDAssociationDiff> matchs, List<ASTCDAssociation> elementList) {
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

  /**
   * Create a list of elements, with two equal or different typs, which are not part of the matchs
   * list. Note that the Types can be equal, as well as different. But nothing will happen if T is
   * not either T1 or T2.
   *
   * @param matchs List of matchs between elements
   * @param elementList List of elements to be reduced
   * @return List of elements, reduced by all elements which are part of the match list.
   * @param <T1> Type of the first element.
   * @param <T2> Type of the second element.
   * @param <T> Type of the elements which want to be found inside the match list, either T1 or T2.
   */
  protected <T1 extends ASTCDType, T2 extends ASTCDType, T> List<T> absentCDTypeList(
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
