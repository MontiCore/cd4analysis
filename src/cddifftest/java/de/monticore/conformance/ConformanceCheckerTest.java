package de.monticore.conformance;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConformanceCheckerTest {

  public static final String dir = "src/cddifftest/resources/de/monticore/conformance/";

  @Before
  public void setup() {
    Log.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

  @Test
  public void testConformanceCheck() {
    try {
      Optional<ASTCDCompilationUnit> conCD =
          CD4CodeMill.parser().parse(dir + "Concrete" + ".cd");
      Optional<ASTCDCompilationUnit> refCD = CD4CodeMill.parser().parse(dir + "Reference" + ".cd");
      if (conCD.isPresent() && refCD.isPresent()) {
        CD4CodeMill.scopesGenitorDelegator().createFromAST(conCD.get());
        CD4CodeMill.scopesGenitorDelegator().createFromAST(refCD.get());
        conCD.get().accept(new CD4CodeSymbolTableCompleter(conCD.get()).getTraverser());
        refCD.get().accept(new CD4CodeSymbolTableCompleter(refCD.get()).getTraverser());

        Assert.assertTrue(
            ConformanceChecker.checkBasicStereotypeConformance(conCD.get(), refCD.get(), Set.of(
                "ref")));
      } else {
        Assert.fail("Could not parse CDs.");
      }

    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }
  }
}
