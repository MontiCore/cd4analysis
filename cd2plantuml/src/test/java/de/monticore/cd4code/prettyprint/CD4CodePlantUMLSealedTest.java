/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.prettyprint;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests that the modifiers of sealed and non-sealed types are shown in PlantUML.
 */
public class CD4CodePlantUMLSealedTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    CD4CodeMill.reset();
    CD4CodeMill.init();
  }
  
  @Test
  public void testSealedModifiers() throws IOException {
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse_String(
        "classdiagram C { abstract sealed class S permits A; non-sealed class A extends S; }");
    assertTrue(ast.isPresent(), Log.getFindings().toString());
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast.get());
    
    PlantUMLConfig config = new PlantUMLConfig(true, true, true, true, true);
    String output = new CD4CodePlantUMLFullPrettyPrinter(new PlantUMLPrettyPrintUtil(
        new IndentPrinter(), config)).prettyprint(ast.get());
    
    assertTrue(output.contains("class S << abstract sealed >>"), output);
    assertTrue(output.contains("class A << non-sealed >> extends S"), output);
  }
  
  /**
   * The permits clause has no PlantUML syntax and is not printed.
   */
  @Test
  public void testSealedInterface() throws IOException {
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse_String(
        "classdiagram C { sealed interface I permits A, J; final class A implements I; "
            + "non-sealed interface J extends I; interface K; }");
    assertTrue(ast.isPresent(), Log.getFindings().toString());
    CD4CodeMill.scopesGenitorDelegator().createFromAST(ast.get());
    
    PlantUMLConfig config = new PlantUMLConfig(true, true, true, true, true);
    String output = new CD4CodePlantUMLFullPrettyPrinter(new PlantUMLPrettyPrintUtil(
        new IndentPrinter(), config)).prettyprint(ast.get());
    
    assertTrue(output.contains("interface I << sealed >>\n"), output);
    assertTrue(output.contains("interface J << non-sealed >> extends I\n"), output);
    assertTrue(output.contains("interface K\n"), output);
    assertFalse(output.contains("permits"), output);
  }
  
}
