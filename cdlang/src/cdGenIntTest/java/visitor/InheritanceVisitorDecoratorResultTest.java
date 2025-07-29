/* (c) https://github.com/MontiCore/monticore */
package visitor;

import TestInheritanceVisitor.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InheritanceVisitorDecoratorResultTest {

  Visitor visitor;

  @Test
  public void test() {
    testPrimitiveTypes();
    testStringTypes();
    testArrayTypes();
    testPojoClassTypes();
    testListTypes();
    testSetTypes();
    testOptionalTypes();
    testMapTypes();
    testAssociationTypes();
    testCompositionTypes();
    testCircularRelations();
    testAllTogether();
    testClassToBeTopped();
    testInterfaceAndInherit();
    testCorrectVisitorCallOrder();
  }

  @Test
  public void testPrimitiveTypes() {
    ClassWithPrimitiveType classWithPrimitiveType = new ClassWithPrimitiveType();
    classWithPrimitiveType.myInt = 1;
    visitor = new Visitor();
    classWithPrimitiveType.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithPrimitiveType);
    Assertions.assertSame(1, visitor.countEndVisitClassWithPrimitiveType);
    //check every stack is empty
    Assertions.assertTrue(visitor.isAllEmpty());
  }

  @Test
  public void testStringTypes() {
    ClassWithString classWithString = new ClassWithString();
    classWithString.myString = "string";
    classWithString.myString2 = "string2";
    visitor = new Visitor();
    classWithString.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithString);
    Assertions.assertSame(1, visitor.countEndVisitClassWithString);
    //check every stack is empty
    Assertions.assertTrue(visitor.isAllEmpty());
  }

  @Test
  public void testArrayTypes() {
    ClassWithArray classWithArray = new ClassWithArray();
    ClassWithPrimitiveType classWithPrimitiveType = new ClassWithPrimitiveType();
    classWithArray.arrayOfString = new ClassWithPrimitiveType[] { classWithPrimitiveType };
    visitor = new Visitor();
    classWithArray.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithArray);
    Assertions.assertSame(1, visitor.countEndVisitClassWithArray);
    Assertions.assertSame(1, visitor.countVisitClassWithPrimitiveType);
    Assertions.assertSame(1, visitor.countEndVisitClassWithPrimitiveType);
    classWithArray.arrayOfString2 = new ClassWithPrimitiveType[] { classWithPrimitiveType };
    visitor = new Visitor();
    classWithArray.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithArray);
    Assertions.assertSame(1, visitor.countEndVisitClassWithArray);
    Assertions.assertSame(2, visitor.countVisitClassWithPrimitiveType);
    Assertions.assertSame(2, visitor.countEndVisitClassWithPrimitiveType);

    ClassWith3DimArray classWith3DimArray = new ClassWith3DimArray();
    classWith3DimArray.threeDimArrayOfString = new ClassWithPrimitiveType[][][] { { {
        classWithPrimitiveType }, { classWithPrimitiveType } } };
    visitor = new Visitor();
    classWith3DimArray.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWith3DimArray);
    Assertions.assertSame(1, visitor.countEndVisitClassWith3DimArray);
    Assertions.assertSame(2, visitor.countEndVisitClassWithPrimitiveType);
    Assertions.assertSame(2, visitor.countVisitClassWithPrimitiveType);
    classWith3DimArray.threeDimArrayOfString2 = new ClassWithPrimitiveType[][][] { { {
        classWithPrimitiveType }, { classWithPrimitiveType } } };
    visitor = new Visitor();
    classWith3DimArray.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWith3DimArray);
    Assertions.assertSame(1, visitor.countEndVisitClassWith3DimArray);
    Assertions.assertSame(4, visitor.countEndVisitClassWithPrimitiveType);
    Assertions.assertSame(4, visitor.countVisitClassWithPrimitiveType);
    //check every stack is empty
    Assertions.assertTrue(visitor.isAllEmpty());
  }

  @Test
  public void testPojoClassTypes() {
    ClassWithPojoClassType classWithPojoClassType = new ClassWithPojoClassType();
    ClassWithPrimitiveType classWithPrimitiveType = new ClassWithPrimitiveType();
    classWithPojoClassType.pojoType = classWithPrimitiveType;
    visitor = new Visitor();
    classWithPojoClassType.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithPojoClassType);
    Assertions.assertSame(1, visitor.countEndVisitClassWithPojoClassType);
    Assertions.assertSame(1, visitor.countVisitClassWithPrimitiveType);
    Assertions.assertSame(1, visitor.countEndVisitClassWithPrimitiveType);
    classWithPojoClassType.pojoType2 = classWithPrimitiveType;
    visitor = new Visitor();
    classWithPojoClassType.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithPojoClassType);
    Assertions.assertSame(1, visitor.countEndVisitClassWithPojoClassType);
    Assertions.assertSame(2, visitor.countVisitClassWithPrimitiveType);
    Assertions.assertSame(2, visitor.countEndVisitClassWithPrimitiveType);
    //check every stack is empty
    Assertions.assertTrue(visitor.isAllEmpty());
  }

  @Test
  public void testListTypes() {
    ArrayList<Integer> oneDimArrayList = new ArrayList<>();
    ClassWithList classWithList = new ClassWithList();
    classWithList.myIntegerList = oneDimArrayList;
    List<List<Integer>> twoDimArrayList = new ArrayList<>();
    visitor = new Visitor();
    classWithList.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithList);
    Assertions.assertSame(1, visitor.countEndVisitClassWithList);

    ClassWith2DimList classWith2DimList = new ClassWith2DimList();
    classWith2DimList.my2dimList = twoDimArrayList;
    visitor = new Visitor();
    classWith2DimList.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWith2DimList);
    Assertions.assertSame(1, visitor.countEndVisitClassWith2DimList);
    //check every stack is empty
    Assertions.assertTrue(visitor.isAllEmpty());
  }

  @Test
  public void testSetTypes() {
    ClassWithSet classWithSet = new ClassWithSet();
    Set<Integer> oneDimHashSet = new HashSet<>();
    Set<Set<Integer>> twoDimHashSet = new HashSet<>();
    twoDimHashSet.add(oneDimHashSet);
    classWithSet.mySet = oneDimHashSet;
    visitor = new Visitor();
    classWithSet.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithSet);
    Assertions.assertSame(1, visitor.countEndVisitClassWithSet);

    ClassWith2DimSet classWith2DimSet = new ClassWith2DimSet();
    classWith2DimSet.my2dimSet = twoDimHashSet;
    visitor = new Visitor();
    classWith2DimSet.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWith2DimSet);
    Assertions.assertSame(1, visitor.countEndVisitClassWith2DimSet);
    //check every stack is empty
    Assertions.assertTrue(visitor.isAllEmpty());
  }

  @Test
  public void testOptionalTypes() {
    ClassWithOptional classWithOptional = new ClassWithOptional();
    classWithOptional.myOptionalInteger = Optional.of(Integer.MAX_VALUE);
    visitor = new Visitor();
    classWithOptional.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithOptional);
    Assertions.assertSame(1, visitor.countEndVisitClassWithOptional);

    classWithOptional.myOptionalInteger2 = Optional.empty();
    visitor = new Visitor();
    classWithOptional.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithOptional);
    Assertions.assertSame(1, visitor.countEndVisitClassWithOptional);

    ClassWith2DimOptional classWith2DimOptional = new ClassWith2DimOptional();
    classWith2DimOptional.my2DimOptional = Optional.empty();
    classWith2DimOptional.my2DimOptional2 = Optional.of(Optional.empty());
    visitor = new Visitor();
    classWith2DimOptional.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWith2DimOptional);
    Assertions.assertSame(1, visitor.countEndVisitClassWith2DimOptional);

    classWith2DimOptional.my2DimOptional = Optional.of(Optional.of(new B()));
    visitor = new Visitor();
    classWith2DimOptional.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWith2DimOptional);
    Assertions.assertSame(1, visitor.countEndVisitClassWith2DimOptional);
    Assertions.assertSame(1, visitor.countVisitB);
    Assertions.assertSame(1, visitor.countEndVisitB);

    classWith2DimOptional.my2DimOptional2 = Optional.of(Optional.of(new B()));
    visitor = new Visitor();
    classWith2DimOptional.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWith2DimOptional);
    Assertions.assertSame(1, visitor.countEndVisitClassWith2DimOptional);
    Assertions.assertSame(2, visitor.countVisitB);
    Assertions.assertSame(2, visitor.countEndVisitB);
    //check every stack is empty
    Assertions.assertTrue(visitor.isAllEmpty());
  }

  @Test
  public void testMapTypes() {
    ClassWithMap classWithMap = new ClassWithMap();
    HashMap<String, B> oneDimHashMap = new HashMap<>();
    oneDimHashMap.put("first", new B());
    HashMap<String, Map<String, B>> twoDimHashMap = new HashMap<>();
    twoDimHashMap.put("String", oneDimHashMap);
    classWithMap.myMap = oneDimHashMap;
    visitor = new Visitor();
    classWithMap.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithMap);
    Assertions.assertSame(1, visitor.countEndVisitClassWithMap);
    Assertions.assertSame(1, visitor.countVisitB);
    Assertions.assertSame(1, visitor.countEndVisitB);

    classWithMap.myMap2 = oneDimHashMap;
    visitor = new Visitor();
    classWithMap.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithMap);
    Assertions.assertSame(1, visitor.countEndVisitClassWithMap);
    Assertions.assertSame(2, visitor.countVisitB);
    Assertions.assertSame(2, visitor.countEndVisitB);

    ClassWith2DimMap classWith2DimMap = new ClassWith2DimMap();
    classWith2DimMap.myMap = twoDimHashMap;
    visitor = new Visitor();
    classWith2DimMap.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWith2DimMap);
    Assertions.assertSame(1, visitor.countEndVisitClassWith2DimMap);
    Assertions.assertSame(1, visitor.countVisitB);
    Assertions.assertSame(1, visitor.countEndVisitB);

    classWith2DimMap.myMap2 = twoDimHashMap;
    visitor = new Visitor();
    classWith2DimMap.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWith2DimMap);
    Assertions.assertSame(1, visitor.countEndVisitClassWith2DimMap);
    Assertions.assertSame(2, visitor.countVisitB);
    Assertions.assertSame(2, visitor.countEndVisitB);
    //check every stack is empty
    Assertions.assertTrue(visitor.isAllEmpty());
  }

  @Test
  public void testAssociationTypes() {
    ClassWithAssociation classWithAssociation = new ClassWithAssociation();
    Set<B> setOfB = new HashSet<>();
    setOfB.add(new B());
    classWithAssociation.owns = setOfB;
    visitor = new Visitor();
    classWithAssociation.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithAssociation);
    Assertions.assertSame(1, visitor.countEndVisitClassWithAssociation);
    Assertions.assertSame(1, visitor.countVisitB);
    Assertions.assertSame(1, visitor.countEndVisitB);

    classWithAssociation.owns2 = setOfB;
    visitor = new Visitor();
    classWithAssociation.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithAssociation);
    Assertions.assertSame(1, visitor.countEndVisitClassWithAssociation);
    Assertions.assertSame(2, visitor.countVisitB);
    Assertions.assertSame(2, visitor.countEndVisitB);
    //check every stack is empty
    Assertions.assertTrue(visitor.isAllEmpty());
  }

  @Test
  public void testCompositionTypes() {
    ClassWithComposition classWithComposition = new ClassWithComposition();
    Set<B> setOfB = new HashSet<>();
    setOfB.add(new B());
    classWithComposition.one = new B();
    visitor = new Visitor();
    classWithComposition.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithComposition);
    Assertions.assertSame(1, visitor.countEndVisitClassWithComposition);
    Assertions.assertSame(1, visitor.countVisitB);
    Assertions.assertSame(1, visitor.countEndVisitB);

    classWithComposition.many = setOfB;
    visitor = new Visitor();
    classWithComposition.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithComposition);
    Assertions.assertSame(1, visitor.countEndVisitClassWithComposition);
    Assertions.assertSame(2, visitor.countVisitB);
    Assertions.assertSame(2, visitor.countEndVisitB);

    classWithComposition.opt = Optional.of(new B());
    visitor = new Visitor();
    classWithComposition.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithComposition);
    Assertions.assertSame(1, visitor.countEndVisitClassWithComposition);
    Assertions.assertSame(3, visitor.countVisitB);
    Assertions.assertSame(3, visitor.countEndVisitB);

    classWithComposition.opt = Optional.empty();
    visitor = new Visitor();
    classWithComposition.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithComposition);
    Assertions.assertSame(1, visitor.countEndVisitClassWithComposition);
    Assertions.assertSame(2, visitor.countVisitB);
    Assertions.assertSame(2, visitor.countEndVisitB);

    classWithComposition.opt = Optional.of(new B());
    classWithComposition.opt2 = Optional.of(new B());
    classWithComposition.many2 = setOfB;
    classWithComposition.one2 = new B();
    visitor = new Visitor();
    classWithComposition.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassWithComposition);
    Assertions.assertSame(1, visitor.countEndVisitClassWithComposition);
    Assertions.assertSame(6, visitor.countVisitB);
    Assertions.assertSame(6, visitor.countEndVisitB);
    //check every stack is empty
    Assertions.assertTrue(visitor.isAllEmpty());
  }

  @Test
  public void testCircularRelations() {
    ClassCircular1 classCircular1 = new ClassCircular1();
    ClassCircular2 classCircular2 = new ClassCircular2();
    classCircular1.myClassCircular2 = classCircular2;
    classCircular2.myClassCircular1 = classCircular1;
    visitor = new Visitor();
    classCircular1.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassCircular1);
    Assertions.assertSame(1, visitor.countEndVisitClassCircular1);
    Assertions.assertSame(1, visitor.countVisitClassCircular2);
    Assertions.assertSame(1, visitor.countEndVisitClassCircular2);
    visitor = new Visitor();
    classCircular2.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassCircular1);
    Assertions.assertSame(1, visitor.countEndVisitClassCircular1);
    Assertions.assertSame(1, visitor.countVisitClassCircular2);
    Assertions.assertSame(1, visitor.countEndVisitClassCircular2);

    ClassCircular1 classCircular12 = new ClassCircular1();
    ClassCircular2 classCircular22 = new ClassCircular2();
    classCircular1.myClassCircular2 = classCircular2;
    classCircular2.myClassCircular1 = classCircular12;
    classCircular12.myClassCircular2 = classCircular22;
    classCircular22.myClassCircular1 = classCircular1;
    visitor = new Visitor();
    classCircular2.accept(visitor);
    Assertions.assertSame(2, visitor.countVisitClassCircular1);
    Assertions.assertSame(2, visitor.countEndVisitClassCircular1);
    Assertions.assertSame(2, visitor.countVisitClassCircular2);
    Assertions.assertSame(2, visitor.countEndVisitClassCircular2);
    //check every stack is empty
    Assertions.assertTrue(visitor.isAllEmpty());
  }

  @Test
  public void testAllTogether() {
    AllTogether allTogether = new AllTogether();
    ClassWith2DimList classWith2DimList = new ClassWith2DimList();
    Set<B> setOfB = new HashSet<>();
    setOfB.add(new B());
    allTogether.myInt = 1;
    allTogether.myBool = true;
    allTogether.manyClassWith2DimList = new HashSet<>(List.of(classWith2DimList));
    visitor = new Visitor();
    allTogether.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitAllTogether);
    Assertions.assertSame(1, visitor.countEndVisitAllTogether);
    Assertions.assertSame(1, visitor.countVisitClassWith2DimList);
    Assertions.assertSame(1, visitor.countEndVisitClassWith2DimList);

    allTogether.owns = setOfB;
    visitor = new Visitor();
    allTogether.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitAllTogether);
    Assertions.assertSame(1, visitor.countEndVisitAllTogether);
    Assertions.assertSame(1, visitor.countVisitClassWith2DimList);
    Assertions.assertSame(1, visitor.countEndVisitClassWith2DimList);
    Assertions.assertSame(1, visitor.countVisitB);
    Assertions.assertSame(1, visitor.countEndVisitB);

    allTogether.oneClassWith2DimList = classWith2DimList;
    visitor = new Visitor();
    allTogether.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitAllTogether);
    Assertions.assertSame(1, visitor.countEndVisitAllTogether);
    Assertions.assertSame(2, visitor.countVisitClassWith2DimList);
    Assertions.assertSame(2, visitor.countEndVisitClassWith2DimList);
    Assertions.assertSame(1, visitor.countVisitB);
    Assertions.assertSame(1, visitor.countEndVisitB);

    allTogether.optClassWith2DimList = Optional.empty();
    visitor = new Visitor();
    allTogether.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitAllTogether);
    Assertions.assertSame(1, visitor.countEndVisitAllTogether);
    Assertions.assertSame(2, visitor.countVisitClassWith2DimList);
    Assertions.assertSame(2, visitor.countEndVisitClassWith2DimList);
    Assertions.assertSame(1, visitor.countVisitB);
    Assertions.assertSame(1, visitor.countEndVisitB);

    allTogether.optClassWith2DimList = Optional.of(classWith2DimList);
    visitor = new Visitor();
    allTogether.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitAllTogether);
    Assertions.assertSame(1, visitor.countEndVisitAllTogether);
    Assertions.assertSame(3, visitor.countVisitClassWith2DimList);
    Assertions.assertSame(3, visitor.countEndVisitClassWith2DimList);
    Assertions.assertSame(1, visitor.countVisitB);
    Assertions.assertSame(1, visitor.countEndVisitB);
    //test all others are 0
    Assertions.assertSame(0, visitor.countVisitClassWithMap);
    Assertions.assertSame(0, visitor.countEndVisitClassWithMap);
    Assertions.assertSame(0, visitor.countVisitClassCircular1);
    Assertions.assertSame(0, visitor.countEndVisitClassCircular1);
    Assertions.assertSame(0, visitor.countVisitClassCircular2);
    Assertions.assertSame(0, visitor.countEndVisitClassCircular2);
    Assertions.assertSame(3, visitor.countVisitClassWith2DimList);
    Assertions.assertSame(3, visitor.countEndVisitClassWith2DimList);
    Assertions.assertSame(0, visitor.countVisitClassWith2DimOptional);
    Assertions.assertSame(0, visitor.countEndVisitClassWith2DimOptional);
    Assertions.assertSame(0, visitor.countVisitClassWith2DimSet);
    Assertions.assertSame(0, visitor.countEndVisitClassWith2DimSet);
    Assertions.assertSame(0, visitor.countVisitClassWith2DimMap);
    Assertions.assertSame(0, visitor.countEndVisitClassWith2DimMap);
    Assertions.assertSame(0, visitor.countVisitClassWith3DimArray);
    Assertions.assertSame(0, visitor.countEndVisitClassWith3DimArray);
    Assertions.assertSame(0, visitor.countVisitClassWithArray);
    Assertions.assertSame(0, visitor.countEndVisitClassWithArray);
    Assertions.assertSame(0, visitor.countVisitClassWithAssociation);
    Assertions.assertSame(0, visitor.countEndVisitClassWithAssociation);
    Assertions.assertSame(0, visitor.countVisitClassWithComposition);
    Assertions.assertSame(0, visitor.countEndVisitClassWithComposition);
    Assertions.assertSame(0, visitor.countVisitClassWithList);
    Assertions.assertSame(0, visitor.countEndVisitClassWithList);
    Assertions.assertSame(0, visitor.countEndVisitClassWithOptional);
    Assertions.assertSame(0, visitor.countVisitClassWithOptional);
    Assertions.assertSame(0, visitor.countEndVisitClassWithPojoClassType);
    Assertions.assertSame(0, visitor.countVisitClassWithPojoClassType);
    Assertions.assertSame(0, visitor.countEndVisitClassWithPrimitiveType);
    Assertions.assertSame(0, visitor.countVisitClassWithPrimitiveType);
    Assertions.assertSame(0, visitor.countEndVisitClassWithSet);
    Assertions.assertSame(0, visitor.countVisitClassWithSet);
    Assertions.assertSame(0, visitor.countEndVisitClassWithString);
    Assertions.assertSame(0, visitor.countVisitClassWithString);
    Assertions.assertSame(1, visitor.countVisitAllTogether);
    Assertions.assertSame(1, visitor.countEndVisitAllTogether);
    Assertions.assertSame(1, visitor.countVisitB);
    Assertions.assertSame(1, visitor.countEndVisitB);
    //check every stack is empty
    Assertions.assertTrue(visitor.isAllEmpty());
  }

  @Test
  public void testClassToBeTopped() {
    ClassToBeTopped classToBeTopped = new ClassToBeTopped();
    classToBeTopped.pojoType = new ClassWithPrimitiveType();
    visitor = new Visitor();
    classToBeTopped.accept(visitor);
    Assertions.assertSame(1, visitor.countVisitClassToBeTopped);
    Assertions.assertSame(1, visitor.countEndVisitClassToBeTopped);
    Assertions.assertSame(1, visitor.countVisitClassWithPrimitiveType);
    Assertions.assertSame(1, visitor.countEndVisitClassWithPrimitiveType);
    //check every stack is empty
    Assertions.assertTrue(visitor.isAllEmpty());
  }

  /**
   * Test for interface Lists and list with inheritance if the right visitor methods are called.
   */
  @Test
  public void testInterfaceAndInherit() {
    Set<Level1Interface> level1InterfacesList = new HashSet<>();
    Level2class level2class1 = new Level2class();
    level2class1.myInt = 1;
    Level2class level2class2 = new Level2class();
    level2class2.myInt = 2;
    Level3class level3class1 = new Level3class();
    level3class1.myInt = 3;
    Level3class level3class2 = new Level3class();
    level3class2.myInt = 4;
    level1InterfacesList.add(level2class1);
    level1InterfacesList.add(level2class2);
    level1InterfacesList.add(level3class1);
    level1InterfacesList.add(level3class2);
    Level0class level0class = new Level0class();
    level0class.many = level1InterfacesList;
    visitor = new Visitor();
    level0class.accept(visitor);
    Assertions.assertSame(4, visitor.countVisitLevel1Interface);
    Assertions.assertSame(4, visitor.countEndVisitLevel1Interface);
    Assertions.assertSame(1, visitor.countVisitLevel0class);
    Assertions.assertSame(1, visitor.countEndVisitLevel0class);
    Assertions.assertSame(4, visitor.countVisitLevel2class);
    Assertions.assertSame(4, visitor.countEndVisitLevel2class);
    Assertions.assertSame(2, visitor.countVisitLevel3class);
    Assertions.assertSame(2, visitor.countEndVisitLevel3class);

    Level4class level4class = new Level4class();
    level4class.myInt = 5;
    Level5class level5class1 = new Level5class();
    level5class1.myInt = 6;
    Level5class level5class2 = new Level5class();
    level5class2.myInt = 7;
    level1InterfacesList.add(level4class);
    level1InterfacesList.add(level5class1);
    level1InterfacesList.add(level5class2);
    level0class.many = level1InterfacesList;
    visitor = new Visitor();
    Assertions.assertSame(0, visitor.countVisitLevel1Interface);
    level0class.accept(visitor);
    //1 Level0Class has a list of: 2 Level2Class, 2 Level3Class, 1 Level4Class, 2 Level5Class
    Assertions.assertSame(1, visitor.countVisitLevel0class);
    Assertions.assertSame(1, visitor.countEndVisitLevel0class);

    Assertions.assertSame(7, visitor.countVisitLevel1Interface);
    Assertions.assertSame(7, visitor.countEndVisitLevel1Interface);

    Assertions.assertSame(7, visitor.countVisitLevel2class);
    Assertions.assertSame(7, visitor.countEndVisitLevel2class);
    Assertions.assertSame(5, visitor.countVisitLevel2Interface);
    Assertions.assertSame(5, visitor.countEndVisitLevel2Interface);
    Assertions.assertSame(2, visitor.countVisitLevel2Interface1);
    Assertions.assertSame(2, visitor.countEndVisitLevel2Interface1);
    Assertions.assertSame(2, visitor.countVisitLevel2Interface2);
    Assertions.assertSame(2, visitor.countEndVisitLevel2Interface2);

    Assertions.assertSame(5, visitor.countVisitLevel3class);
    Assertions.assertSame(5, visitor.countEndVisitLevel3class);
    Assertions.assertSame(2, visitor.countVisitLevel3Interface1);
    Assertions.assertSame(2, visitor.countEndVisitLevel3Interface1);
    Assertions.assertSame(2, visitor.countVisitLevel3Interface2);
    Assertions.assertSame(2, visitor.countEndVisitLevel3Interface2);

    Assertions.assertSame(3, visitor.countVisitLevel4class);
    Assertions.assertSame(3, visitor.countEndVisitLevel4class);
    Assertions.assertSame(2, visitor.countVisitLevel4Interface);
    Assertions.assertSame(2, visitor.countEndVisitLevel4Interface);

    Assertions.assertSame(2, visitor.countVisitLevel5class);
    Assertions.assertSame(2, visitor.countEndVisitLevel5class);

    Assertions.assertSame(0, visitor.countVisitClassWithMap);
    Assertions.assertSame(0, visitor.countEndVisitClassWithMap);
    Assertions.assertSame(0, visitor.countVisitClassCircular1);
    Assertions.assertSame(0, visitor.countEndVisitClassCircular1);
    Assertions.assertSame(0, visitor.countVisitClassCircular2);
    Assertions.assertSame(0, visitor.countEndVisitClassCircular2);
    Assertions.assertSame(0, visitor.countVisitClassWith2DimList);
    Assertions.assertSame(0, visitor.countEndVisitClassWith2DimList);
    Assertions.assertSame(0, visitor.countVisitClassWith2DimOptional);
    Assertions.assertSame(0, visitor.countEndVisitClassWith2DimOptional);
    Assertions.assertSame(0, visitor.countVisitClassWith2DimSet);
    Assertions.assertSame(0, visitor.countEndVisitClassWith2DimSet);
    Assertions.assertSame(0, visitor.countVisitClassWith2DimMap);
    Assertions.assertSame(0, visitor.countEndVisitClassWith2DimMap);
    Assertions.assertSame(0, visitor.countVisitClassWith3DimArray);
    Assertions.assertSame(0, visitor.countEndVisitClassWith3DimArray);
    Assertions.assertSame(0, visitor.countVisitClassWithArray);
    Assertions.assertSame(0, visitor.countEndVisitClassWithArray);
    Assertions.assertSame(0, visitor.countVisitClassWithAssociation);
    Assertions.assertSame(0, visitor.countEndVisitClassWithAssociation);
    Assertions.assertSame(0, visitor.countVisitClassWithComposition);
    Assertions.assertSame(0, visitor.countEndVisitClassWithComposition);
    Assertions.assertSame(0, visitor.countVisitClassWithList);
    Assertions.assertSame(0, visitor.countEndVisitClassWithList);
    Assertions.assertSame(0, visitor.countEndVisitClassWithOptional);
    Assertions.assertSame(0, visitor.countVisitClassWithOptional);
    Assertions.assertSame(0, visitor.countEndVisitClassWithPojoClassType);
    Assertions.assertSame(0, visitor.countVisitClassWithPojoClassType);
    Assertions.assertSame(0, visitor.countEndVisitClassWithPrimitiveType);
    Assertions.assertSame(0, visitor.countVisitClassWithPrimitiveType);
    Assertions.assertSame(0, visitor.countVisitClassWithSet);
    Assertions.assertSame(0, visitor.countEndVisitClassWithString);
    Assertions.assertSame(0, visitor.countVisitClassWithString);
    Assertions.assertSame(0, visitor.countVisitAllTogether);
    Assertions.assertSame(0, visitor.countEndVisitAllTogether);
    Assertions.assertSame(0, visitor.countVisitB);
    Assertions.assertSame(0, visitor.countEndVisitB);
    Assertions.assertSame(0, visitor.countVisitClassToBeTopped);
    Assertions.assertSame(0, visitor.countEndVisitClassToBeTopped);
    //check every stack is empty
    Assertions.assertTrue(visitor.isAllEmpty());
  }

  @Test
  public void testCorrectVisitorCallOrder() {
    try {
      File myObj = new File(
        "target/cdGenOutTest/InheritanceVisitorDecoratorTest/TestInheritanceVisitor/ITestInheritanceVisitorInheritanceVisitor.java");
      Scanner myReader = new Scanner(myObj);
      StringBuilder stringBuilder = new StringBuilder();
      while (myReader.hasNextLine()) {
        stringBuilder.append(myReader.nextLine());
      }
      myReader.close();


      String sourceCode = stringBuilder.toString();

      Pattern pattern = Pattern.compile("public\\s+void\\s+handle\\s*\\(\\s*TestInheritanceVisitor\\.Level5class\\s+\\w+\\s*\\)\\s*\\{");
      Matcher matcher = pattern.matcher(sourceCode);

      if (!matcher.find()) {
        throw new IllegalStateException("handle(...) method not found.");
      }

      int startIndex = matcher.start();
      int braceCount = 0;
      int endIndex = -1;

      for (int i = startIndex; i < sourceCode.length(); i++) {
        char ch = sourceCode.charAt(i);
        if (ch == '{') braceCount++;
        else if (ch == '}') braceCount--;

        if (braceCount == 0) {
          endIndex = i + 1;
          break;
        }
      }

      if (endIndex == -1) {
        throw new IllegalStateException("Method braces not balanced.");
      }

      String handleMethod = sourceCode.substring(startIndex, endIndex);

      List<Integer> visitLevels = extractLevelNumbers(handleMethod, "visit\\(\\(TestInheritanceVisitor\\.Level(\\d+).*?\\)node\\);");
      List<Integer> endVisitLevels = extractLevelNumbers(handleMethod, "endVisit\\(\\(TestInheritanceVisitor\\.Level(\\d+).*?\\)node\\);");

      // Check that visit levels are in ascending order
      List<Integer> sortedVisits = new ArrayList<>(visitLevels);
      Collections.sort(sortedVisits);
      Assertions.assertEquals(sortedVisits, visitLevels);

      // Check that endVisit levels are in descending order
      List<Integer> sortedEndVisits = new ArrayList<>(endVisitLevels);
      sortedEndVisits.sort(Collections.reverseOrder());
      Assertions.assertEquals(sortedEndVisits, endVisitLevels);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

    private List<Integer> extractLevelNumbers(String methodBody, String regex) {
      List<Integer> levels = new ArrayList<>();
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(methodBody);
      while (matcher.find()) {
        levels.add(Integer.parseInt(matcher.group(1)));
      }
      return levels;
    }

}
