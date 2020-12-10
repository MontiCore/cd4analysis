/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcd4codebasis;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCreatorDelegator;
import de.monticore.cd4codebasis._symboltable.ICD4CodeBasisGlobalScope;
import de.monticore.cd4codebasis.cocos.CD4CodeBasisCoCos;
import de.monticore.io.paths.ModelPath;
import de.monticore.testcd4codebasis._parser.TestCD4CodeBasisParser;
import org.junit.Before;

import java.nio.file.Paths;

public class CD4CodeBasisTestBasis extends TestBasis {
  protected TestCD4CodeBasisParser p;
  protected CD4CodeBasisCoCos cdCD4CodeBasisCoCos;

  @Before
  public void initObjects() {
    CD4CodeBasisMill.reset();
    CD4CodeBasisMill.init();
    p = new TestCD4CodeBasisParser();

    final ICD4CodeBasisGlobalScope globalScope = CD4CodeBasisMill
        .cD4CodeBasisGlobalScope();
    globalScope.clear();
    globalScope.setModelPath(new ModelPath(Paths.get(PATH)));
    globalScope.setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION);

    cdCD4CodeBasisCoCos = new CD4CodeBasisCoCos();
  }
}
