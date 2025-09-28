/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd.codegen.decorators.InheritanceVisitorDecorator;
import de.monticore.cd.codegen.decorators.matcher.MatchResult;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Optional;

public class InheritanceVisitorDecoratorTest extends AbstractDecoratorTest {
  
  /**
   * Test the {@link InheritanceVisitorDecorator} by applying it to a CD. The
   * cdlang/src/cdGenIntTest/java/visitor/InheritanceVisitorDecoratorTest then tests the generated
   * result
   */
  @Test
  public void testInheritanceVisitor() throws Exception {
    var opt = CD4CodeMill.parser()
        .parse_String( // @formatter:off
        "classdiagram TestInheritanceVisitor {\n"
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
          + "public class ClassToBeTopped { " + " public ClassWithPrimitiveType pojoType;\n"
          + " public ClassWithPrimitiveType pojoType2;\n" + "}\n "
          + "public class ClassWithPrimitiveType { " + " public int myInt;\n" + "}\n "
          + "public class ClassWithSet { " + " public Set<Integer> mySet;\n"
          + " public Set<Integer> mySet2;\n" + "}\n " + "public class ClassWithList { \n"
          + " public List<Integer> myIntegerList;\n" + " public List<Integer> myIntegerList2;\n"
          + "}  \n" + "public class ClassCircular1 { \n" + "public ClassCircular2 myClassCircular2;\n"
          + "}\n" + "public class ClassCircular2 { \n" + "public ClassCircular1 myClassCircular1;\n"
          + "}\n" + "public class ClassWithAssociation { \n" + "}\n"
          + "public class ClassWithComposition { \n" + "-> (opt)B [0..1] public;\n"
          + "-> (many)B [*] public;\n" + "-> (one)B [1] public;\n" + "-> (opt2)B [0..1] public;\n"
          + "-> (many2)B [*] public;\n" + "-> (one2)B [1] public;\n" + "}\n"
          + "public class ClassWithArray { \n" + " public ClassWithPrimitiveType[] arrayOfString; \n"
          + " public ClassWithPrimitiveType[] arrayOfString2; \n" + "}\n"
          + "public class ClassWith3DimArray { \n"
          + " public ClassWithPrimitiveType[][][] threeDimArrayOfString; \n"
          + " public ClassWithPrimitiveType[][][] threeDimArrayOfString2; \n" + "}\n"
          + "public class ClassWithString { \n" + " public String myString;\n"
          + " public String myString2;\n" + "}\n" + "public class ClassWithMap { \n"
          + " public Map<String, B> myMap;\n" + " public Map<String, B> myMap2;\n" + "}\n"
          + "public class ClassWith2DimMap { \n" + " public Map<String, Map<String,B>> myMap;\n"
          + " public Map<String, Map<String,B>> myMap2;\n" + "}\n" + "public class B { \n" + "}\n"
          + "association [1] AllTogether (owner) -> (owns) B [*]public; "
          + "association [1] ClassWithAssociation (owner) -> (owns) B [*]public; "
          + "association [1] ClassWithAssociation (owner2) -> (owns2) B [*]public; "
          + "interface Level1Interface;"
          + "class Level2class implements Level1Interface{"
          + " int myInt;"
          + "}"
          + "interface Level2Interface;"
          + "class Level3class extends Level2class implements Level2Interface;"
          + "class Level0class {"
          + "-> (many)Level1Interface [*];"
          + "}"
          + "class Level4class extends Level3class;"
          + "class Level5class extends Level4class implements Level4Interface;"
          + "interface Level4Interface extends Level3Interface1, Level3Interface2;"
          + "interface Level3Interface1 extends Level2Interface1;"
          + "interface Level3Interface2 extends Level2Interface2;"
          + "interface Level2Interface1;"
          + "interface Level2Interface2;"
          + "}");
    // @formatter:on
    
    Assertions.assertTrue(opt.isPresent());
    
    super.doTest(opt.get());
    
    // TODO: Remove once non primitive types in CD files are supported and Set and List Setters are implemented
    Log.clearFindings();
  }
  
  @Override
  protected Optional<MCPath> getHandWrittenPath() {
    return Optional.of(new MCPath("src/cdGenIntTestHwc/java"));
  }
  
  @Override
  public void initializeDecConf(GlobalExtensionManagement glex, DecoratorConfig config,
      GeneratorSetup setup) {
    config.withCopyCreator().defaultApply();
    config.withDecorator(new InheritanceVisitorDecorator());
    config.configDefault(InheritanceVisitorDecorator.class, MatchResult.APPLY);
  }
  
}
