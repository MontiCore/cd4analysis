/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.reporting;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code._ast.ASTCD4CodeNode;
import de.monticore.cd4code._od.CD4Code2OD;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.Names;

import java.io.File;

public class CD4Code2ODReporter extends AReporter {
  private final String modelName;
  private final ReportingRepository reporting;

  public CD4Code2ODReporter(String outputDir, String modelName, ReportingRepository reporting) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR + File.separator
            + modelName,
        Names.getSimpleName(modelName) + "_AST", ReportingConstants.OD_FILE_EXTENSION);
    this.modelName = modelName;
    this.reporting = reporting;
  }

  @Override
  protected void writeHeader() {
    writeLine("/*");
    writeLine(" * ========================================================== AST for CD4Code");
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
      ASTCD4CodeNode cdNode = (ASTCD4CodeNode) ast;
      IndentPrinter pp = new IndentPrinter();
      CD4Code2OD odPrinter = new CD4Code2OD(pp, reporting);
      odPrinter.printObjectDiagram(Names.getSimpleName(modelName) + "_AST", cdNode);
      writeLine(pp.getContent());
    }
  }
}
