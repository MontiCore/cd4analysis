/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCreatorDelegator;
import de.monticore.cd4code.cocos.CD4CodeCoCos;
import de.monticore.cd4code.prettyprint.CD4CodePrettyPrinter;
import de.monticore.io.paths.ModelPath;

import java.nio.file.Paths;

public class CD4CodeTestBasis extends TestBasis {
  protected final CD4CodeParser p = new CD4CodeParser();
  protected final CD4CodeGlobalScope globalScope = CD4CodeMill
      .cD4CodeGlobalScopeBuilder()
      .setModelPath(new ModelPath(Paths.get(PATH)))
      .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
      .addBuiltInTypes()
      .build();
  protected final CD4CodeSymbolTableCreatorDelegator symbolTableCreator = CD4CodeMill
      .cD4CodeSymbolTableCreatorDelegatorBuilder()
      .setGlobalScope(globalScope)
      .build();
  protected final CD4CodeCoCos cd4CodeCoCos = new CD4CodeCoCos();
  protected final CD4CodePrettyPrinter printer = CD4CodeMill.cD4CodePrettyPrinter();
}
