/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.integrationtest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.base.Preconditions;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.BaseTest;
import de.monticore.cdmerge.MergeTool;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.merging.mergeresult.MergeResult;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class StereotypeMergeTest extends BaseTest {
  
  private static final String INPUT_MODEL_DIR = "src/test/resources/class_diagrams";
  
  // A stereo
  private static final String INPUT_MODEL_A = INPUT_MODEL_DIR + "/Stereotypes/CD1.cd";
  
  // B stereo
  private static final String INPUT_MODEL_B = INPUT_MODEL_DIR + "/Stereotypes/CD2.cd";
  
  // No stereos
  private static final String INPUT_MODEL_NO = INPUT_MODEL_DIR + "/Stereotypes/CD3.cd";
  
  // multiple stereos
  private static final String INPUT_MODEL_MULT = INPUT_MODEL_DIR + "/Stereotypes/CD4.cd";
  
  @Test
  public void testMergeWithOneSide() throws IOException, MergingException {
    // Test merge (A, No_stereos): If all stereos of A are present in the result
    List<String> inputModels = new ArrayList<>();
    inputModels.add(INPUT_MODEL_A);
    inputModels.add(INPUT_MODEL_NO);
    final MergeTool cdMerger = new MergeTool(getConfig(inputModels));
    MergeResult results = cdMerger.mergeCDs();
    processResult(results);
    
    assertTrue(results.mergeSuccess());
    assertTrue(results.getMergedCD().isPresent());
    
    ASTCDDefinition def = results.getMergedCD().get().getCDDefinition();
    assertTrue(def.getModifier().isPresentStereotype(), "CDDefinition stereo missing");
    assertTrue(def.getModifier().getStereotype().contains("A1"), "CDDefinition stereo incorrect");
    ASTCDClass cl = def.getCDClassesList().get(0);
    assertTrue(cl.getModifier().isPresentStereotype(), "Class stereo missing");
    assertTrue(cl.getModifier().getStereotype().contains("A2"), "Class stereo incorrect");
    ASTCDAttribute attr_a = cl.getCDAttributeList().stream().filter(a -> a.getName().equals("cd1"))
        .findAny().get();
    ASTCDAttribute attr_no = cl.getCDAttributeList().stream().filter(a -> a.getName().equals("cd3"))
        .findAny().get();
    assertTrue(attr_a.getModifier().isPresentStereotype(), "Attribute stereo missing");
    assertTrue(attr_a.getModifier().getStereotype().contains("A3"), "Attribute stereo incorrect");
    assertFalse(attr_no.getModifier().isPresentStereotype(), "Attribute stereo not missing");
  }
  
  @Test
  public void testMergeWithOtherSide() throws IOException, MergingException {
    // Test merge (No_stereos, A): If all stereos of A are present in the result
    List<String> inputModels = new ArrayList<>();
    inputModels.add(INPUT_MODEL_NO);
    inputModels.add(INPUT_MODEL_A);
    final MergeTool cdMerger = new MergeTool(getConfig(inputModels));
    MergeResult results = cdMerger.mergeCDs();
    processResult(results);
    
    assertTrue(results.mergeSuccess());
    assertTrue(results.getMergedCD().isPresent());
    
    ASTCDDefinition def = results.getMergedCD().get().getCDDefinition();
    assertTrue(def.getModifier().isPresentStereotype(), "CDDefinition stereo missing");
    assertTrue(def.getModifier().getStereotype().contains("A1"), "CDDefinition stereo incorrect");
    ASTCDClass cl = def.getCDClassesList().get(0);
    assertTrue(cl.getModifier().isPresentStereotype(), "Class stereo missing");
    assertTrue(cl.getModifier().getStereotype().contains("A2"), "Class stereo incorrect");
    ASTCDAttribute attr_a = cl.getCDAttributeList().stream().filter(a -> a.getName().equals("cd1"))
        .findAny().get();
    ASTCDAttribute attr_no = cl.getCDAttributeList().stream().filter(a -> a.getName().equals("cd3"))
        .findAny().get();
    assertTrue(attr_a.getModifier().isPresentStereotype(), "Attribute stereo missing");
    assertTrue(attr_a.getModifier().getStereotype().contains("A3"), "Attribute stereo incorrect");
    assertFalse(attr_no.getModifier().isPresentStereotype(), "Attribute stereo not missing");
  }
  
  @Test
  public void testMergeWithBothSides() throws IOException, MergingException {
    // Test merge (A, B): If all stereos of A and B are present in the merged result
    List<String> inputModels = new ArrayList<>();
    inputModels.add(INPUT_MODEL_A);
    inputModels.add(INPUT_MODEL_B);
    final MergeTool cdMerger = new MergeTool(getConfig(inputModels));
    MergeResult results = cdMerger.mergeCDs();
    processResult(results);
    
    assertTrue(results.mergeSuccess());
    assertTrue(results.getMergedCD().isPresent());
    
    ASTCDDefinition def = results.getMergedCD().get().getCDDefinition();
    assertTrue(def.getModifier().isPresentStereotype(), "CDDefinition stereo missing");
    assertTrue(def.getModifier().getStereotype().contains("A1"), "CDDefinition stereo incorrect");
    assertTrue(def.getModifier().getStereotype().contains("B1"), "CDDefinition stereo incorrect");
    ASTCDClass cl = def.getCDClassesList().get(0);
    assertTrue(cl.getModifier().isPresentStereotype(), "Class stereo missing");
    assertTrue(cl.getModifier().getStereotype().contains("A2"), "Class stereo incorrect");
    assertTrue(cl.getModifier().getStereotype().contains("B2"), "Class stereo incorrect");
    assertTrue(((ASTCDAttribute) cl.getCDMember(0)).getModifier().isPresentStereotype(),
        "Attribute stereo missing");
    ASTCDAttribute attr_a = cl.getCDAttributeList().stream().filter(a -> a.getName().equals("cd1"))
        .findAny().get();
    ASTCDAttribute attr_b = cl.getCDAttributeList().stream().filter(a -> a.getName().equals("cd2"))
        .findAny().get();
    assertTrue(attr_a.getModifier().isPresentStereotype(), "Attribute stereo missing");
    assertTrue(attr_a.getModifier().getStereotype().contains("A3"), "Attribute stereo incorrect");
    assertTrue(attr_b.getModifier().isPresentStereotype(), "Attribute stereo missing");
    assertTrue(attr_b.getModifier().getStereotype().contains("B3"), "Attribute stereo incorrect");
  }
  
  @Test
  public void testMergeWithBothSidesMult() throws IOException, MergingException {
    // Same as above, just that merge(A, MULT) where mult has multiple stereos
    List<String> inputModels = new ArrayList<>();
    inputModels.add(INPUT_MODEL_A);
    inputModels.add(INPUT_MODEL_MULT);
    final MergeTool cdMerger = new MergeTool(getConfig(inputModels));
    MergeResult results = cdMerger.mergeCDs();
    processResult(results);
    
    assertTrue(results.mergeSuccess());
    assertTrue(results.getMergedCD().isPresent());
    
    ASTCDDefinition def = results.getMergedCD().get().getCDDefinition();
    assertTrue(def.getModifier().isPresentStereotype(), "CDDefinition stereo missing");
    assertTrue(def.getModifier().getStereotype().contains("A1"), "CDDefinition stereo incorrect");
    assertTrue(def.getModifier().getStereotype().contains("D1"), "CDDefinition stereo incorrect");
    assertTrue(def.getModifier().getStereotype().contains("DD1"), "CDDefinition stereo incorrect");
    ASTCDClass cl = def.getCDClassesList().get(0);
    assertTrue(cl.getModifier().isPresentStereotype(), "Class stereo missing");
    assertTrue(cl.getModifier().getStereotype().contains("A2"), "Class stereo incorrect");
    assertTrue(cl.getModifier().getStereotype().contains("D2"), "Class stereo incorrect");
    assertTrue(cl.getModifier().getStereotype().contains("DD2"), "Class stereo incorrect");
    ASTCDAttribute attr_a = cl.getCDAttributeList().stream().filter(a -> a.getName().equals("cd1"))
        .findAny().get();
    ASTCDAttribute attr_mult = cl.getCDAttributeList().stream().filter(a -> a.getName().equals(
        "cd4")).findAny().get();
    assertTrue(attr_a.getModifier().isPresentStereotype(), "Attribute stereo missing");
    assertTrue(attr_a.getModifier().getStereotype().contains("A3"), "Attribute stereo incorrect");
    assertTrue(attr_mult.getModifier().isPresentStereotype(), "Attribute stereo missing");
    assertTrue(attr_mult.getModifier().getStereotype().contains("D3"),
        "Attribute stereo incorrect");
    assertTrue(attr_mult.getModifier().getStereotype().contains("DD3"),
        "Attribute stereo incorrect");
  }
  
  private CDMergeConfig getConfig(List<String> inputModels) throws IOException {
    CDMergeConfig.Builder builder = getConfigBuilder().withParam(MergeParameter.CHECK_ONLY,
        MergeParameter.ON).withParam(MergeParameter.FAIL_FAST).withParam(MergeParameter.OUTPUT_NAME,
            "mergedCD");
    for (String m : inputModels) {
      Preconditions.checkNotNull(loadModel(Paths.get(m)));
      builder.addInputFile(m);
    }
    return builder.build();
  }
  
}
