/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cd4analysis.prettyprint.CD4AnalysisFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.log.MCLoggerWrapper;
import de.monticore.cdmerge.merging.mergeresult.MergeResult;
import de.monticore.cdmerge.merging.mergeresult.MergeStepResult;
import de.monticore.cdmerge.util.CDUtils;
import de.monticore.prettyprint.IndentPrinter;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class BaseTest {

  /**
   * Simple switch to print merged CD from tests always to Standard Output for inspection, default
   * false
   */
  private final static boolean PRINTOUT_CD = true;

  /**
   * Simple switch to print detailled merge log from tests always to Standard Output for
   * inspection,
   * default false
   */
  private final static boolean PRINTOUT_TRACE = false;

  protected final static String MODEL_PATH = "src/cdmergetest/resources/class_diagrams";

  protected final CD4AnalysisParser parser;

  protected ICD4AnalysisGlobalScope globalScope;

  @BeforeClass
  public static void init() {

  }

  public BaseTest() {
    parser = new CD4AnalysisParser();
  }

  @Before
  public void initBefore() {
    MCLoggerWrapper.init(ErrorLevel.WARNING, true);
    CD4AnalysisMill.reset();
    CD4AnalysisMill.init();
    globalScope = CD4AnalysisMill.globalScope();
    BuiltInTypes.addBuiltInTypes(globalScope);
  }

  protected ASTCDCompilationUnit loadModel(String filename) throws IOException {
    //We need an empty symbol table as we load models that contain the same symbols
    Optional<ASTCDCompilationUnit> ast = CDUtils.parseCDFile(filename, false);
    if (ast.isPresent()) {
      return ast.get();
    }
    else {
      throw new RuntimeException("Unable to parse cd model");
    }
  }

  protected ASTCDCompilationUnit loadModel(Path cdFile) throws IOException {
    return loadModel(cdFile.toString());
  }

  protected ASTCDCompilationUnit parseCD(String cd) {

    Optional<ASTCDCompilationUnit> ast = CDUtils.parseCDCompilationUnit(cd, false);
    if (ast.isPresent()) {
      return ast.get();
    }
    else {
      throw new RuntimeException("Unable to parse cd model");
    }
  }

  protected String prettyPrint(ASTCDCompilationUnit cd) {
    IndentPrinter i = new IndentPrinter();
    CD4AnalysisFullPrettyPrinter prettyprinter = new CD4AnalysisFullPrettyPrinter(i);
    return prettyprinter.prettyprint(cd);
  }

  protected void processResult(MergeStepResult result) {
    if (PRINTOUT_CD | getSystemProperty("test.printout.result").equals("1")) {
      if (result.isSuccessful()) {
        System.err.println(prettyPrint(result.getMergedCD()));
      }
    }
    if (PRINTOUT_TRACE | getSystemProperty("test.printout.trace").equals("1")) {
      result.getMergeLog()
          .getAllLogs(false)
          .forEach(log -> System.out.println(log.toStringWithTimeStamp()));
    }

  }

  @SuppressWarnings("unused")
  protected void processResult(MergeResult result) {
    if (!PRINTOUT_CD && !getSystemProperty("test.printout.result").equals("1") && !PRINTOUT_TRACE
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
    }
    else if (result.getIntermediateResults().size() == 1) {
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
