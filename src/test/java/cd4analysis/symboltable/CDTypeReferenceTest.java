/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.symboltable;

import cd4analysis.CD4AnalysisLanguage;
import de.monticore.io.paths.IOPaths;
import de.monticore.modelloader.ModelLoader;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.resolving.DefaultResolver;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CDTypeReferenceTest {

  @Test
  public void test() {
    CD4AnalysisLanguage cdLanguage = new CD4AnalysisLanguage();

    final IOPaths ioPaths = new IOPaths(new ArrayList<>(), Arrays.asList(Paths
        .get("src/test/resources")), Paths.get(""));
    ModelLoader modelLoader = new ModelLoader(ioPaths.getModelPath());
    modelLoader.setModelingLanguages(Arrays.asList(cdLanguage));

    ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(DefaultResolver.newResolver(CDTypeSymbol.class,
        CDTypeSymbol.KIND));

    GlobalScope globalScope = new GlobalScope(modelLoader, resolverConfiguration);

    CDTypeSymbol cdType = (CDTypeSymbol) globalScope.resolve("cd4analysis.symboltable.CD2.Person", CDTypeSymbol
        .KIND).orNull();

    assertNotNull(cdType);
    assertEquals("cd4analysis.symboltable.CD2.Person", cdType.getName());

  }

}
