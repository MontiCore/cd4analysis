/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.methodtemplates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.ImportStatement;
import de.monticore.umlmodifier._ast.ASTModifierBuilder;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

/** Tests for parameterized calls of the {@link TemplateController} */
public class CD4CTest extends CD4CodeTestBasis {

  private GeneratorSetup config;
  private ASTCDCompilationUnit node;

  @Before
  public void init() throws IOException {
    LogStub.init();
    Log.enableFailQuick(false);
    Log.clearFindings();
    CD4C.reset();

    // Configure glex
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    config = new GeneratorSetup();
    config.setGlex(glex);
    config.setOutputDirectory(new File("target/generated"));
    config.setTracing(false);
    config.setAdditionalTemplatePaths(
        Lists.newArrayList(new File("src/main/resources"), new File("src/test/resources")));

    // Configure CD4C
    CD4C.init(config);

    // create diagram
    node = p.parse(getFilePath("cd4code/generator/Simple.cd")).get();
  }

  // =================================================
  // Tests with templates
  // =================================================

  @Test
  public void testCreateMethod() {
    ASTCDClass clazz =
        CD4CodeMill.cDClassBuilder()
            .setName("HelloWorld")
            .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
            .build();

    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(clazz);

    // testing createMethod
    Optional<ASTCDMethodSignature> methSignature =
        CD4C.getInstance().createMethod(clazz, "de.monticore.cd.methodtemplates.PrintMethod");

    assertTrue(methSignature.isPresent());
    assertTrue(methSignature.get() instanceof ASTCDMethod);
    ASTCDMethod meth = (ASTCDMethod) methSignature.get();
    assertEquals("print", meth.getName());

    checkLogError();
  }

  @Test
  public void testCreateMethodInInterfaces() {
    ASTCDInterface ast =
        CD4CodeMill.cDInterfaceBuilder()
            .setName("IHelloWorld")
            .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
            .build();

    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(ast);

    // testing createMethod
    Optional<ASTCDMethodSignature> methSignature =
        CD4C.getInstance().createMethod(ast, "de.monticore.cd.methodtemplates.PrintMethod");

    assertTrue(methSignature.isPresent());
    assertTrue(methSignature.get() instanceof ASTCDMethod);
    ASTCDMethod meth = (ASTCDMethod) methSignature.get();
    assertEquals("print", meth.getName());

    checkLogError();
  }

  @Test
  public void testGenerateMethod() {
    // Build class for testing
    ASTCDClass clazz =
        CD4CodeMill.cDClassBuilder()
            .setName("HelloWorld")
            .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
            .build();
    final CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(clazz);

    // testing addMethod, .addConstructor
    // add the method that is described in template "PrintMethod"
    CD4C.getInstance().addMethod(clazz, "de.monticore.cd.methodtemplates.PrintMethod");
    // add the constructor that is described in template "DefaultConstructor"
    CD4C.getInstance().addConstructor(clazz, "de.monticore.cd.methodtemplates.DefaultConstructor");

    checkLogError();

    // generate Java-Code
    GeneratorEngine generatorEngine = new GeneratorEngine(config);
    final Path output = Paths.get("HelloWorld.java");
    generatorEngine.generate("cd2java.Class", output, clazz, createDeafaultPkg());
  }

  @Test
  public void testGenerateMethodWithAllAttributes() {
    // Build class for testing
    ASTCDClass clazz =
        CD4CodeMill.cDClassBuilder()
            .setName("HelloWorldWithConstructor")
            .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
            .build();

    //  testing .createAttribute
    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(clazz);

    clazz.addCDMember(
        CDAttributeFacade.getInstance()
            .createAttribute(new ASTModifierBuilder().PUBLIC().build(), String.class, "text"));
    clazz.addCDMember(
        CDAttributeFacade.getInstance()
            .createAttribute(new ASTModifierBuilder().PROTECTED().build(), "int", "notPublic"));

    final CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    // add the constructor that is described in template "DefaultConstructor"
    CD4C.getInstance()
        .addConstructor(clazz, "de.monticore.cd.methodtemplates.ConstructorWithAllAttributes");

    checkLogError();

    // generate Java-Code
    GeneratorEngine generatorEngine = new GeneratorEngine(config);
    final Path output = Paths.get("HelloWorldWithConstructor.java");
    generatorEngine.generate("cd2java.Class", output, clazz, createDeafaultPkg());
  }

  @Test
  public void testGenerateAttributeFromTemplate() throws IOException {
    // Build class for testing
    ASTCDClass clazz =
        CD4CodeMill.cDClassBuilder()
            .setName("AttributeFromTemplate")
            .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
            .build();

    node.getCDDefinition().addCDElement(clazz);
    ASTCDAttribute a =
        CD4C.getInstance()
            .addAttributeFromTemplate(clazz, "de.monticore.cd.methodtemplates.Attribute", "world");

    ASTCDAttribute b =
      CD4C.getInstance()
        .addAttributeFromTemplate(clazz, "de.monticore.cd.methodtemplates.AttributeWithComplexInit", "world");

    checkLogError();

    assertEquals(2, clazz.sizeCDMembers());
    ASTCDMember member = clazz.getCDMember(0);
    assertEquals(a, member);

    ASTCDMember member2 = clazz.getCDMember(1);
    assertEquals(b, member2);

    GeneratorEngine generatorEngine = new GeneratorEngine(config);
    final Path output = Paths.get("AttributeFromTemplate.java");
    generatorEngine.generate("cd2java.Class", output, clazz, createDeafaultPkg());

    Path outputFile = config.getOutputDirectory().toPath().resolve(output);
    String content = IOUtils.toString(outputFile.toUri(), StandardCharsets.UTF_8);

    assertTrue(content.contains("\"Hello world\""));
    assertTrue(content.contains("= new String(\"Constructor use: world\");"));
  }

