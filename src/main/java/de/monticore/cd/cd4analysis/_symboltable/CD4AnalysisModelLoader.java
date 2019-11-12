/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cd4analysis._symboltable;

import java.util.List;

public  class CD4AnalysisModelLoader extends CD4AnalysisModelLoaderTOP {

  public CD4AnalysisModelLoader(CD4AnalysisLanguage language) {
    super(language);
  }

  @Override
  protected void showWarningIfParsedModels(List<?> asts, String modelName) {
    // Do nothing
  }
}
