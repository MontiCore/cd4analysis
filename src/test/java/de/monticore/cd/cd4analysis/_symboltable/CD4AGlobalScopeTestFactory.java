/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cd4analysis._symboltable;

import de.monticore.io.paths.ModelPath;

import java.nio.file.Paths;

public class CD4AGlobalScopeTestFactory {

  public static CD4AnalysisGlobalScope create() {
    final CD4AnalysisLanguage cdLanguage = new CD4AnalysisLanguage();


    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/"));

    return new CD4AnalysisGlobalScope(modelPath, cdLanguage);
  }

}