  @Test
  public void testNoPredicates() {
    // Build class for testing
    ASTCDClass clazz =
        CD4CodeMill.cDClassBuilder()
            .setName("HelloWorldNoPredicates")
            .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
            .build();
    clazz.addCDMember(
        CDMethodFacade.getInstance()
            .createMethod(new ASTModifierBuilder().PUBLIC().build(), String.class, "print"));

    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(clazz);

    final CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    // try to create a print method that already exists
    CD4C.getInstance().addMethod(clazz, "de.monticore.cd.methodtemplates.PrintMethod");

    checkLogError();

    // generate Java-Code
    GeneratorEngine generatorEngine = new GeneratorEngine(config);
    final Path output = Paths.get(clazz.getName() + ".java");
    generatorEngine.generate("cd2java.Class", output, clazz, createDeafaultPkg());
  }

  @Test
  public void testWithClassPredicates() {
    // Build class for testing
    ASTCDClass clazz =
        CD4CodeMill.cDClassBuilder()
            .setName("HelloWorldWithClassPredicates")
            .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
            .build();
    clazz.addCDMember(
        CDMethodFacade.getInstance()
            .createMethod(new ASTModifierBuilder().PUBLIC().build(), String.class, "print"));

    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(clazz);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);

    CD4C.getInstance().addDefaultClassPredicates();

    // try to create a print method that already exists
    CD4C.getInstance().addMethod(clazz, "de.monticore.cd.methodtemplates.PrintMethod");

    assertEquals(2, Log.getFindingsCount());
    assertEquals(
        "0x110C8: The class 'HelloWorldWithClassPredicates' already has a method named 'print'",
        Log.getFindings().get(0).getMsg());
    assertEquals(
        "0x11011: A check for the class method failed for method 'print'",
        Log.getFindings().get(1).getMsg());

    Log.clearFindings();
  }

  @Test
  public void testWithUnknownReturnTypePredicates() {
    // Build class for testing
    ASTCDClass clazz =
        CD4CodeMill.cDClassBuilder()
            .setName("HelloWorldWithUnknownReturnTypePredicates")
            .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
            .build();

    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(clazz);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);

    CD4C.getInstance().addDefaultPredicates();

    // try to create a method with unkown type
    CD4C.getInstance().addMethod(clazz, "de.monticore.cd.methodtemplates.UnknownReturnType");

    assertEquals(1, Log.getFindingsCount());
    assertEquals("0xA0324 Cannot find symbol UnknownReturnType", Log.getFindings().get(0).getMsg());

    Log.clearFindings();
  }

  @Test
  public void testWithUnknownParameterTypePredicates() {
    // Build class for testing
    ASTCDClass clazz =
        CD4CodeMill.cDClassBuilder()
            .setName("HelloWorldWithUnknownParameterTypePredicates")
            .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
            .build();

    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(clazz);
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(node);
    scope.addImports(new ImportStatement("java.lang", true));

    CD4C.getInstance().addDefaultPredicates();

    // try to create a print method that already exists
    CD4C.getInstance().addMethod(clazz, "de.monticore.cd.methodtemplates.UnknownParameterType");

    assertEquals(1, Log.getFindingsCount());
    assertEquals(
        "0xA0324 Cannot find symbol UnknownParameterType", Log.getFindings().get(0).getMsg());

    Log.clearFindings();
  }

  @Test
  public void testWithAttrClassPredicates() {
    // Build class for testing
    ASTCDClass clazz =
        CD4CodeMill.cDClassBuilder()
            .setName("HelloWorldWithClassPredicates")
            .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
            .build();
    clazz.addCDMember(
        CDAttributeFacade.getInstance()
            .createAttribute(CD4CodeMill.modifierBuilder().build(), "int", "counter"));

    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(clazz);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);

    CD4C.getInstance().addDefaultClassPredicates();

    // try to create a print method that already exists
    CD4C.getInstance().addAttribute(clazz, "int counter;");

    assertEquals(1, Log.getFindingsCount());
    assertEquals(
        "0x110C9: The class 'HelloWorldWithClassPredicates' already has a attribute named 'counter'",
        Log.getFindings().get(0).getMsg());

    Log.clearFindings();
  }

  @Test
  public void testWithUnknownAttributeTypePredicates() {
    // Build class for testing
    ASTCDClass clazz =
        CD4CodeMill.cDClassBuilder()
            .setName("HelloWorldWithUnknownAttributeTypePredicates")
            .setModifier(CD4CodeMill.modifierBuilder().setPublic(true).build())
            .build();

    // add class to the AST and create a symbol table to we can resolve the types
    node.getCDDefinition().addCDElement(clazz);
    CD4CodeMill.scopesGenitorDelegator().createFromAST(node);

    CD4C.getInstance().addDefaultPredicates();

    // try to create a method with unkown type
    CD4C.getInstance().addAttribute(clazz, "UnknownAttributeType unkwonAttributeType;");

    assertEquals(1, Log.getFindingsCount());
    assertEquals(
        "0xA0324 Cannot find symbol UnknownAttributeType", Log.getFindings().get(0).getMsg());

    Log.clearFindings();
  }

  protected ASTCDPackage createDeafaultPkg() {
    return CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(
            CD4CodeMill.mCQualifiedNameBuilder().addParts("de").addParts("monticore").build())
        .build();
  }
}
