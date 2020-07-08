/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4analysis._symboltable.CD4AnalysisScopeDeSer;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCreatorDelegator;
import de.monticore.cd4analysis.cocos.CD4AnalysisCoCos;
import de.monticore.cd4analysis.prettyprint.CD4AnalysisPrettyPrinter;
import de.monticore.io.paths.ModelPath;

import java.nio.file.Paths;

public class CD4AnalysisTestBasis extends TestBasis {
  protected final CD4AnalysisParser p = new CD4AnalysisParser();
  protected final CD4AnalysisGlobalScope globalScope = CD4AnalysisMill
      .cD4AnalysisGlobalScopeBuilder()
      .setModelPath(new ModelPath(Paths.get(PATH)))
      .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
      .addBuiltInTypes()
      .build();
  protected final CD4AnalysisSymbolTableCreatorDelegator symbolTableCreator = CD4AnalysisMill
      .cD4AnalysisSymbolTableCreatorDelegatorBuilder()
      .setGlobalScope(globalScope)
      .build();
  protected final CD4AnalysisCoCos cd4AnalyisCoCos = new CD4AnalysisCoCos();
  protected final CD4AnalysisPrettyPrinter printer = new CD4AnalysisPrettyPrinter();
  protected final CD4AnalysisScopeDeSer deSer = new CD4AnalysisScopeDeSer();
}
