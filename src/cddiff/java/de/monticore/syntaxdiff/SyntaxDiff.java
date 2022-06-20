package de.monticore.syntaxdiff;

import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.*;
import de.monticore.ast.ASTNode;

import java.util.*;

/**
 * Main class of the syntax diff calculation, currently used for creating matches from potential matches which are calculated in previous steps
 */
public class SyntaxDiff {

  public enum Op { CHANGE, ADD, DELETE}

  // Create the diff between two provided classdiagrams
  // CDs consists of Classes, Interfaces, Enums, Associations
  // Todo: Add json file creator
  // Todo: Extract printing
  // Todo: Add parser, change signature to two file paths
  public static void createCDDiff(ASTCDCompilationUnit cd1, ASTCDCompilationUnit cd2){

    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    // Trafo to make in-class declarations of compositions appear in the association list
    new CD4CodeDirectCompositionTrafo().transform(cd1);
    new CD4CodeDirectCompositionTrafo().transform(cd2);


    // Create Lists for each type of CDElement to check

    // Classes
    List<ASTCDClass> cd1ClassesList = cd1.getCDDefinition().getCDClassesList();
    List<ASTCDClass> cd2ClassesList = cd2.getCDDefinition().getCDClassesList();

    List<List<ClassDiff>> classesDiffList = SyntaxDiff.getClassDiffList(cd1ClassesList, cd2ClassesList);

    // Associations
    List<ASTCDAssociation> cd1AssociationsList = cd1.getCDDefinition().getCDAssociationsList();
    List<ASTCDAssociation> cd2AssociationsList = cd2.getCDDefinition().getCDAssociationsList();

    List<List<AssoDiff>> assosDiffList = SyntaxDiff.getAssoDiffList(cd1AssociationsList, cd2AssociationsList);


    // Create Class Match
    int classthreshold = 10;
    List<Integer> cd1matchedLines = new ArrayList<>();
    List<Integer> cd2matchedLines = new ArrayList<>();

    List<ClassDiff> matchedClasses = new ArrayList<>();
    List<ASTCDClass> deletedClasses = new ArrayList<>();
    List<ASTCDClass> addedClasses = new ArrayList<>();

    for (List<ClassDiff> currentClassList: classesDiffList){
      if (!currentClassList.isEmpty()) {
        ClassDiff currentClassDiff = currentClassList.get(0);
        int cd1Line = currentClassDiff.getCd1Element().get_SourcePositionStart().getLine();
        int cd2Line = currentClassDiff.getCd2Element().get_SourcePositionStart().getLine();
        if (!cd1matchedLines.contains(cd1Line) && !cd2matchedLines.contains(cd2Line)){
          int currentMinDiff = currentClassDiff.getDiffSize();
          //System.out.println("Matching Lines: " + cd1Line + ", " + cd2Line + ", size "+ currentMinDiff);
          // Todo: Check if there is a match to the target line(in cd2) with a smaller diff size
          if (currentMinDiff < classthreshold){
            matchedClasses.add(currentClassDiff);
            cd1matchedLines.add(cd1Line);
            cd2matchedLines.add(cd2Line);
          }
        }
      }

    }
    for (ASTCDClass cd1class : cd1ClassesList){
      if (!cd1matchedLines.contains(cd1class.get_SourcePositionStart().getLine())){
        deletedClasses.add(cd1class);
      }
    }
    for (ASTCDClass cd2class : cd2ClassesList){
      if (!cd2matchedLines.contains(cd2class.get_SourcePositionStart().getLine())){
        addedClasses.add(cd2class);
      }
    }

    for (ClassDiff x : matchedClasses) {
      StringBuilder output = new StringBuilder();

      x.print();


      System.out.println(output);
    }
    StringBuilder output = new StringBuilder();
    output.append("Deleted Classes: ").append(System.lineSeparator());
    for (ASTCDClass a : deletedClasses) {
      output.append(a.get_SourcePositionStart().getLine())
        .append(" ")
        .append(pp.prettyprint(a));
    }
    output.append("Added Classes: ").append(System.lineSeparator());
    for (ASTCDClass a : addedClasses) {
      output.append(a.get_SourcePositionStart().getLine())
        .append(" ")
        .append(pp.prettyprint(a));
    }
    System.out.println(output);


    // Create Association Match
    int assothreshold = 3;
    List<ASTCDAssociation> cd1Assomatched = new ArrayList<>();
    List<ASTCDAssociation> cd2Assomatched = new ArrayList<>();

    List<AssoDiff> matchedAssos = new ArrayList<>();
    List<ASTCDAssociation> deletedAssos = new ArrayList<>();
    List<ASTCDAssociation> addedAssos = new ArrayList<>();

    for (List<AssoDiff> currentAssoList: assosDiffList){
      if (!currentAssoList.isEmpty()) {
        AssoDiff currentAssoDiff = currentAssoList.get(0);
        ASTCDAssociation cd1Asso = currentAssoList.get(0).getCd1Element();
        ASTCDAssociation cd2Asso = currentAssoList.get(0).getCd2Element();
        if (!cd1Assomatched.contains(cd1Asso) && !cd2Assomatched.contains(cd2Asso)){
          // Todo: Check if there is a match to the target association with a smaller diff size
          if (currentAssoDiff.getDiffSize() < assothreshold){
            matchedAssos.add(currentAssoDiff);
            cd1Assomatched.add(cd1Asso);
            cd2Assomatched.add(cd2Asso);
          }
        }
        currentAssoDiff.print();
      }

    }
    for (ASTCDAssociation cd1assos : cd1AssociationsList){
      if (!cd1Assomatched.contains(cd1assos)){
        deletedAssos.add(cd1assos);
      }
    }
    for (ASTCDAssociation cd2assos : cd2AssociationsList){
      if (!cd2Assomatched.contains(cd2assos)){
        addedAssos.add(cd2assos);
      }
    }
    StringBuilder addDelAssos = new StringBuilder();
    addDelAssos.append("Deleted Associations: ")
               .append(deletedAssos.size())
               .append(System.lineSeparator());

    for (ASTCDAssociationNode asso : deletedAssos){
      addDelAssos.append(pp.prettyprint(asso));
    }

    addDelAssos.append("Added Associations: ")
               .append(addedAssos.size())
               .append(System.lineSeparator());

    for (ASTCDAssociationNode asso : addedAssos){
      addDelAssos.append(pp.prettyprint(asso));
    }
    System.out.println(addDelAssos);

    // Create Interface Match

    // Create Enum Match
  }

  /**
   * Create a FieldDiff Object between the two given Fields
   * @param cd1Field Field in the original Model
   * @param cd2Field Field in the target(new) Model
   * @return A FieldDiff Object, which contains the ASTFields and operation
   * @param <NodeType> Ensure the boundary of ASTNodes as input
   */
  public static <NodeType extends ASTNode> FieldDiff<Op, NodeType> getFieldDiff(Optional<NodeType> cd1Field,
      Optional<NodeType> cd2Field) {
    if (cd1Field.isPresent() && cd2Field.isPresent() && !cd1Field.get().deepEquals(cd2Field.get())) {
      // Diff reason: Value changed
      return new FieldDiff<>(Op.CHANGE, cd1Field.get(), cd2Field.get());

    } else if (cd1Field.isPresent() && !cd2Field.isPresent()) {
      // Diff reason: Value deleted
      return new FieldDiff<>(Op.DELETE, cd1Field.get(), null);

    } else if (!cd1Field.isPresent() && cd2Field.isPresent()) {
      // Diff reason: Value added
      return new FieldDiff<>(Op.ADD, null, cd2Field.get());

    } else {
      // No Diff reason: is equal
      return new FieldDiff<>();
    }
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
}

