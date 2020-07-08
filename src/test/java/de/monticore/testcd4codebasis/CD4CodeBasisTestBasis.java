/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcd4codebasis;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisGlobalScope;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCreatorDelegator;
import de.monticore.cd4codebasis.cocos.CD4CodeBasisCoCos;
import de.monticore.io.paths.ModelPath;
import de.monticore.testcd4codebasis._parser.TestCD4CodeBasisParser;

import java.nio.file.Paths;

public class CD4CodeBasisTestBasis extends TestBasis {
  protected final TestCD4CodeBasisParser p = new TestCD4CodeBasisParser();
  protected final CD4CodeBasisGlobalScope globalScope = CD4CodeBasisMill
      .cD4CodeBasisGlobalScopeBuilder()
      .setModelPath(new ModelPath(Paths.get(PATH)))
      .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
      .build();
  protected final CD4CodeBasisSymbolTableCreatorDelegator symbolTableCreator = CD4CodeBasisMill
      .cD4CodeBasisSymbolTableCreatorDelegatorBuilder()
      .setGlobalScope(globalScope)
      .build();
  protected final CD4CodeBasisCoCos cdCD4CodeBasisCoCos = new CD4CodeBasisCoCos();
}
