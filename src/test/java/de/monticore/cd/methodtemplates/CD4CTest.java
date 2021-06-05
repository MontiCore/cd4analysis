package de.monticore.cd.methodtemplates;/* (c) https://github.com/MontiCore/monticore */

import com.google.common.collect.Lists;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.umlmodifier._ast.ASTModifierBuilder;
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
 */

public class CD4CTest extends CD4CodeTestBasis {

  private GeneratorSetup config;
  private ASTCDCompilationUnit node;

  @Before
  public void init() {
    Log.init();
    Log.enableFailQuick(false);

    // Configure glex
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    config = new GeneratorSetup();
    config.setGlex(glex);
    config.setOutputDirectory(new File("target/generated"));
    config.setTracing(false);
    File templatePath = new File("src/test/resources");
    config.setAdditionalTemplatePaths(Lists.newArrayList(templatePath));

    final ASTCDDefinition definition = new ASTCDDefinitionBuilder()
        .setName("Test")
        .setModifier(CD4CodeMill.modifierBuilder().build())
        .build();
    node = new ASTCDCompilationUnitBuilder()
        .setCDDefinition(definition)
        .build();

    // Configure CD4C
    CD4C.init(config);
  }

  // =================================================
  // Tests with templates
  // =================================================

  @Test
  public void testCreateMethod() {
    ASTCDClass clazz = CD4CodeMill.cDClassBuilder()
        .setName("HelloWorld")
        .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
        .build();

    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(clazz);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);

    // testing createMethod
    Optional<ASTCDMethodSignature> methSignature = CD4C.getInstance().createMethod(clazz, "de.monticore.cd.methodtemplates.PrintMethod");

    assertTrue(methSignature.isPresent());
    assertTrue(methSignature.get() instanceof ASTCDMethod);
    ASTCDMethod meth = (ASTCDMethod) methSignature.get();
    assertEquals("print", meth.getName());

