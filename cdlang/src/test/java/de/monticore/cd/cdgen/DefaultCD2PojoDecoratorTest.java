/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd.codegen.trafo.JavaAssociationRoleNameTrafo;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.runtime.junit.MCAssertions;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests the decorators configured by CD2Java. The
 * cdlang/src/cdGenIntTest/java/getter/GetterDecoratorResultTest then tests the generated result
 */
public class DefaultCD2PojoDecoratorTest extends AbstractDecoratorTest {
  
  @ParameterizedTest
  @ValueSource(booleans = { false, true })
  public void testReservedRolesAcrossCDs(boolean reverseOrder) throws Exception {
    var base = CD4CodeMill.parser().parse_String(
        "classdiagram BaseModel { public class Base { protected int class_; } }").orElseThrow();
    var target = CD4CodeMill.parser().parse_String("""
        classdiagram TargetModel {
          public class A extends BaseModel.Base {}
          public class Class {}
          public association A -> Class;
        }
        """).orElseThrow();
    var child = CD4CodeMill.parser().parse_String(
        "classdiagram ChildModel { public class Child extends TargetModel.A { protected int class__; } }")
        .orElseThrow();
    var asts = reverseOrder ? List.of(child, target, base) : List.of(base, target, child);
    tool.trafoBeforeSymtab(asts);
    tool.initializeSymbolTable(false);
    asts.forEach(ast -> tool.createSymbolTable(ast));
    asts.forEach(tool::completeSymbolTable);
    outputDir = new File(outputDir, "crossCDRoles" + reverseOrder);
    var glex = new GlobalExtensionManagement();
    var setup = tool.newConfiguredGeneratorSetup(getAdditionalTemplatesPath(), getHandWrittenPath(),
        outputDir.getAbsolutePath(), glex);
    tool.decorateAndGenerate(glex, config -> initializeDecConf(glex, config, setup), setup,
        () -> tool.initDecoratedGlobalScope(false), decorated -> {}, asts);
    var owner = target.getCDDefinition().getCDClassesList().get(0).getSymbol();
    Assertions.assertEquals(1, owner.getFieldList("class___").size());
    Assertions.assertEquals(1, owner.getCDRoleList("class___").size());
    Assertions.assertTrue(owner.getCDRoleList("class").isEmpty());
    MCAssertions.assertNoFindings();
    compileGeneratedSources();
  }
  
  @Test
  public void testRoleNormalizationUpdatesCachedNamesAndIsIdempotent() throws Exception {
    var ast = CD4CodeMill.parser().parse_String(
        "classdiagram CachedRoles { class A {} class Class {} association A -> Class; }")
        .orElseThrow();
    tool.trafoBeforeSymtab(List.of(ast));
    tool.initializeSymbolTable(false);
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    var owner = ast.getCDDefinition().getCDClassesList().get(0).getSymbol();
    var role = owner.getCDRoleList("class").get(0);
    String originalName = role.getFullName();
    var trafo = new JavaAssociationRoleNameTrafo();
    trafo.transform(List.of(ast));
    trafo.transform(List.of(ast));
    Assertions.assertEquals("class_", role.getName());
    Assertions.assertEquals(originalName + "_", role.getFullName());
    Assertions.assertSame(role, owner.getCDRoleList("class_").get(0));
    Assertions.assertTrue(owner.getCDRoleList("class").isEmpty());
    MCAssertions.assertNoFindings();
  }
  
  @Test
  public void testReservedJavaRoleNames() throws Exception {
    outputDir = new File(outputDir, "reservedRoles");
    // Includes reserved but unused keywords and literals, which are not legal identifiers either.
    String keywords = "abstract assert boolean break byte case catch char class const continue "
        + "default do double else enum extends final finally float for goto if implements import "
        + "instanceof int interface long native new package private protected public return short "
        + "static strictfp super switch synchronized this throw throws transient try void volatile "
        + "while true false null";
    StringBuilder model = new StringBuilder(
        "classdiagram ReservedRoles { <<builder>> public class A {} ");
    for (String keyword : keywords.split(" ")) {
      String type = Character.toUpperCase(keyword.charAt(0)) + keyword.substring(1);
      model.append("public class ").append(type).append(" {} public association A -> ").append(type)
          .append("; ");
    }
    model.append("}");
    var ast = CD4CodeMill.parser().parse_String(model.toString()).orElseThrow();
    doTest(ast);
    var owner = ast.getCDDefinition().getCDClassesList().get(0);
    for (String keyword : keywords.split(" ")) {
      Assertions.assertEquals(1, owner.getSymbol().getFieldList(keyword + "_").size());
      Assertions.assertEquals(1, owner.getSymbol().getCDRoleList(keyword + "_").size());
      Assertions.assertTrue(owner.getSymbol().getCDRoleList(keyword).isEmpty());
    }
    MCAssertions.assertNoFindings();
    compileGeneratedSources();
  }
  
  @Test
  public void testReservedRolesAvoidCollisions() throws Exception {
    outputDir = new File(outputDir, "roleCollisions");
    var ast = CD4CodeMill.parser().parse_String("""
        classdiagram RoleCollisions {
          public class Base { protected int public_; }
          <<builder>> public class A extends Base { protected int public__; }
          public class Child extends A { protected int public___; }
          public class Public {}
          public class Class {}
          public class Other {}
          public association A <-> Public;
          public association A -> Class [0..1];
          public association A -> (public____) Other [*];
          public association A -> (class_) Other [*] {ordered};
          public association Public <-> Class [*];
        }
        """).orElseThrow();
    doTest(ast);
    var owner = ast.getCDDefinition().getCDClassesList().get(1);
    for (String name : List.of("public_____", "class__", "public____", "class_")) {
      Assertions.assertEquals(1, owner.getSymbol().getFieldList(name).size(), name);
      Assertions.assertEquals(1, owner.getSymbol().getCDRoleList(name).size(), name);
    }
    MCAssertions.assertNoFindings();
    compileGeneratedSources();
  }
  
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
