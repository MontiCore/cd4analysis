/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.log.MCLoggerWrapper;
import de.monticore.cdmerge.merging.mergeresult.MergeResult;
import de.monticore.cdmerge.merging.mergeresult.MergeStepResult;
import de.monticore.cdmerge.util.CDMergeUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.Before;
import org.junit.BeforeClass;

public class BaseTest {

  /**
   * Simple switch to print merged CD from tests always to Standard Output for inspection, default
   * false
   */
  private static final boolean PRINTOUT_CD = true;

  /**
   * Simple switch to print detailled merge log from tests always to Standard Output for inspection,
   * default false
   */
  private static final boolean PRINTOUT_TRACE = false;

  protected static final String MODEL_PATH = "src/test/resources/class_diagrams";

  protected final CD4CodeParser parser;

  protected ICD4CodeGlobalScope globalScope;

  @BeforeClass
  public static void init() {
    MCLoggerWrapper.init(ErrorLevel.WARNING, true);
  }

  public BaseTest() {
    parser = CD4CodeMill.parser();
  }

  @Before
  public void initBefore() {
    CD4CodeMill.reset();
    CD4CodeMill.init();
    globalScope = CD4CodeMill.globalScope();
    BuiltInTypes.addBuiltInTypes(globalScope);
  }

  protected ASTCDCompilationUnit loadModel(String filename) throws IOException {
    // We need an empty symbol table as we load models that contain the same symbols
    Optional<ASTCDCompilationUnit> ast = CDMergeUtils.parseCDFile(filename, false);
    if (ast.isPresent()) {
      return ast.get();
    } else {
      throw new RuntimeException("Unable to parse cd model");
    }
  }

  protected ASTCDCompilationUnit loadModel(Path cdFile) throws IOException {
    return loadModel(cdFile.toString());
  }

  protected ASTCDCompilationUnit parseCD(String cd) {

    Optional<ASTCDCompilationUnit> ast = CDMergeUtils.parseCDCompilationUnit(cd, false);
    if (ast.isPresent()) {
      return ast.get();
    } else {
      throw new RuntimeException("Unable to parse cd model");
    }
  }

  protected String prettyPrint(ASTCDCompilationUnit cd) {
    return CD4CodeMill.prettyPrint(cd, true);
  }

  protected void processResult(MergeStepResult result) {
    if (PRINTOUT_CD | getSystemProperty("test.printout.result").equals("1")) {
      if (result.isSuccessful()) {
        System.err.println(prettyPrint(result.getMergedCD()));
      }
    }
    if (PRINTOUT_TRACE | getSystemProperty("test.printout.trace").equals("1")) {
      result
          .getMergeLog()
          .getAllLogs(false)
          .forEach(log -> System.out.println(log.toStringWithTimeStamp()));
    }
  }

  @SuppressWarnings("unused")
  protected void processResult(MergeResult result) {
    if (!PRINTOUT_CD
        && !getSystemProperty("test.printout.result").equals("1")
        && !PRINTOUT_TRACE
        && !getSystemProperty("test.printout.trace").equals("1")) {
      return;
    }
    int i = 1;
    if (result.getIntermediateResults().size() > 1) {
      for (MergeStepResult step : result.getIntermediateResults()) {
        System.out.println(">>>>  STEP " + i + " >>>>");
        processResult(step);
        System.out.println();
      }
    } else if (result.getIntermediateResults().size() == 1) {
      processResult(result.getIntermediateResults().get(0));
    }
  }

  protected CDMergeConfig.Builder getConfigBuilder() {
    CDMergeConfig.Builder builder = new CDMergeConfig.Builder(false);
    if (PRINTOUT_TRACE | getSystemProperty("test.verbose").equals("1")) {
      builder.withParam(MergeParameter.LOG_VERBOSE);
    }
    if (PRINTOUT_TRACE | getSystemProperty("test.printout.trace").equals("1")) {
      builder.withParam(MergeParameter.LOG_TO_CONSOLE);
    }

    return builder;
  }

  private static String getSystemProperty(String propertyName) {
    if (System.getProperty(propertyName) == null) {
      return "";
    }
    return System.getProperty(propertyName);
  }
}
