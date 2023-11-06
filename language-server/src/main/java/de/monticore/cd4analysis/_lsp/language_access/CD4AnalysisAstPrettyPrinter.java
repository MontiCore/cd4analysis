package de.monticore.cd4analysis._lsp.language_access;

import de.monticore.cd4analysis._prettyprint.CD4AnalysisFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.prettyprint.AstPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class CD4AnalysisAstPrettyPrinter implements AstPrettyPrinter<ASTCDCompilationUnit> {
  private final CD4AnalysisFullPrettyPrinter prettyPrinter =
      new CD4AnalysisFullPrettyPrinter(new IndentPrinter());

  @Override
  public String prettyPrint(ASTCDCompilationUnit node) {
    return prettyPrinter.prettyprint(node);
  }
}
