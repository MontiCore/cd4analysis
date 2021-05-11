/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd;

import com.google.common.base.Joiner;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * The base class for the tests, to provide common functionality
 */
public class TestBasis {

  public final static String PATH = "src/test/resources/de/monticore/";

  /**
   * have a temporary folder for the tests
   */
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @BeforeClass
  public static void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  public String getTmpAbsolutePath() {
    return folder.getRoot().getAbsolutePath();
  }

  public String getTmpFilePath(String fileName) {
    return getTmpAbsolutePath() + File.separator + fileName;
  }

  protected boolean modelFileExists(String fileName) {
    Path filePath = Paths.get(fileName);
    return Files.exists(filePath);
  }

  public static String getFilePath(String path) {
    return Paths.get(PATH + path).toString();
  }

  public static String getJoinedErrors() {
    return Joiner.on("\n").join(Log.getFindings());
  }

  public static void checkNullAndPresence(MCConcreteParser parser, @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<? extends ASTNode> node) {
    final String joinedErrors = getJoinedErrors();
    final boolean hasErrors = parser.hasErrors();
    parser.setError(false);
    assertFalse(joinedErrors, hasErrors);
    assertNotNull("The node should not be null", node);
    assertTrue(node.isPresent());
    checkLogError();
  }

  public static void checkLogError() {
    if (Log.getErrorCount() > 0) {
      final String joinedErrors = getJoinedErrors();
      Log.getFindings().clear();
      fail("Following errors occured: \n" + joinedErrors);
    }
  }

  public static void expectErrorCount(int i, List<String> listOfErrors) {
    if (Log.getErrorCount() == 0) {
      if (i == 0) {
        return;
      }
      else {
        fail("exptected " + i + " errors, but none were present");
      }
    }

    assertEquals("exptected to get exaclty " + i + " errors, the errors where:\n" + getJoinedErrors(), Log.getErrorCount(), i);
    final List<Finding> findings = Log.getFindings();
    IntStream.range(0, i).forEach(c -> assertEquals(listOfErrors.get(c), findings.get(c).toString()));
    Log.getFindings().clear();
  }

  @Before
  public void before() {
    Log.getFindings().clear();
  }

  @After
  public void after() {
    checkLogError();
  }

  protected ICD4AnalysisArtifactScope createST(ASTCDCompilationUnit astcdCompilationUnit) {
    final ICD4AnalysisArtifactScope st = CD4AnalysisMill.scopesGenitorDelegator().createFromAST(astcdCompilationUnit);
    checkLogError();
    return st;
  }
}
