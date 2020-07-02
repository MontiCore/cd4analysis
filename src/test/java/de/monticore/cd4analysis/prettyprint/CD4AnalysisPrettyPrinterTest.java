/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.prettyprint;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCreatorDelegator;
import de.monticore.cd4analysis.cocos.CD4AnalysisCoCosDelegator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class CD4AnalysisPrettyPrinterTest extends TestBasis {
  CD4AnalysisParser p = new CD4AnalysisParser();
  CD4AnalysisPrettyPrinter printer = new CD4AnalysisPrettyPrinter();

  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cd4analysis/parser/Simple.cd"));

    checkNullAndPresence(p, astcdCompilationUnit);
    String output = printer.prettyprint(astcdCompilationUnit.get());

    final Optional<ASTCDCompilationUnit> astcdCompilationUnitReParsed = p.parse_StringCDCompilationUnit(output);
    checkNullAndPresence(p, astcdCompilationUnitReParsed);
  }

  @Test
  public void completeModelWithSymboltable() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parse(getFilePath("cd4analysis/parser/MyLife.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);
    final ASTCDCompilationUnit node = astcdCompilationUnit.get();

    CD4AnalysisGlobalScope globalScope = CD4AnalysisMill
        .cD4AnalysisGlobalScopeBuilder()
        .setModelPath(new ModelPath(Paths.get(PATH)))
        .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
        .build();
    final CD4AnalysisSymbolTableCreatorDelegator symbolTableCreator = CD4AnalysisMill
        .cD4AnalysisSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(globalScope)
        .build();

    globalScope.addBuiltInTypes();

    symbolTableCreator.createFromAST(node);
    checkLogError();

    new CD4AnalysisCoCosDelegator().getCheckerForAllCoCos().checkAll(node);
  }
}
