/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge;

import static org.junit.jupiter.api.Assertions.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.config.MergeParameter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

public class CDMergeTest extends BaseTest {
  
  @Test
  public void testMerge() {
    
    final String srcDir = "src/test/resources/class_diagrams/CDMergeTest/";
    List<ASTCDCompilationUnit> inputSet = new ArrayList<>();
    try {
      inputSet.add(loadModel(srcDir + "A.cd"));
      inputSet.add(loadModel(srcDir + "B.cd"));
      inputSet.add(loadModel(srcDir + "C.cd"));
    }
    catch (IOException e) {
      fail("IO exception while accessing input models: " + e.getMessage());
    }
    
    ASTCDCompilationUnit mergedCD = CDMerge.merge(inputSet, "ABC", new LinkedHashSet<>());
    
    assertNotNull(mergedCD);
    Log.print(CD4CodeMill.prettyPrint(mergedCD, true));
  }
  
  @Test
  public void testMotivatingExample() {
    final String srcDir = "src/test/resources/class_diagrams/CDMergeTest/";
    List<ASTCDCompilationUnit> inputSet = new ArrayList<>();
    try {
      inputSet.add(loadModel(srcDir + "Teaching.cd"));
      inputSet.add(loadModel(srcDir + "Management.cd"));
    }
    catch (IOException e) {
      fail("IO exception while accessing input models: " + e.getMessage());
    }
    
    LinkedHashSet<MergeParameter> params = new LinkedHashSet<>();
    
    params.add(MergeParameter.LOG_VERBOSE);
    params.add(MergeParameter.LOG_TO_CONSOLE);
    
    ASTCDCompilationUnit mergedCD = CDMerge.merge(inputSet, "UniversitySystem", params);
    
    assertNotNull(mergedCD);
    Log.print(CD4CodeMill.prettyPrint(mergedCD, true));
  }
  
  @Test
  public void testMotivatingExampleWithPackages() {
    final String srcDir = "src/test/resources/class_diagrams/CDMergeTest/";
    List<ASTCDCompilationUnit> inputSet = new ArrayList<>();
    try {
      inputSet.add(loadModel(srcDir + "PackagedTeaching.cd"));
      inputSet.add(loadModel(srcDir + "PackagedManagement.cd"));
    }
    catch (IOException e) {
      fail("IO exception while accessing input models: " + e.getMessage());
    }
    
    LinkedHashSet<MergeParameter> params = new LinkedHashSet<>();
    
    params.add(MergeParameter.LOG_VERBOSE);
    params.add(MergeParameter.LOG_TO_CONSOLE);
    
    ASTCDCompilationUnit mergedCD = CDMerge.merge(inputSet, "UniversitySystem", params);
    
    assertNotNull(mergedCD);
    Log.print(CD4CodeMill.prettyPrint(mergedCD, true));
  }
  
  @Test
  public void testUMLPExample() {
    final String srcDir = "src/test/resources/class_diagrams/umlp/";
    List<ASTCDCompilationUnit> inputSet = new ArrayList<>();
    ASTCDCompilationUnit expected = null;
    try {
      expected = loadModel(srcDir + "MergeDriveAndEmployment.umlp");
      inputSet.add(loadModel(srcDir + "Driver.umlp"));
      inputSet.add(loadModel(srcDir + "Employment.umlp"));
    }
    catch (IOException e) {
      fail("IO exception while accessing input models: " + e.getMessage());
    }
    
    LinkedHashSet<MergeParameter> params = new LinkedHashSet<>();
    
    params.add(MergeParameter.LOG_VERBOSE);
    params.add(MergeParameter.LOG_TO_CONSOLE);
    
    ASTCDCompilationUnit mergedCD = CDMerge.merge(inputSet, "MergeDriveAndEmployment", params);
    
    assertNotNull(mergedCD);
    Log.print(CD4CodeMill.prettyPrint(mergedCD, true));
    assertTrue(mergedCD.deepEquals(expected, false));
  }
  
