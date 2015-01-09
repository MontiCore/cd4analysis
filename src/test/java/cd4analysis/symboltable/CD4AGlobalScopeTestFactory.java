/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.symboltable;

import cd4analysis.CD4AnalysisLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.modelloader.ModelLoader;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolverConfiguration;

import java.nio.file.Paths;
import java.util.Arrays;

public class CD4AGlobalScopeTestFactory {

  public static GlobalScope create() {
    final CD4AnalysisLanguage cdLanguage = new CD4AnalysisLanguage();

    final ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(cdLanguage.getResolvers());


    final ModelPath modelPath = new ModelPath(Paths.get("src/test/resources"));
    final ModelLoader modelLoader = new ModelLoader(modelPath);
    modelLoader.setModelingLanguages(Arrays.asList(cdLanguage));

    return new GlobalScope(modelLoader, resolverConfiguration);
  }

}
