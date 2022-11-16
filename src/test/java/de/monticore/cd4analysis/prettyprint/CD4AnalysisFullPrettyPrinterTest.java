/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis.prettyprint;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cd4analysis.cocos.CD4AnalysisCoCosDelegator;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.io.IOException;
import java.util.Optional;
import org.junit.Test;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CD4AnalysisFullPrettyPrinterTest extends CD4AnalysisTestBasis {

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parseCDCompilationUnit(getFilePath("cd4analysis/parser/Simple.cd"));

    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4AnalysisAfterParseTrafo().transform(node);
    String output = printer.prettyprint(node);

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed =
        p.parse_StringCDCompilationUnit(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);
  }

  @Test
  public void completeModelWithSymboltable() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit =
        p.parse(getFilePath("cd4analysis/parser/MyLife.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();
    new CD4AnalysisAfterParseTrafo().transform(node);

    CD4AnalysisMill.scopesGenitorDelegator().createFromAST(node);
    checkLogError();
    node.accept(new CD4AnalysisSymbolTableCompleter(node).getTraverser());

    new CD4AnalysisCoCosDelegator().getCheckerForAllCoCos().checkAll(node);
  }
}
