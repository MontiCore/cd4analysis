package de.monticore.coevolution;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.syntaxdiff.CDSyntaxDiff;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PrototypeTest extends CoEvoTestBasis{
  ASTCDCompilationUnit cd1 = parseModel("src/coevolutiontest/resources/models/Driving1.cd");
  ASTCDCompilationUnit cd2 = parseModel("src/coevolutiontest/resources/models/Driving2.cd");

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
  public void hwcUpdaterDemoTest() {

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(cd2, cd1);
    String pathInput = "src/coevolutiontest/resources/de.monticore.coevolution/hwcdri1";
    String pathOutput = "target/generated/co-evolution-test";

    HWCUpdater updater = new HWCUpdater(syntaxDiff,pathInput,pathOutput);

    try {
      updater.hwcupdater();
    }
    catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }
}
