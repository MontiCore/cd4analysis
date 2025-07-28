/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd.codegen.decorators.BuilderDecorator;
import de.monticore.cd.codegen.decorators.CardinalityDefaultDecorator;
import de.monticore.cd.codegen.decorators.DeepCloneAndDeepEqualsDecorator;
import de.monticore.cd.codegen.decorators.matcher.MatchResult;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdgen.CDGenTool;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.tagging.tags.TagsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeepCloneAndDeepEqualsDecoratorTest extends AbstractDecoratorTest {
  
  @Test
  public void testDeepCopyAndDeepEquals() throws Exception {
    TagsMill.reset();
    TagsMill.init();
    var opt = CD4CodeMill.parser().parse_String("classdiagram TestDeepCloneAndDeepEquals {\n"
        + " public class AllTogether { \n" + " public int myInt;\n" + " public boolean myBool;\n"
        + " -> (manyClassWith2DimList)ClassWith2DimList [*] public;\n"
        + " -> (optClassWith2DimList)ClassWith2DimList [0..1] public;\n"
        + " -> (oneClassWith2DimList)ClassWith2DimList [1] public;\n" + " }\n"
        + " public class ClassWith2DimList { \n" + " public List<List<Integer>> my2dimList;\n"
        + " public List<List<Integer>> my2dimList2;\n" + " }\n"
        + " public class ClassWith2DimSet { \n" + " public Set<Set<Integer>> my2dimSet;\n"
        + " public Set<Set<Integer>> my2dimSet2;\n" + " }\n" + "public class ClassWithOptional { "
        + " public Optional<Integer> myOptionalInteger;\n"
        + " public Optional<Integer> myOptionalInteger2;\n" + "}\n "
        + "public class ClassWith2DimOptional { "
        + " public Optional<Optional<B>> my2DimOptional;\n"
        + " public Optional<Optional<B>> my2DimOptional2;\n" + "}\n "
        + "public class ClassWithPojoClassType { " + " public ClassWithPrimitiveType pojoType;\n"
        + " public ClassWithPrimitiveType pojoType2;\n" + "}\n "
        + "public class ClassWithPrimitiveType { " + " public int myInt;\n" + "}\n "
        + "public class ClassWithSet { " + " public Set<Integer> mySet;\n"
        + " public Set<Integer> mySet2;\n" + "}\n " + "public class ClassWithList { \n"
        + " public List<Integer> myIntegerList;\n" + " public List<Integer> myIntegerList2;\n"
        + "}  \n" + "public class ClassCircular1 { \n" + "public ClassCircular2 myClassCircular2;\n"
        + "}\n" + "public class ClassCircular2 { \n" + "public ClassCircular1 myClassCircular1;\n"
        + "}\n" + "public class ClassWithAssociation { \n" + "}\n"
        + "public class ClassWithNoDefaultConstructor {\n"
        + " public ClassWithNoDefaultConstructor(int i);\n" + " int i; \n" + "}\n"
        + "public class ClassWithComposition { \n" + "-> (opt)B [0..1] public;\n"
        + "-> (many)B [*] public;\n" + "-> (one)B [1] public;\n" + "-> (opt2)B [0..1] public;\n"
        + "-> (many2)B [*] public;\n" + "-> (one2)B [1] public;\n" + "}\n"
        + "public class ClassWithArray { \n" + " public ClassWithPrimitiveType[] arrayOfString; \n"
        + " public ClassWithPrimitiveType[] arrayOfString2; \n" + "}\n"
        + "public class ClassWith3DArray { \n"
        + " public ClassWithPrimitiveType[][][] threeDimArrayOfString; \n"
        + " public ClassWithPrimitiveType[][][] threeDimArrayOfString2; \n" + "}\n"
        + "public class ClassWithString { \n" + " public String myString;\n"
        + " public String myString2;\n" + "}\n" + "public class ClassWithEnum { \n"
        + " public TestEnum myEnum;\n" + " public TestEnum myEnum2;\n" + "}\n"
        + "public class ClassWithInterface { \n" + " public Level1Interface myInterface;\n"
        + " public Level1Interface myInterface2 ;\n" + " -> (many)Level1Interface [*] public;\n"
        + " -> (many2)Level1Interface [*] public;\n" + "}\n" + "public class ClassWithMap { \n"
        + " public Map<String, B> myMap;\n" + " public Map<String, B> myMap2;\n" + "}\n"
        + "public class ClassWith2DMap { \n" + " public Map<String, Map<String,B>> myMap;\n"
        + " public Map<String, Map<String,B>> myMap2;\n" + "}\n" + "public class B { \n" + "}\n"
        + " enum TestEnum { RUNNING, IDLE, ERROR; }\n"
        + " interface Level1Interface { public boolean myBool = false;} \n"
        + " class Level2class implements Level1Interface{\n" + "  int myInt;\n" + " }\n"
        + "class Level3class extends Level2class implements Level1Interface;"
        + " <<builder>> public class ClassWithBuilder { \n" + " public ClassWithBuilder(int i);\n"
        + " public int myInt;\n" + " }\n"
        + "association [1] AllTogether (owner) -> (owns) B [*]public; "
        + "association [1] ClassWithAssociation (owner) -> (owns) B [*]public; "
        + "association [1] ClassWithAssociation (owner2) -> (owns2) B [*]public; " + "}");
    
    Assertions.assertTrue(opt.isPresent());
    
    super.doTest(opt.get());
    
    // TODO: Remove once non primitive types in CD files are supported and Set and List Setters are implemented
    Log.getFindings().clear();
  }
  
  @Test
  public void testTemplateExistence() {
    //test existence of the templates
    List<Path> templatePaths = new ArrayList<>();
    templatePaths.add(Paths.get(
        "src/main/resources/methods/deepCloneAndDeepEquals/deepClone1.ftl"));
    templatePaths.add(Paths.get(
        "src/main/resources/methods/deepCloneAndDeepEquals/deepClone2.ftl"));
    templatePaths.add(Paths.get(
        "src/main/resources/methods/deepCloneAndDeepEquals/deepClone2Inner.ftl"));
    templatePaths.add(Paths.get(
        "src/main/resources/methods/deepCloneAndDeepEquals/deepEquals1.ftl"));
    templatePaths.add(Paths.get(
        "src/main/resources/methods/deepCloneAndDeepEquals/deepEquals2.ftl"));
    templatePaths.add(Paths.get(
        "src/main/resources/methods/deepCloneAndDeepEquals/deepEquals3.ftl"));
    templatePaths.add(Paths.get(
        "src/main/resources/methods/deepCloneAndDeepEquals/deepEquals3Inner.ftl"));
    for (Path temPath : templatePaths) {
      Assertions.assertTrue(Files.exists(temPath));
    }
  }
  
  @Test
  public void testGetAllCDAttributes() throws IOException {
    var opt = CD4CodeMill.parser().parse_String("classdiagram TestDeepCloneAndDeepEquals {\n"
        + "public class B { \n" + "}\n" + " interface Level1Interface { public boolean myBool;} \n"
        + " class Level2class implements Level1Interface{\n" + "  int myInt;\n" + " }\n"
        + " class Level3class extends Level2class implements Level1Interface{\n"
        + " boolean myBool;\n" + " }\n"
        + " class Level4class extends Level3class implements Level1Interface;" + "}");
    
    Assertions.assertTrue(opt.isPresent());
    ASTCDCompilationUnit cd = opt.get();
    CDGenTool tool = new CDGenTool();
    tool.trafoBeforeSymtab(Collections.singletonList(cd));
    
    final boolean class2mc = this.withClass2MC();
    tool.initializeSymbolTable(class2mc);
    
    // Create ST
    tool.createSymbolTable(cd);
    
    // Complete ST
    tool.completeSymbolTable(cd);
    
    ASTCDClass astcdClass = (ASTCDClass) cd.getCDDefinition().getCDElement(4);
    DeepCloneAndDeepEqualsDecorator deepCloneAndDeepEqualsDecorator =
        new DeepCloneAndDeepEqualsDecorator();
    List<ASTCDAttribute> attributes = deepCloneAndDeepEqualsDecorator.getAllCDAttributes(
        astcdClass);
    
    //as we do not care about interface attributes, they should be ignored.
    //the class has 2 super class with 1 attribute each.
    //Therefore, the resulting list should be of size 2
    Assertions.assertSame(2, attributes.size());
  }
  
  @Override
  public void initializeDecConf(GlobalExtensionManagement glex, DecoratorConfig config,
      GeneratorSetup setup) {
    config.withCopyCreator().defaultApply();
    config.withDecorator(new CardinalityDefaultDecorator());
    config.configDefault(CardinalityDefaultDecorator.class, MatchResult.APPLY);
    config.withDecorator(new DeepCloneAndDeepEqualsDecorator());
    config.configDefault(DeepCloneAndDeepEqualsDecorator.class, MatchResult.APPLY);
    config.withDecorator(new BuilderDecorator());
    config.configApplyMatchName(BuilderDecorator.class, "builder");
    config.configIgnoreMatchName(BuilderDecorator.class, "noBuilder");
  }
  
}
