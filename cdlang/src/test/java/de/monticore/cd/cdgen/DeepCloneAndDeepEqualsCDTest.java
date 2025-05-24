package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.decorators.BuilderDecorator;
import de.monticore.cd.codegen.decorators.CardinalityDefaultDecorator;
import de.monticore.cd.codegen.decorators.DeepCloneAndDeepEqualsDecorator;
import de.monticore.cd.codegen.decorators.matcher.MatchResult;
import de.monticore.cd4code.CD4CodeMill;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DeepCloneAndDeepEqualsCDTest extends AbstractCDGenTest{

  @Test
  public void testDeepCopyAndDeepEquals() throws Exception {
    setup.withDecorator(new CardinalityDefaultDecorator());
    setup.configDefault(CardinalityDefaultDecorator.class, MatchResult.APPLY);

    setup.withDecorator(new DeepCloneAndDeepEqualsDecorator());
    setup.configDefault(DeepCloneAndDeepEqualsDecorator.class, MatchResult.APPLY);

    var opt =
      CD4CodeMill.parser()
        .parse_String("classdiagram TestDeepCloneAndDeepEquals {\n" +
          " public class AllTogether { \n" +
          " public int myInt;\n" +
          " public boolean myBool;\n" +
          " -> (manyClassWith2DimList)ClassWith2DimList [*] public;\n" +
          " -> (optClassWith2DimList)ClassWith2DimList [0..1] public;\n" +
          " -> (oneClassWith2DimList)ClassWith2DimList [1] public;\n" +
          " }\n" +
          " public class ClassWith2DimList { \n" +
          " public List<List<Integer>> my2dimList;\n" +
          " public List<List<Integer>> my2dimList2;\n" +
          " }\n" +
          " public class ClassWith2DimSet { \n" +
          " public Set<Set<Integer>> my2dimSet;\n" +
          " public Set<Set<Integer>> my2dimSet2;\n" +
          " }\n" +
          "public class ClassWithOptional { " +
          " public Optional<Integer> myOptionalInteger;\n" +
          " public Optional<Integer> myOptionalInteger2;\n" +
          "}\n " +
          "public class ClassWith2DimOptional { " +
          " public Optional<Optional<B>> my2DimOptional;\n" +
          " public Optional<Optional<B>> my2DimOptional2;\n" +
          "}\n " +
          "public class ClassWithPojoClassType { " +
          " public ClassWithPrimitiveType pojoType;\n" +
          " public ClassWithPrimitiveType pojoType2;\n" +
          "}\n " +
          "public class ClassWithPrimitiveType { " +
          " public int myInt;\n" +
          "}\n " +
          "public class ClassWithSet { " +
          " public Set<Integer> mySet;\n" +
          " public Set<Integer> mySet2;\n" +
          "}\n " +
          "public class ClassWithList { \n" +
          " public List<Integer> myIntegerList;\n" +
          " public List<Integer> myIntegerList2;\n" +
          "}  \n" +
          "public class ClassCircular1 { \n" +
          "public ClassCircular2 myClassCircular2;\n" +
          "}\n" +
          "public class ClassCircular2 { \n" +
          "public ClassCircular1 myClassCircular1;\n" +
          "}\n" +
          "public class ClassWithAssociation { \n" +
          "}\n" +
          "public class ClassWithNoDefaultConstructor {\n" +
          " public ClassWithNoDefaultConstructor(int i);\n" +
          " int i; \n" +
          "}\n" +
          "public class ClassWithComposition { \n" +
          "-> (opt)B [0..1] public;\n" +
          "-> (many)B [*] public;\n" +
          "-> (one)B [1] public;\n" +
          "-> (opt2)B [0..1] public;\n" +
          "-> (many2)B [*] public;\n" +
          "-> (one2)B [1] public;\n" +
          "}\n" +
          "public class ClassWithArray { \n" +
          " public ClassWithPrimitiveType[] arrayOfString; \n" +
          " public ClassWithPrimitiveType[] arrayOfString2; \n" +
          "}\n" +
          "public class ClassWith3DArray { \n" +
          " public ClassWithPrimitiveType[][][] threeDimArrayOfString; \n" +
          " public ClassWithPrimitiveType[][][] threeDimArrayOfString2; \n" +
          "}\n" +
          "public class ClassWithString { \n" +
          " public String myString;\n" +
          " public String myString2;\n" +
          "}\n" +
          "public class ClassWithMap { \n" +
          " public Map<String, B> myMap;\n" +
          " public Map<String, B> myMap2;\n" +
          "}\n" +
          "public class ClassWith2DMap { \n" +
          " public Map<String, Map<String,B>> myMap;\n" +
          " public Map<String, Map<String,B>> myMap2;\n" +
          "}\n" +
          "public class B { \n" +
          "}\n" +
          "association [1] AllTogether (owner) -> (owns) B [*]public; "+
          "association [1] ClassWithAssociation (owner) -> (owns) B [*]public; "+
          "association [1] ClassWithAssociation (owner2) -> (owns2) B [*]public; "+
          "}");

    Assertions.assertTrue(opt.isPresent());

    super.doTest(opt.get());
  }

  @Test
  public void testTemplateExistence() {
    //test existence of the templates
    List<Path> templatePaths= new ArrayList<>();
    templatePaths.add(Paths.get("src/main/resources/methods/deepCloneAndDeepEquals/deepClone.ftl"));
    //TODO add more later
    for (Path temPath: templatePaths) {
      Assertions.assertTrue(Files.exists(temPath));
    }
  }
}
