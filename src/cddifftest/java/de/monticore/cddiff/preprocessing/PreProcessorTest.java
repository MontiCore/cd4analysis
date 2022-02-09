package de.monticore.cddiff.preprocessing;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.AbstractTest;
import de.monticore.preprocessing.OpenWorldPreProcessor;
import org.junit.Test;

public class PreProcessorTest extends AbstractTest {

  ASTCDCompilationUnit m1Ast = parseModel("src/cddifftest/resources/de/monticore/cddiff/Manager/cd2v1.cd");

  ASTCDCompilationUnit m2Ast = parseModel("src/cddifftest/resources/de/monticore/cddiff/Manager/cd2v2.cd");

  @Test
  public void testOpenWorldPreProcessing(){
    OpenWorldPreProcessor preProcessor = new OpenWorldPreProcessor();
    preProcessor.completeCDs(m1Ast,m2Ast);
    //todo: test pre-processing
  }

}
