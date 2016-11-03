/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import java.nio.file.Paths;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.umlcd4a.CD4AnalysisLanguage;

public class CD4AGlobalScopeTestFactory {

  public static GlobalScope create() {
    final CD4AnalysisLanguage cdLanguage = new CD4AnalysisLanguage();

    final ResolvingConfiguration resolverConfiguration = new ResolvingConfiguration();
    resolverConfiguration.addDefaultFilters(cdLanguage.getResolvers());

    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/"));

    return new GlobalScope(modelPath, cdLanguage, resolverConfiguration);
  }

}
