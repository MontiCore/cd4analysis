/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cdassociation.cocos.CDAssociationCoCos;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.MCPath;
import de.monticore.testcdassociation._parser.TestCDAssociationParser;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.fail;

public class CDAssociationTestBasis extends TestBasis {
  protected TestCDAssociationParser p;
  protected CDAssociationCoCos cdAssociationCoCos;

  @Before
  public void initObjects() {
    CD4AnalysisMill.reset();
    CD4AnalysisMill.init();
    p = new TestCDAssociationParser();

    final ICD4AnalysisGlobalScope globalScope = CD4AnalysisMill
        .globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));
    if (globalScope instanceof CD4AnalysisGlobalScope) {
      ((CD4AnalysisGlobalScope) globalScope).addBuiltInTypes();
    }

    cdAssociationCoCos = new CDAssociationCoCos();
  }

  protected ASTCDCompilationUnit parseModel(String modelName) {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit;
    try {
      astcdCompilationUnit = p.parseCDCompilationUnit(getFilePath(modelName));
    }
    catch (IOException e) {
      fail("Failed while parsing the model `" + getFilePath(modelName) + "': " + e.getMessage());
      return null;
    }

    checkNullAndPresence(p, astcdCompilationUnit);
    return astcdCompilationUnit.get();
  }

}
