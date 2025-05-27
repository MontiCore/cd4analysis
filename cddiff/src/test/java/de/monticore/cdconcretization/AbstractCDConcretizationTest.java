package de.monticore.cdconcretization;

import static de.monticore.cdconformance.CDConfParameter.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconformance.CDConformanceChecker;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractCDConcretizationTest {

  protected static final String TEST_RES_DIR = "src/test/resources/de/monticore/cdconcretization/";

  /**
   * The default conformance parameters that are used for each test case if not specified otherwise.
   */
  protected static final Set<CDConfParameter> DEFAULT_CONFORMANCE_PARAMS =
      Set.of(
          STEREOTYPE_MAPPING,
          NAME_MAPPING,
          SRC_TARGET_ASSOC_MAPPING,
          INHERITANCE,
          ALLOW_CARD_RESTRICTION,
          METHOD_OVERLOADING);

  protected ASTCDCompilationUnit refCD;

  protected ASTCDCompilationUnit conCD;

  protected Set<CDConfParameter> confParameters;

  @BeforeAll
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
  }

  @BeforeEach
  public void setupEach() {
    Log.clearFindings();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
    UnderspecifiedPlaceholderType.addPlaceholderType(CD4CodeMill.globalScope());
    confParameters = new HashSet<>(DEFAULT_CONFORMANCE_PARAMS);
  }

  /***
   * Parses the two models and checks if the concretized CD equals the reference CD.
   * <br>
   * Use this to test if basic completion of model elements works, without any application of
   * explicit incarnation mappings.
   *
   * @param conc the path to the concrete CD
   * @param ref the path to the reference CD
   */
  protected void testConcretizedEqualsRef(String conc, String ref) {
    try {
      parseAndConcretize(conc, ref);
    } catch (CompletionException e) {
      fail("CompletionException", e);
    }
    assertNoFindings("Findings while concretizing CD");

    // to use deep equals, both CDs need to have the same name
    conCD.getCDDefinition().setName(refCD.getCDDefinition().getName());
    assertTrue(conCD.deepEquals(refCD, false));
  }

  /**
   * Parses the two models and checks if the concretized CD conforms to the reference CD. <br>
   * Use this for all non-trivial test cases where the concretization is no longer expected to equal
   * to the reference CD.
   *
   * @param conc the path to the concrete CC
   * @param ref the path to the reference CD
   */
  private void testConcretizedConformsToRef(String conc, String ref) {
    try {
      parseAndConcretize(conc, ref);
    } catch (CompletionException e) {
      fail("CompletionException", e);
    }
    assertNoFindings("Findings while concretizing CD");
    assertTrue(
        new CDConformanceChecker(confParameters).checkConformance(conCD, refCD, Set.of("ref")));
  }

  protected void testConcretizedConformsToRefAndExpectedOut(String conc, String ref, String out) {
    ASTCDCompilationUnit expectedCD = parseCD(out);
    // 1. concretize and check conformance
    testConcretizedConformsToRef(conc, ref);
    // 2. check if concretized CD equals expected output
    assertTrue(conCD.deepEquals(expectedCD, false), "Concretized output does not match expected");
  }

  protected void testConcretizedEqualsExpectedOut(
      ConcretizationCompleter completer, String conc, String ref, String out) {
    ASTCDCompilationUnit expectedCD = parseCD(out);
    // 1. concretize and check conformance
    try {
      parseAndConcretize(completer, conc, ref);
    } catch (CompletionException e) {
      fail("CompletionException", e);
    }
    assertNoFindings("Findings while concretizing CD");
    // 2. check if concretized CD equals expected output
    assertTrue(conCD.deepEquals(expectedCD, false), "Concretized output does not match expected");
  }

  protected void parseAndConcretize(String conc, String ref) throws CompletionException {
    parseAndConcretize(new ConcretizationCompleter("ref", confParameters), conc, ref);
  }

  protected void parseAndConcretize(ConcretizationCompleter completer, String conc, String ref)
      throws CompletionException {
    parseModels(conc, ref);
    completer.completeCD(conCD, refCD);
    System.out.println("Concretized CD:");
    System.out.println(CD4CodeMill.prettyPrint(conCD, false));
  }

  protected void parseModels(String concrete, String ref) {
    this.refCD = parseCD(ref);
    this.conCD = parseCD(concrete);
  }

  protected ASTCDCompilationUnit parseCD(String filePath) {
    ASTCDCompilationUnit cd;
    try {
      cd =
          CD4CodeMill.parser()
              .parseCDCompilationUnit(TEST_RES_DIR + filePath)
              .orElseThrow(() -> new RuntimeException("Could not parse CD: " + filePath));
    } catch (IOException e) {
      throw new RuntimeException("Failed to load CD: " + filePath, e);
    }
    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    cd.accept(new CD4CodeSymbolTableCompleter(cd).getTraverser());
    assertNoFindings("Findings while loading CD");
    return cd;
  }

  // TODO Replace once there is a MontiCore method: MCAssertions#assertNoFindings()
  private static void assertNoFindings(String message) {
    if (!Log.getFindings().isEmpty()) {
      fail(message);
    }
  }
}