  @Test
  public void testCarRental_correct() {
    final String srcDir = "src/test/resources/class_diagrams/carrental/";
    List<ASTCDCompilationUnit> inputSet = new ArrayList<>();
    ASTCDCompilationUnit expected = null;
    try {
      inputSet.add(loadModel(srcDir + "Renting.cd"));
      inputSet.add(loadModel(srcDir + "Trucks.cd"));
      inputSet.add(loadModel(srcDir + "Cars.cd"));
      inputSet.add(loadModel(srcDir + "CustomerService.cd"));
    }
    catch (IOException e) {
      fail("IO exception while accessing input models: " + e.getMessage());
    }
    
    // Test all permutations of the merging order
    for (List<ASTCDCompilationUnit> permutation : computeAllPermutations(inputSet)) {
      LinkedHashSet<MergeParameter> params = new LinkedHashSet<>();
      
      Log.print("Merging " + permutation.stream().map(cd -> cd.getCDDefinition().getName()).collect(
          Collectors.toList()));
      
      params.add(MergeParameter.LOG_VERBOSE);
      params.add(MergeParameter.LOG_TO_CONSOLE);
      
      ASTCDCompilationUnit mergedCD = CDMerge.merge(permutation, "CarRental", params);
      
      assertNotNull(mergedCD);
    }
  }
  
  // recursive helper function that produces all permutations of a list
  private Set<List<ASTCDCompilationUnit>> computeAllPermutations(
      List<ASTCDCompilationUnit> inputSet) {
    Set<List<ASTCDCompilationUnit>> permutations = new LinkedHashSet<>();
    if (inputSet.isEmpty() || inputSet.size() == 1) {
      permutations.add(inputSet);
      return permutations;
    }
    for (ASTCDCompilationUnit input : inputSet) {
      List<ASTCDCompilationUnit> remainder = new ArrayList<>(inputSet);
      remainder.remove(input);
      for (List<ASTCDCompilationUnit> rPermutation : computeAllPermutations(remainder)) {
        for (int i = 0; i < inputSet.size(); i++) {
          ArrayList<ASTCDCompilationUnit> permutation = new ArrayList<>();
          if (i < rPermutation.size()) {
            permutation.addAll(rPermutation.subList(i, rPermutation.size()));
          }
          permutation.add(input);
          if (i > 0 && i <= rPermutation.size()) {
            permutation.addAll(rPermutation.subList(0, i));
          }
          permutations.add(permutation);
        }
      }
    }
    return permutations;
  }
  
  // Testet eine andere Reihenfolge der Eingabe-CDs (mit Kommentaren)
  @Test
  public void testCarRental_2() {
    final String srcDir = "src/test/resources/class_diagrams/carrental/";
    List<ASTCDCompilationUnit> inputSet = new ArrayList<>();
    ASTCDCompilationUnit expected = null;
    try {
      inputSet.add(loadModel(srcDir + "Trucks.cd"));
      inputSet.add(loadModel(srcDir + "Cars.cd"));
      inputSet.add(loadModel(srcDir + "Renting.cd"));
    }
    catch (IOException e) {
      fail("IO exception while accessing input models: " + e.getMessage());
    }
    
    LinkedHashSet<MergeParameter> params = new LinkedHashSet<>();
    
    params.add(MergeParameter.LOG_VERBOSE);
    params.add(MergeParameter.LOG_TO_CONSOLE);
    
    ASTCDCompilationUnit mergedCD = CDMerge.merge(inputSet, "CarRental", params);
    
    assertNotNull(mergedCD);
  }
  
  @Test
  public void testParseMrgConfig() {
    final String file = "src/test/resources/json/mrg-config.json";
    assertTrue(CDMerge.parseMrgConfig(file).containsAll(Set.of(MergeParameter.MERGE_COMMENTS,
        MergeParameter.MERGE_ONLY_NAMED_ASSOCIATIONS)));
  }
  
}
