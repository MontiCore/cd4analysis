/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.reporting;

import de.monticore.ast.ASTNode;
import de.monticore.cd4analysis._ast.ASTCD4AnalysisNode;
import de.monticore.cd4analysis._od.CD4Analysis2ODDelegator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.Names;

import java.io.File;

public class CD4Analysis2ODReporter extends AReporter {
  private final String modelName;
  private final ReportingRepository reporting;

  public CD4Analysis2ODReporter(String outputDir, String modelName, ReportingRepository reporting) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR + File.separator
            + modelName,
        Names.getSimpleName(modelName) + "_AST", ReportingConstants.OD_FILE_EXTENSION);
    this.modelName = modelName;
    this.reporting = reporting;
  }

  @Override
  protected void writeHeader() {
    writeLine("/*");
    writeLine(" * ========================================================== AST for CD4Analysis");
    writeLine(" */");
  }

  private void writeFooter() {
    writeLine("/*");
    writeLine(" * ========================================================== Explanation");
    writeLine(" * Shows the AST with all attributes as object diagram");
    writeLine(" */");
  }

  @Override
  public void flush(ASTNode ast) {
    writeContent(ast);
    writeFooter();
    super.flush(ast);
  }

  /**
   * @param ast the ASTNode used to write the object diagram
   */
  private void writeContent(ASTNode ast) {
    if (ast instanceof ASTCDCompilationUnit || ast instanceof ASTCDDefinition) {
      ASTCD4AnalysisNode cdNode = (ASTCD4AnalysisNode) ast;
      IndentPrinter pp = new IndentPrinter();
      CD4Analysis2ODDelegator odPrinter = new CD4Analysis2ODDelegator(pp, reporting);
      odPrinter.printObjectDiagram(Names.getSimpleName(modelName) + "_AST", cdNode);
      writeLine(pp.getContent());
    }
  }
}
