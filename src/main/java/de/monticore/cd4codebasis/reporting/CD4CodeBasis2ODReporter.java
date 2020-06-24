/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis.reporting;

import de.monticore.ast.ASTNode;
import de.monticore.cd4codebasis._ast.ASTCD4CodeBasisNode;
import de.monticore.cd4codebasis._od.CD4CodeBasis2OD;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.Names;

import java.io.File;

public class CD4CodeBasis2ODReporter extends AReporter {
  private String modelName;
  private ReportingRepository reporting;

  public CD4CodeBasis2ODReporter(String outputDir, String modelName, ReportingRepository reporting) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR + File.separator
            + modelName,
        Names.getSimpleName(modelName) + "_AST", ReportingConstants.OD_FILE_EXTENSION);
    this.modelName = modelName;
    this.reporting = reporting;
  }

  @Override
  protected void writeHeader() {
    writeLine("/*");
    writeLine(" * ========================================================== AST for CD4CodeBasis");
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
      ASTCD4CodeBasisNode cdNode = (ASTCD4CodeBasisNode) ast;
      IndentPrinter pp = new IndentPrinter();
      CD4CodeBasis2OD odPrinter = new CD4CodeBasis2OD(pp, reporting);
      odPrinter.printObjectDiagram(Names.getSimpleName(modelName) + "_AST", cdNode);
      writeLine(pp.getContent());
    }
  }
}
