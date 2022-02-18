package de.monticore.cddiff.preprocessing;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.AbstractTest;
import de.monticore.preprocessing.OpenWorldPreProcessor;
import net.sourceforge.plantuml.Log;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PreProcessorTest extends AbstractTest {

  ASTCDCompilationUnit m1Ast = parseModel("src/cddifftest/resources/de/monticore/cddiff/Manager/cd2v1.cd");

  ASTCDCompilationUnit m2Ast = parseModel("src/cddifftest/resources/de/monticore/cddiff/Manager/cd2v2.cd");

  @Test
  public void testOpenWorldPreProcessing(){
    OpenWorldPreProcessor preProcessor = new OpenWorldPreProcessor();
    String res = preProcessor.completeCDs(m1Ast,m2Ast);
    Log.info(res);
    assertEquals("de.monticore.Task", res);
    //todo: test actual pre-processing
  }

}
