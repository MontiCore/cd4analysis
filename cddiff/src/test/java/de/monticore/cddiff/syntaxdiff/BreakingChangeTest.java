/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syntaxdiff;

import static org.junit.Assert.*;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BreakingChangeTest extends CDDiffTestBasis {

  protected final ASTCDCompilationUnit cd1 =
      parseModel("src/test/resources/de/monticore/cddiff/syntaxdiff/BreakingChange1.cd");

  protected final ASTCDCompilationUnit cd2 =
      parseModel("src/test/resources/de/monticore/cddiff/syntaxdiff/BreakingChange2.cd");

  @Override
  protected ASTCDCompilationUnit parseModel(String modelFile) {
    Path model = Paths.get(modelFile);
    CD4CodeParser parser = new CD4CodeParser();
    Optional<ASTCDCompilationUnit> optAutomaton;
    try {
      optAutomaton = parser.parse(model.toString());
      // assertFalse(parser.hasErrors());
      assertTrue(optAutomaton.isPresent());

      return optAutomaton.get();
    } catch (Exception e) {
      e.printStackTrace();
      fail("There was an exception when parsing the model " + modelFile + ": " + e.getMessage());
    }

    return null;
  }

  @Before
  public void buildSymTable() {
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
    new CD4CodeDirectCompositionTrafo().transform(cd1);
    new CD4CodeDirectCompositionTrafo().transform(cd2);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd1);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd2);
  }

  @Test
  public void testScore() {
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(cd1, cd2);
    List<CDAssociationDiff> matchedAssos = syntaxDiff.getMatchedAssos();

    Assert.assertTrue(
        matchedAssos
            .get(0)
            .getInterpretationList()
            .contains(CDSyntaxDiff.Interpretation.BREAKINGCHANGE));
    Assert.assertFalse(
        matchedAssos
            .get(1)
            .getInterpretationList()
            .contains(CDSyntaxDiff.Interpretation.BREAKINGCHANGE));
  }
}
