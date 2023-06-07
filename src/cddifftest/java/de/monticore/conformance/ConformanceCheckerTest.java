package de.monticore.conformance;

import static de.monticore.conformance.ConfParameter.*;
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

  protected ConformanceChecker checker;

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
    checker = new ConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, Set.of("ref")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"EqName.cd", "STName.cd", "composed.cd"})
  public void testAttributeConformanceValid(String concrete) {
    parseModels("attributes/valid/" + concrete, "attributes/Reference.cd");
    checker = new ConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"DiffName.cd", "DiffType.cd", "NumberAttr.cd"})
  public void testAttributeConformanceInvalid(String concrete) {
    parseModels("attributes/invalid/" + concrete, "attributes/Reference.cd");
    checker = new ConformanceChecker(Set.of(STEREOTYPE_MAPPING, NAME_MAPPING));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"AttrInSuperClasses.cd"})
  public void testDeepAttributeConformanceValid(String concrete) {
    parseModels("attributes/valid/" + concrete, "attributes/Reference.cd");
    checker = new ConformanceChecker(Set.of(INHERITANCE));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"AttrInSuperTypeNoMatch.cd", "AttrInSuperTypeNoMatch.cd"})
  public void testDeepAttributeConformanceInvalid(String concrete) {
    parseModels("attributes/invalid/" + concrete, "attributes/Reference.cd");
    checker = new ConformanceChecker(Set.of(INHERITANCE));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"AssocInSuperType.cd", "InhrBothSides.cd", "Valid1.cd"})
  public void testDeepAssocConformanceValid(String concrete) {
    parseModels("associations/valid/" + concrete, "associations/Reference.cd");
    checker = new ConformanceChecker(Set.of(INHERITANCE));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"FalseDirection.cd"})
  public void testDeepAssocConformanceInvalid(String concrete) {
    parseModels("associations/invalid/" + concrete, "associations/Reference.cd");
    checker = new ConformanceChecker(Set.of(INHERITANCE));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"AssocInSuperType.cd", "InhrBothSides.cd", "Valid1.cd"})
  public void testStrictDeepAssocConformanceValid(String concrete) {
    parseModels("associations/valid/" + concrete, "associations/Reference.cd");
    checker = new ConformanceChecker(Set.of(STRICT_INHERITANCE));
    assertTrue(checker.checkConformance(conCD, refCD, "ref"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"FalseDirection.cd", "inValid1.cd"})
  public void testStrictDeepAssocConformanceInvalid(String concrete) {
    parseModels("associations/invalid/" + concrete, "associations/Reference.cd");
    checker = new ConformanceChecker(Set.of(STRICT_INHERITANCE));
    assertFalse(checker.checkConformance(conCD, refCD, "ref"));
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
