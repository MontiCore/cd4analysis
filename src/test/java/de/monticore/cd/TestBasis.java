/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd;

import com.google.common.base.Joiner;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import org.junit.BeforeClass;

import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class TestBasis {

  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }

  /*
    @After
    public void after() {
      checkLogError();
    }
  */
  public String getFilePath(String path) {
    return Paths.get("src/test/resources/de/monticore/cd/" + path).toString();
  }

  protected String getJoinedErrors() {
    return Joiner.on("\n").join(Log.getFindings());
  }

  public void checkNullAndPresents(MCConcreteParser parser, Optional<? extends ASTNode> node) {
    final String joinedErrors = getJoinedErrors();
    Log.getFindings().clear();
    final boolean hasErrors = parser.hasErrors();
    parser.setError(false);
    assertFalse(joinedErrors, hasErrors);
    assertNotNull("The node should not be null", node);
    assertTrue(node.isPresent());
  }

  public void checkNullAndError(MCConcreteParser parser, Optional<? extends ASTNode> node) {
    final String joinedErrors = getJoinedErrors();
    Log.getFindings().clear();
    final boolean hasErrors = parser.hasErrors();
    parser.setError(false);
    assertFalse(joinedErrors, hasErrors);
    assertNotNull("The node should not be null", node);
    assertTrue(node.isPresent());
  }

  public void checkLogError() {
    final String joinedErrors = getJoinedErrors();
    Log.getFindings().clear();
    assertEquals(joinedErrors, 0, Log.getErrorCount());
  }
}
