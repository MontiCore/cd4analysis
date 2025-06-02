/* (c) https://github.com/MontiCore/monticore */
package de.monticore.trafo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.tf.MoveOptionalAttribute;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Created by
 *
 * @author KH
 */
public class MoveOptionalAttributeTest {
  
  @BeforeAll
  public static void init() {
    CD4CodeMill.init();
  }
  
  @Test
  public void testBothFound() throws IOException {
    String input = "src/test/resources/de/monticore/trafo/MoveAttrAB.cd";
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse(input);
    
    assertTrue(ast.isPresent());
    
    MoveOptionalAttribute moveAttr = new MoveOptionalAttribute(ast.get());
    
    assertTrue(moveAttr.doPatternMatching());
    moveAttr.doReplacement();
    
    assertEquals(2, ast.get().getCDDefinition().getCDClassesList().size());
    assertEquals("A", ast.get().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(0, ast.get().getCDDefinition().getCDClassesList().get(0).getCDAttributeList()
        .size());
    assertEquals("B", ast.get().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(1, ast.get().getCDDefinition().getCDClassesList().get(1).getCDAttributeList()
        .size());
    assertEquals("foo", ast.get().getCDDefinition().getCDClassesList().get(1).getCDAttributeList()
        .get(0).getName());
  }
  
  @Test
  public void testOnlyAFound() throws IOException {
    String input = "src/test/resources/de/monticore/trafo/MoveAttrA.cd";
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse(input);
    
    assertTrue(ast.isPresent());
    
    MoveOptionalAttribute moveAttr = new MoveOptionalAttribute(ast.get());
    
    assertTrue(moveAttr.doPatternMatching());
    moveAttr.doReplacement();
    
    assertEquals(1, ast.get().getCDDefinition().getCDClassesList().size());
    assertEquals("A", ast.get().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(0, ast.get().getCDDefinition().getCDClassesList().get(0).getCDAttributeList()
        .size());
  }
  
  @Test
  public void testOnlyBFound() throws IOException {
    String input = "src/test/resources/de/monticore/trafo/MoveAttrB.cd";
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse(input);
    
    assertTrue(ast.isPresent());
    
    MoveOptionalAttribute moveAttr = new MoveOptionalAttribute(ast.get());
    
    assertTrue(moveAttr.doPatternMatching());
    moveAttr.doReplacement();
    
    assertEquals(1, ast.get().getCDDefinition().getCDClassesList().size());
    assertEquals("B", ast.get().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(0, ast.get().getCDDefinition().getCDClassesList().get(0).getCDAttributeList()
        .size());
  }
  
}
