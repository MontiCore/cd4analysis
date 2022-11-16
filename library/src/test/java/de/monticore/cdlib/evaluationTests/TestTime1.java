/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.evaluationTests;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdlib.refactorings.ExtractIntermediateClass;
import de.monticore.cdlib.utilities.FileUtility;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Date;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Check Executiontime for Testcase 1
 *
 * <p>Created by
 *
 * @author KE
 */
public class TestTime1 {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    CD4CodeMill.init();
    ReportManager.ReportManagerFactory factory =
        new ReportManager.ReportManagerFactory() {
          @Override
          public ReportManager provide(String modelName) {
            ReportManager reports = new ReportManager("target/generated-sources");
            TransformationReporter transformationReporter =
                new TransformationReporter(
                    "target/generated-sources",
                    modelName,
                    new ReportingRepository(new ASTNodeIdentHelper()));
            reports.addReportEventHandler(transformationReporter);
            return reports;
          }
        };

    Reporting.init("target/generated-sources", "target/reports", factory);
  }

  @Test
  public void testCase1() throws IOException {

    FileUtility utility = new FileUtility("cdlib/EvaluationCDs/TestCase1");
    ExtractIntermediateClass refactoring = new ExtractIntermediateClass();

    // Perform transformation
    long start = new Date().getTime();
    refactoring.extractAllIntermediateClasses(utility.getAst());
    long end = new Date().getTime();
    long runTime = end - start;
    System.out.println("Executiontime of Testcase 1: " + runTime + " Milliseconds");
  }
}
