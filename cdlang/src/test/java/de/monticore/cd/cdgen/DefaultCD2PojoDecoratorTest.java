/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the decorators configured by CD2Java. The
 * cdlang/src/cdGenIntTest/java/getter/GetterDecoratorResultTest then tests the generated result
 */
public class DefaultCD2PojoDecoratorTest extends AbstractDecoratorTest {
  
  protected void compileGeneratedSources() throws Exception {
    var compiler = ToolProvider.getSystemJavaCompiler();
    Assertions.assertNotNull(compiler, "The generation regression test requires a JDK");
    var diagnostics = new DiagnosticCollector<JavaFileObject>();
    Path classes = outputDir.toPath().resolve("compiled");
    Files.createDirectories(classes);
    try (
        var files = Files.walk(outputDir.toPath());
        var manager = compiler.getStandardFileManager(diagnostics, null, null)
    ) {
      List<File> sources = files.filter(path -> path.toString().endsWith(".java")).map(Path::toFile)
          .collect(Collectors.toList());
      String loggingPath = new File(Log.class.getProtectionDomain().getCodeSource().getLocation()
          .toURI()).getAbsolutePath();
      Assertions.assertTrue(compiler.getTask(null, manager, diagnostics, List.of("-d", classes
          .toString(), "-classpath", loggingPath), null, manager.getJavaFileObjectsFromFiles(
              sources)).call(), diagnostics.getDiagnostics().toString());
    }
  }
  
  @Test
  public void testRelaxedClassMemberVisibility() throws Exception {
    var ast = CD4CodeMill.parser().parse_String("""
        classdiagram RelaxedVisibility {
          class A {
            private A();
            private String privateValue;
            String packageValue;
            private void privateMethod();
            void packageMethod();
            public void publicMethod();
            protected void protectedMethod();
          }
          class B {}
          association A -> (privateBs) B [*] private;
          association A -> (packageBs) B [*];
          association A -> (privateOrderedBs) B [*] private {ordered};
          association A -> (packageOrderedBs) B [*] {ordered};
          association A -> (privateOptionalB) B [0..1] private;
          association A -> (packageOptionalB) B [0..1];
          association A -> (publicBs) B [*] public;
        }
        """).orElseThrow();
    
    var result = doTest(ast);
    var clazz = result.getDecoratedCD().getCDDefinition().getCDClassesList().get(0);
    assertProtectedOnly(clazz.getCDConstructorList().get(0).getModifier());
    for (String attributeName : List.of("privateValue", "packageValue", "privateBs", "packageBs",
        "privateOrderedBs", "packageOrderedBs", "privateOptionalB", "packageOptionalB")) {
      var modifier = clazz.getCDAttributeList().stream().filter(attribute -> attribute.getName()
          .equals(attributeName)).findFirst().orElseThrow().getModifier();
      assertProtectedOnly(modifier);
    }
    for (String methodName : List.of("privateMethod", "packageMethod", "getPrivateValue",
        "setPrivateValue", "getPackageValue", "setPackageValue")) {
      var modifier = clazz.getCDMethodList().stream().filter(method -> method.getName().equals(
          methodName)).findFirst().orElseThrow().getModifier();
      assertProtectedOnly(modifier);
    }
    for (String memberSuffix : List.of("PrivateBs", "PackageBs", "PrivateOrderedBs",
        "PackageOrderedBs", "PrivateOptionalB", "PackageOptionalB")) {
      var generatedMethods = clazz.getCDMethodList().stream().filter(method -> method.getName()
          .endsWith(memberSuffix)).collect(Collectors.toList());
      Assertions.assertFalse(generatedMethods.isEmpty(), memberSuffix);
      generatedMethods.forEach(method -> assertProtectedOnly(method.getModifier()));
    }
    Assertions.assertTrue(clazz.getCDMethodList().stream().filter(method -> method.getName().equals(
        "publicMethod")).findFirst().orElseThrow().getModifier().isPublic());
    Assertions.assertTrue(clazz.getCDMethodList().stream().filter(method -> method.getName().equals(
        "protectedMethod")).findFirst().orElseThrow().getModifier().isProtected());
    var publicAssociationMethods = clazz.getCDMethodList().stream().filter(method -> method
        .getName().endsWith("PublicBs")).collect(Collectors.toList());
    Assertions.assertFalse(publicAssociationMethods.isEmpty());
    publicAssociationMethods.forEach(method -> Assertions.assertTrue(method.getModifier()
        .isPublic()));
    MCAssertions.assertNoFindings();
    compileGeneratedSources();
  }
  
  protected void assertProtectedOnly(ASTModifier modifier) {
    Assertions.assertTrue(modifier.isProtected());
    Assertions.assertFalse(modifier.isPrivate());
    Assertions.assertFalse(modifier.isPublic());
  }
  
  @Test
  public void testAll() throws Exception {
    var opt = CD4CodeMill.parser().parse_String("classdiagram TestDefaultCD2Pojo {\n"
        + " <<getter>> public class TestGetterC { \n" + " boolean myBool;" + " public int myInt;"
        + " <<noGetter>> public int pubX;" + " public void voidM();" + " public String stringM();"
        + " public static void staticVoidM();" + " public static String staticStringM();" + " }\n"
        + " public association TestGetterC -> (roleB) Other [*];\n"
        + " public association TestGetterC -> (orderedRole) Other [*] {ordered};\n"
        + " <<getter>> public class Other { \n" + "}\n" + "}");
    
    Assertions.assertTrue(opt.isPresent());
    
    super.doTest(opt.get());
    
    MCAssertions.assertNoFindings();
  }
  
  @Override
  public void initializeDecConf(GlobalExtensionManagement glex, DecoratorConfig config,
      GeneratorSetup setup) {
    // Instead of adding the getters via the API, we call the config template
    String configTemplate = "cd2java.init.CD2Pojo";
    TemplateController tc = setup.getNewTemplateController(configTemplate);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);
    glex.setGlobalValue("glex", glex);
    glex.setGlobalValue("decConfig", config);
    glex.setGlobalValue("genSetup", setup);
    hpp.processValue(tc, new ArrayList<>());
  }
  
}
