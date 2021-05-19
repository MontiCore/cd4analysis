package de.monticore.cd.methodtemplates;/* (c) https://github.com/MontiCore/monticore */

import com.google.common.collect.Lists;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for parameterized calls of the {@link TemplateController} 
 *
 */

public class CD4CTest {
  
  private GlobalExtensionManagement glex;
  private CD4C cd4c;
  private GeneratorSetup config;

  @Before
  public void setup() {
    Log.init();
    Log.enableFailQuick(false);

    // Configure glex
    glex = new GlobalExtensionManagement();
    config = new GeneratorSetup();
    config.setGlex(glex);
    config.setOutputDirectory(new File("target/generated"));
    config.setTracing(false);
    File templatePath = new File("src/test/resources");
    config.setAdditionalTemplatePaths(Lists.newArrayList(templatePath));

    // Configure CD4C
    cd4c = new CD4C(config);
  }
  
  // =================================================
  // Tests with templates
  // =================================================
  
  @Test
  public void testCreateMethod() {
    ASTCDClass  clazz = CD4CodeMill.cDClassBuilder().
            setName("HelloWorld").
            setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build()).build();
    Optional<ASTCDMethodSignature> methSignature = cd4c.createMethod(clazz, "de.monticore.cd.methodtemplates.PrintMethod");

    assertTrue(methSignature.isPresent());
    assertTrue(methSignature.get() instanceof ASTCDMethod);
    ASTCDMethod meth = (ASTCDMethod) methSignature.get();
    assertEquals("print", meth.getName());
  }

  @Test
  public void testGenerateMethod() {
    // Build class for testing
    ASTCDClass  clazz = CD4CodeMill.cDClassBuilder().
            setName("HelloWorld").
            setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build()).build();
    final CD4CodeFullPrettyPrinter printer =  new CD4CodeFullPrettyPrinter(new IndentPrinter());

    // add the method that is described in template "PrintMethod"
    cd4c.addMethod(clazz, "de.monticore.cd.methodtemplates.PrintMethod");
    // add the constructor that is described in template "DefaultConstructor"
    cd4c.addConstructor(clazz, "de.monticore.cd.methodtemplates.DefaultConstructor");

    // generate Java-Code
    GeneratorEngine generatorEngine = new GeneratorEngine(config);
    final Path output = Paths.get("HelloWorld.java");
    generatorEngine.generate("de.monticore.cd.methodtemplates.core.Class", output, clazz, printer);
  }

}
