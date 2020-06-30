/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.ModelPath;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class CD4AnalysisDeSerTest extends TestBasis {
  CD4AnalysisParser p = new CD4AnalysisParser();
  CD4AnalysisScopeDeSer deSer = new CD4AnalysisScopeDeSer();

  @Ignore
  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cd4analysis/parser/STTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    CD4AnalysisGlobalScope globalScope = CD4AnalysisMill
        .cD4AnalysisGlobalScopeBuilder()
        .setModelPath(new ModelPath())
        .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
        .build();
    final CD4AnalysisSymbolTableCreator symbolTableCreator = CD4AnalysisMill
        .cD4AnalysisSymbolTableCreatorBuilder()
        .addToScopeStack(globalScope)
        .build();

    final CD4AnalysisArtifactScope scope = symbolTableCreator.createFromAST(astcdCompilationUnit.get());
    System.out.println(deSer.serialize(scope));
  }
}
