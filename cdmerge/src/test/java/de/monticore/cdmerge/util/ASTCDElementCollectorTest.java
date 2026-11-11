/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.BaseTest;
import java.io.IOException;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ASTCDElementCollectorTest extends BaseTest {
  
  public static final String INPUT_MODEL_FILE = "General/university/Staff.cd";
  
  public ASTCDCompilationUnit cd;
  
  public ASTCDElementCollector testant;
  
  public ASTCDHelper helper;
  
  @BeforeEach
  public void setup() throws IOException {
    this.cd = loadModel(Paths.get(MODEL_PATH, INPUT_MODEL_FILE).toString());
    this.helper = new ASTCDHelper(this.cd);
    this.testant = new ASTCDElementCollector(helper);
  }
  
  @Test
  public void test() {
    
    // The Element collector does not change the result
    
    String oldCD = CD4CodeMill.prettyPrint(cd, false);
    testant.collect(cd);
    assertEquals(oldCD, CD4CodeMill.prettyPrint(cd, false));
  }
  
}
