/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeScopeDeSer;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCreatorDelegator;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.cocos.CD4CodeCoCos;
import de.monticore.cd4code.prettyprint.CD4CodePrettyPrinter;
import de.monticore.io.paths.ModelPath;
import org.junit.Before;

import java.nio.file.Paths;

public class CD4CodeTestBasis extends TestBasis {
  protected CD4CodeParser p;
  protected ICD4CodeGlobalScope globalScope;
  protected CD4CodeSymbolTableCreatorDelegator symbolTableCreator;
  protected CD4CodeCoCos cd4CodeCoCos;
  protected CD4CodePrettyPrinter printer;
  protected CD4CodeScopeDeSer deSer;

  @Before
  public void initObjects() {
    CD4CodeMill.init();
    p = new CD4CodeParser();
    globalScope = CD4CodeMill
        .cD4CodeGlobalScopeBuilder()
        .setModelPath(new ModelPath(Paths.get(PATH)))
        .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
        .addBuiltInTypes()
        .build();
    symbolTableCreator = new CD4CodeSymbolTableCreatorDelegator(globalScope);
    cd4CodeCoCos = new CD4CodeCoCos();
    printer = CD4CodeMill.cD4CodePrettyPrinter();
    deSer = new CD4CodeScopeDeSer();
  }
}
