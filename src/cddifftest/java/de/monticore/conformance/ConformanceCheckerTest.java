package de.monticore.conformance;

import static org.junit.jupiter.api.Assertions.*;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ConformanceCheckerTest {

  public static final String dir = "src/cddifftest/resources/de/monticore/conformance/";

  protected ASTCDCompilationUnit refCD;

  protected ASTCDCompilationUnit conCD;

  @BeforeEach
  public void setup() {
    Log.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

  @Test
  public void testConformanceCheck() {
    parseModels("Concrete.cd", "Reference.cd");
    assertTrue(ConformanceChecker.checkBasicStereotypeConformance(conCD, refCD, Set.of("ref")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"EqName.cd", "STName.cd", "composed.cd"})
  public void testAttributeConformanceValid(String concrete) {
    parseModels("attributes/valid/" + concrete, "attributes/Reference.cd");
    assertTrue(ConformanceChecker.checkBasicComposedConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"DiffName.cd", "DiffType.cd", "NumberAttr.cd"})
  public void testAttributeConformanceInvalid(String concrete) {
    parseModels("attributes/invalid/" + concrete, "attributes/Reference.cd");
    assertFalse(ConformanceChecker.checkBasicComposedConformance(conCD, refCD, "ref"));
  }

  public void parseModels(String concrete, String ref) {
    try {
      Optional<ASTCDCompilationUnit> conCD = CD4CodeMill.parser().parse(dir + concrete);
      Optional<ASTCDCompilationUnit> refCD = CD4CodeMill.parser().parse(dir + ref);
      if (conCD.isPresent() && refCD.isPresent()) {
        CD4CodeMill.scopesGenitorDelegator().createFromAST(conCD.get());
        CD4CodeMill.scopesGenitorDelegator().createFromAST(refCD.get());
        conCD.get().accept(new CD4CodeSymbolTableCompleter(conCD.get()).getTraverser());
        refCD.get().accept(new CD4CodeSymbolTableCompleter(refCD.get()).getTraverser());
        this.refCD = refCD.get();
        this.conCD = conCD.get();
      } else {
        fail("Could not parse CDs.");
      }

    } catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