    checkLogError();
  }

  @Test
  public void testGenerateMethod() {
    // Build class for testing
    ASTCDClass clazz = CD4CodeMill.cDClassBuilder()
        .setName("HelloWorld")
        .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
        .build();
    final CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(clazz);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);

    // testing addMethod, .addConstructor
    // add the method that is described in template "PrintMethod"
    CD4C.getInstance().addMethod(clazz, "de.monticore.cd.methodtemplates.PrintMethod");
    // add the constructor that is described in template "DefaultConstructor"
    CD4C.getInstance().addConstructor(clazz, "de.monticore.cd.methodtemplates.DefaultConstructor");

    checkLogError();

    // generate Java-Code
    GeneratorEngine generatorEngine = new GeneratorEngine(config);
    final Path output = Paths.get("HelloWorld.java");
    generatorEngine.generate("de.monticore.cd.methodtemplates.core.Class", output, clazz, printer);
  }

  @Test
  public void testGenerateMethodWithAllAttributes() {
    // Build class for testing
    ASTCDClass clazz = CD4CodeMill.cDClassBuilder()
        .setName("HelloWorldWithConstructor")
        .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
        .build();

    //  testing .createAttribute
    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(clazz);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);

    clazz.addCDMember(
        CDAttributeFacade.getInstance()
            .createAttribute(new ASTModifierBuilder().PUBLIC().build(), String.class, "text")
    );
    clazz.addCDMember(
        CDAttributeFacade.getInstance()
            .createAttribute(new ASTModifierBuilder().PROTECTED().build(), "int", "notPublic")
    );

    final CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    // add the constructor that is described in template "DefaultConstructor"
    CD4C.getInstance().addConstructor(clazz, "de.monticore.cd.methodtemplates.ConstructorWithAllAttributes");

    checkLogError();

    // generate Java-Code
    GeneratorEngine generatorEngine = new GeneratorEngine(config);
    final Path output = Paths.get("HelloWorldWithConstructor.java");
    generatorEngine.generate("de.monticore.cd.methodtemplates.core.Class", output, clazz, printer);
  }

  @Test
  public void testNoPredicates() {
    // Build class for testing
    ASTCDClass clazz = CD4CodeMill.cDClassBuilder()
        .setName("HelloWorldNoPredicates")
        .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
        .build();
    clazz.addCDMember(
        CDMethodFacade.getInstance()
            .createMethod(new ASTModifierBuilder().PUBLIC().build(), String.class, "print")
    );

    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(clazz);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);

    final CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    // try to create a print method that already exists
    CD4C.getInstance().addMethod(clazz, "de.monticore.cd.methodtemplates.PrintMethod");

    checkLogError();

    // generate Java-Code
    GeneratorEngine generatorEngine = new GeneratorEngine(config);
    final Path output = Paths.get(clazz.getName() + ".java");
    generatorEngine.generate("de.monticore.cd.methodtemplates.core.Class", output, clazz, printer);
  }

  @Test
  public void testWithClassPredicates() {
    // Build class for testing
    ASTCDClass clazz = CD4CodeMill.cDClassBuilder()
        .setName("HelloWorldWithClassPredicates")
        .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
        .build();
    clazz.addCDMember(
        CDMethodFacade.getInstance()
            .createMethod(new ASTModifierBuilder().PUBLIC().build(), String.class, "print")
    );

    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(clazz);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);

    CD4C.getInstance().addDefaultClassPredicates();

    // try to create a print method that already exists
    CD4C.getInstance().addMethod(clazz, "de.monticore.cd.methodtemplates.PrintMethod");

    assertEquals(2, Log.getFindingsCount());
    assertEquals("110C8: The class 'HelloWorldWithClassPredicates' already has a method named 'print'", Log.getFindings().get(0).getMsg());
    assertEquals("11011: A check for the class method failed for method 'print'", Log.getFindings().get(1).getMsg());

    Log.clearFindings();
  }

  @Test
  public void testWithUnknownReturnTypePredicates() {
    // Build class for testing
    ASTCDClass clazz = CD4CodeMill.cDClassBuilder()
        .setName("HelloWorldWithUnknownReturnTypePredicates")
        .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
        .build();

    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(clazz);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);

    CD4C.getInstance().addDefaultPredicates();

    // try to create a method with unkown type
    CD4C.getInstance().addMethod(clazz, "de.monticore.cd.methodtemplates.UnknownReturnType");

    assertEquals(3, Log.getFindingsCount());
    assertEquals("0xA0324 The qualified type UnknownReturnType cannot be found", Log.getFindings().get(0).getMsg());
    assertEquals("110C1: The return type 'UnknownReturnType' of the method signature (public UnknownReturnType print();\n) could not be resolved.", Log.getFindings().get(1).getMsg());
    assertEquals("11010: There was no method created in the template 'de.monticore.cd.methodtemplates.UnknownReturnType'", Log.getFindings().get(2).getMsg());

    Log.clearFindings();
  }

  @Test
  public void testWithUnknownParameterTypePredicates() {
    // Build class for testing
    ASTCDClass clazz = CD4CodeMill.cDClassBuilder()
        .setName("HelloWorldWithUnknownParameterTypePredicates")
        .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
        .build();

    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(clazz);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);

    CD4C.getInstance().addDefaultPredicates();

    // try to create a print method that already exists
    CD4C.getInstance().addMethod(clazz, "de.monticore.cd.methodtemplates.UnknownParameterType");

    assertEquals(3, Log.getFindingsCount());
    assertEquals("0xA0324 The qualified type UnknownParameterType cannot be found", Log.getFindings().get(0).getMsg());
    assertEquals("110C0: The following types of the method signature (public String print(UnknownParameterType p);\n"
        + ") could not be resolved 'UnknownParameterType'.", Log.getFindings().get(1).getMsg());
    assertEquals("11010: There was no method created in the template 'de.monticore.cd.methodtemplates.UnknownParameterType'", Log.getFindings().get(2).getMsg());

    Log.clearFindings();
  }
}
