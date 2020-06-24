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
import java.nio.file.Paths;
import java.util.Optional;

public class CD4AnalysisDeSerTest extends TestBasis {
  CD4AnalysisParser p = new CD4AnalysisParser();
  CD4AnalysisScopeDeSer deSer = new CD4AnalysisScopeDeSer();

  @Ignore
  @Test
  public void completeModel() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath("cd4analysis/parser/STTest.cd"));
    checkNullAndPresence(p, astcdCompilationUnit);

    final CD4AnalysisGlobalScope globalScope =
        CD4AnalysisMill
            .cD4AnalysisGlobalScopeBuilder()
            .setModelPath(new ModelPath(Paths.get("src/test/resources")))
            .setCD4AnalysisLanguage(new CD4AnalysisLanguage())
            .build();
    final CD4AnalysisSymbolTableCreatorDelegator stCreator =
        CD4AnalysisMill
            .cD4AnalysisSymbolTableCreatorDelegatorBuilder()
            .setGlobalScope(globalScope)
            .build();
    final CD4AnalysisArtifactScope scope = stCreator.createFromAST(astcdCompilationUnit.get());
    System.out.println(deSer.serialize(scope));
  }
}
