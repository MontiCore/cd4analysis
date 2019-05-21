/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cd4analysis._symboltable;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.cd.CD4AnalysisLanguage;

import java.nio.file.Paths;

public class CD4AGlobalScopeTestFactory {

  public static GlobalScope create() {
    final CD4AnalysisLanguage cdLanguage = new CD4AnalysisLanguage();

    final ResolvingConfiguration resolverConfiguration = new ResolvingConfiguration();
    resolverConfiguration.addDefaultFilters(cdLanguage.getResolvingFilters());

    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/"));

    return new GlobalScope(modelPath, cdLanguage, resolverConfiguration);
  }

}
