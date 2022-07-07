package de.monticore.cddiff.syntaxdiff;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.syntaxdiff.SyntaxDiff;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SyntaxDemoTest extends CDDiffTestBasis {

  ASTCDCompilationUnit cd1 = parseModel("src/cddifftest/resources/de/monticore/cddiff/syntaxdiff/CDSynExample1.cd");
  ASTCDCompilationUnit cd2 = parseModel("src/cddifftest/resources/de/monticore/cddiff/syntaxdiff/CDSynExample2.cd");


  @Override
  protected ASTCDCompilationUnit parseModel(String modelFile) {
    Path model = Paths.get(modelFile);
    CD4CodeParser parser = new CD4CodeParser();
    Optional<ASTCDCompilationUnit> optAutomaton;
    try {
      optAutomaton = parser.parse(model.toString());
      //assertFalse(parser.hasErrors());
      assertTrue(optAutomaton.isPresent());

      return optAutomaton.get();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("There was an exception when parsing the model " + modelFile + ": " + e.getMessage());
    }

    return null;
  }

  @Before
  public void buildSymTable(){
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
    new CD4CodeDirectCompositionTrafo().transform(cd1);
    new CD4CodeDirectCompositionTrafo().transform(cd2);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd1);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd2);
  }

  @Test
  public void syntaxDemoTest() {
    SyntaxDiff syntaxDiff = new SyntaxDiff (cd1,cd2);
    syntaxDiff.print();
  }
}
