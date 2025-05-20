package deepCopyAnddeepEquals;

import TestDeepCloneAndDeepEquals.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.*;

public class DeepCloneAndDeepEqualsDecoratorTest {
  static List<Integer> listAbsent1 = new ArrayList<>();
  static List<Integer> listAbsent2 = new ArrayList<>();
  static List<Integer> listDescent1 = new ArrayList<>();
  static List<Integer> listUnequal = new ArrayList<>();
  static Set<Integer> set1 = new HashSet<>();
  static Set<Integer> set2 = new HashSet<>();
  static Set<Integer> setUnequal = new HashSet<>();

  @Test
  public void test() throws Exception {
    //region create equal sets and lists
    for(int i =0; i<= 10;i++){
      Integer absent1 = i;
      Integer absent2 = i;
      Integer descent = 10-i;
      Random rand = new Random();
      Integer unequal = rand.nextInt();
      listAbsent1.add(absent1);
      listAbsent2.add(absent2);
      listDescent1.add(descent);
      listUnequal.add(unequal);
      set1.add(absent1);
      set2.add(absent1);
      setUnequal.add(unequal);
    }
    //endregion

    //region DeepEquals
    //region deepEquals for primitive types
    ClassWithPrimitiveType de1 = new ClassWithPrimitiveType();
    ClassWithPrimitiveType de2 = new ClassWithPrimitiveType();
    de1.myInt = 1;
    de2.myInt = 1;
    Assertions.assertTrue(de1.deepEquals(de2));
    de1.myInt = 2;
    de2.myInt = 1;
    Assertions.assertFalse(de1.deepEquals(de2));
    //endregion
    //region deepEquals for String types
    ClassWithString deString1 = new ClassWithString();
    ClassWithString deString2 = new ClassWithString();
    deString1.myString = "test";
    deString2.myString = "test";
    Assertions.assertTrue(deString1.deepEquals(deString2));
    deString1.myString = "test1";
    deString2.myString = "test";
    Assertions.assertFalse(deString1.deepEquals(deString2));
    //null check
    deString1.myString = null;
    Assertions.assertFalse(deString1.deepEquals(deString2));
    deString2.myString = null;
    Assertions.assertTrue(deString1.deepEquals(deString2));
    //endregion
    //region deepEquals for pojo types
    de1.myInt = 1;
    de2.myInt = 1;
    ClassWithPojoClassType de3 = new ClassWithPojoClassType();
    ClassWithPojoClassType de4 = new ClassWithPojoClassType();
    de3.pojoType = de1;
    de4.pojoType = de2;
    Assertions.assertTrue(de3.deepEquals(de4));
    de1.myInt=2;
    de2.myInt=1;
    Assertions.assertFalse(de3.deepEquals(de4));
    //null check
    de1.myInt=2;
    de2.myInt=1;
    de3.pojoType = null;
    de4.pojoType = null;
    Assertions.assertTrue(de3.deepEquals(de4));
    //endregion
    //region deepEquals list types
    ClassWithList de5 = new ClassWithList();
    ClassWithList de6 = new ClassWithList();
    de5.myIntegerList = listAbsent1;
    de6.myIntegerList = listAbsent2;
    Assertions.assertTrue(de5.deepEquals(de6));
    de5.myIntegerList = listDescent1;
    de6.myIntegerList = listAbsent2;
    Assertions.assertFalse(de5.deepEquals(de6));
    Assertions.assertFalse(de5.deepEquals(de6,true));
    Assertions.assertTrue(de5.deepEquals(de6,false));
    de5.myIntegerList = listAbsent1;
    de6.myIntegerList = listUnequal;
    Assertions.assertFalse(de5.deepEquals(de6));
    Assertions.assertFalse(de5.deepEquals(de6,true));
    Assertions.assertFalse(de5.deepEquals(de6,false));
    de5.myIntegerList=new ArrayList<>();
    de6.myIntegerList=new ArrayList<>();
    Assertions.assertTrue(de5.deepEquals(de6));
    Assertions.assertTrue(de5.deepEquals(de6,true));
    Assertions.assertTrue(de5.deepEquals(de6,false));
    //null check
    de5.myIntegerList=null;
    de6.myIntegerList=null;
    Assertions.assertTrue(de5.deepEquals(de6));

    //Test 2D list types
    ClassWith2DimList de7 = new ClassWith2DimList();
    ClassWith2DimList de8 = new ClassWith2DimList();
    de7.my2dimList = new ArrayList<>();
    de8.my2dimList = new ArrayList<>();
    de7.my2dimList.add(listAbsent1);
    de8.my2dimList.add(listAbsent2);
    de7.my2dimList.add(new ArrayList<>());
    de8.my2dimList.add(new ArrayList<>());
    Assertions.assertTrue(de7.deepEquals(de8));
    Assertions.assertTrue(de7.deepEquals(de8,false));
    Assertions.assertTrue(de7.deepEquals(de8,true));
    List<Integer> hSwap = de7.my2dimList.get(0);
    de7.my2dimList.set(0, de7.my2dimList.get(1));
    de7.my2dimList.set(1, hSwap);
    Assertions.assertFalse(de7.deepEquals(de8));
    Assertions.assertTrue(de7.deepEquals(de8,false));
    Assertions.assertFalse(de7.deepEquals(de8,true));
    de7.my2dimList.set(0, listDescent1);
    Assertions.assertFalse(de7.deepEquals(de8));
    Assertions.assertTrue(de7.deepEquals(de8,false));
    Assertions.assertFalse(de7.deepEquals(de8,true));
    //endregion
    //region deepEquals set types
    ClassWithSet de9 = new ClassWithSet();
    ClassWithSet de10 = new ClassWithSet();
    de9.mySet = set1;
    de10.mySet = set2;
    Assertions.assertTrue(de9.deepEquals(de10));
    Assertions.assertTrue(de9.deepEquals(de10,false));
    Assertions.assertTrue(de9.deepEquals(de10,true));
    de9.mySet = setUnequal;
    de10.mySet = set2;
    Assertions.assertFalse(de9.deepEquals(de10));
    Assertions.assertFalse(de9.deepEquals(de10,false));
    Assertions.assertFalse(de9.deepEquals(de10,true));
    de9.mySet = new HashSet<>();
    de10.mySet = new HashSet<>();
    Assertions.assertTrue(de9.deepEquals(de10));
    Assertions.assertTrue(de9.deepEquals(de10,false));
    Assertions.assertTrue(de9.deepEquals(de10,true));
    //null check
    de9.mySet = null;
    de10.mySet = null;
    Assertions.assertTrue(de9.deepEquals(de10));

    //Test 2D set types
    ClassWith2DimSet de11 = new ClassWith2DimSet();
    ClassWith2DimSet de12 = new ClassWith2DimSet();
    de11.my2dimSet = new HashSet<>();
    de12.my2dimSet = new HashSet<>();
    de11.my2dimSet.add(set1);
    de12.my2dimSet.add(set2);
    de11.my2dimSet.add(new HashSet<>());
    de12.my2dimSet.add(new HashSet<>());
    Assertions.assertTrue(de11.deepEquals(de12));
    Assertions.assertTrue(de11.deepEquals(de12,false));
    Assertions.assertTrue(de11.deepEquals(de12,true));
    de12.my2dimSet = new HashSet<>();
    de12.my2dimSet.add(set2);
    de12.my2dimSet.add(new HashSet<>());
    Assertions.assertTrue(de11.deepEquals(de12));
    Assertions.assertTrue(de11.deepEquals(de12,false));
    Assertions.assertTrue(de11.deepEquals(de12,true));
    //endregion
    //region deepEquals optional types
    ClassWithOptional de13 = new ClassWithOptional();
    ClassWithOptional de14 = new ClassWithOptional();
    de13.myOptionalInteger = Optional.of(1);
    de14.myOptionalInteger = Optional.of(1);
    Assertions.assertTrue(de13.deepEquals(de14));
    Assertions.assertTrue(de13.deepEquals(de14,false));
    Assertions.assertTrue(de13.deepEquals(de14,true));
    de13.myOptionalInteger = Optional.of(2);
    de14.myOptionalInteger = Optional.of(1);
    Assertions.assertFalse(de13.deepEquals(de14));
    Assertions.assertFalse(de13.deepEquals(de14,false));
    Assertions.assertFalse(de13.deepEquals(de14,true));
    de13.myOptionalInteger = Optional.empty();
    de14.myOptionalInteger = Optional.empty();
    Assertions.assertTrue(de13.deepEquals(de14));
    de13.myOptionalInteger= Optional.of(1);
    Assertions.assertFalse(de13.deepEquals(de14));
    Assertions.assertFalse(de13.deepEquals(de14,false));
    Assertions.assertFalse(de13.deepEquals(de14,true));
    //null check
    de13.myOptionalInteger = null;
    de14.myOptionalInteger = null;
    Assertions.assertTrue(de13.deepEquals(de14));
    Assertions.assertTrue(de13.deepEquals(de14,false));
    Assertions.assertTrue(de13.deepEquals(de14,true));




    //Test 2Dim Optional
    ClassWith2DimOptional deO1 = new ClassWith2DimOptional();
    ClassWith2DimOptional deO2 = new ClassWith2DimOptional();
    deO1.my2DimOptional = Optional.of(Optional.of(new B()));
    Assertions.assertFalse(deO1.deepEquals(deO2));
    deO2.my2DimOptional= Optional.empty();
    Assertions.assertFalse(deO1.deepEquals(deO2));
    deO2.my2DimOptional= Optional.of(Optional.empty());
    Assertions.assertFalse(deO1.deepEquals(deO2));
    deO2.my2DimOptional= Optional.of(Optional.of(new B()));
    Assertions.assertTrue(deO1.deepEquals(deO2));
    //null check
    deO1 .my2DimOptional=null;
    deO2.my2DimOptional=null;
    Assertions.assertTrue(deO1.deepEquals(deO2));
    deO2 = null;
    Assertions.assertFalse(deO1.deepEquals(deO2));
    //endregion
    //region deepEquals map types
    ClassWithMap deMap1 = new ClassWithMap();
    ClassWithMap deMap2 = new ClassWithMap();
    deMap1.myMap = null;
    deMap2.myMap = null;
    Assertions.assertTrue(deMap1.deepEquals(deMap2));
    Assertions.assertTrue(deMap1.deepEquals(deMap2,false));
    Assertions.assertTrue(deMap1.deepEquals(deMap2,true));
    deMap1.myMap = new HashMap<>();
    Assertions.assertFalse(deMap1.deepEquals(deMap2));
    Assertions.assertFalse(deMap1.deepEquals(deMap2,false));
    Assertions.assertFalse(deMap1.deepEquals(deMap2,true));
    deMap2.myMap = new HashMap<>();
    Assertions.assertTrue(deMap1.deepEquals(deMap2));
    Assertions.assertTrue(deMap1.deepEquals(deMap2,false));
    Assertions.assertTrue(deMap1.deepEquals(deMap2,true));
    deMap1.myMap.put("key", new B());
    deMap2.myMap.put("key", new B());
    Assertions.assertTrue(deMap1.deepEquals(deMap2));
    Assertions.assertTrue(deMap1.deepEquals(deMap2,false));
    Assertions.assertTrue(deMap1.deepEquals(deMap2,true));
    //endregion
    //region deepEquals association types
    ClassWithAssociation de15 = new ClassWithAssociation();
    ClassWithAssociation de16 = new ClassWithAssociation();
    de15.owns = new HashSet<>();
    de16.owns = new HashSet<>();
    Assertions.assertTrue(de15.deepEquals(de16));
    Assertions.assertTrue(de15.deepEquals(de16,false));
    Assertions.assertTrue(de15.deepEquals(de16,true));
    B b1 = new B();
    de15.owns.add(b1);
    Assertions.assertFalse(de15.deepEquals(de16));
    Assertions.assertFalse(de15.deepEquals(de16,false));
    Assertions.assertFalse(de15.deepEquals(de16,true));
    de16.owns.add(b1);
    Assertions.assertTrue(de15.deepEquals(de16));
    Assertions.assertTrue(de15.deepEquals(de16,false));
    Assertions.assertTrue(de15.deepEquals(de16,true));
    //null check
    de15.owns = null;
    de16.owns = null;
    Assertions.assertTrue(de15.deepEquals(de16));
    Assertions.assertTrue(de15.deepEquals(de16,false));
    Assertions.assertTrue(de15.deepEquals(de16,true));
    //endregion
    //region deepEquals composition types
    ClassWithComposition de17 = new ClassWithComposition();
    ClassWithComposition de18 = new ClassWithComposition();
    de17.many = null;
    de18.many = null;
    de17.one = null;
    de18.one = null;
    de17.opt = null;
    de18.opt = null;
    Assertions.assertTrue(de17.deepEquals(de18));
    Assertions.assertTrue(de17.deepEquals(de18,false));
    Assertions.assertTrue(de17.deepEquals(de18,true));
    de17.many = new HashSet<>();
    Assertions.assertFalse(de17.deepEquals(de18,false));
    de17.many.add(new B());
    Assertions.assertFalse(de17.deepEquals(de18,false));
    de18.many=new HashSet<>();
    de18.many.add(new B());
    Assertions.assertTrue(de17.deepEquals(de18));
    Assertions.assertTrue(de17.deepEquals(de18,false));
    Assertions.assertTrue(de17.deepEquals(de18,true));
    de17.one = new B();
    Assertions.assertFalse(de17.deepEquals(de18));
    de18.one = new B();
    Assertions.assertTrue(de17.deepEquals(de18));
    Assertions.assertTrue(de17.deepEquals(de18,false));
    Assertions.assertTrue(de17.deepEquals(de18,true));
    de17.opt = Optional.empty();
    Assertions.assertFalse(de17.deepEquals(de18,false));
    de18.opt = Optional.of(new B());
    Assertions.assertFalse(de17.deepEquals(de18));
    Assertions.assertFalse(de17.deepEquals(de18,false));
    Assertions.assertFalse(de17.deepEquals(de18,true));
    de17.opt = Optional.of(new B());
    Assertions.assertTrue(de17.deepEquals(de18));
    Assertions.assertTrue(de17.deepEquals(de18,false));
    Assertions.assertTrue(de17.deepEquals(de18,true));
    //endregion
    //region termination condition needs to be checked in circular references
    ClassCircular1 de19 = new ClassCircular1();
    ClassCircular1 de20 = new ClassCircular1();
    ClassCircular2 c131 = new ClassCircular2();
    ClassCircular2 c141 = new ClassCircular2();
    de19.myClassCircular2 = c131;
    de20.myClassCircular2 = c141;
    c131.myClassCircular1 = de19;
    c141.myClassCircular1 = de20;
    Assertions.assertTrue(de19.deepEquals(de20));
    Assertions.assertTrue(de19.deepEquals(de20,false));
    Assertions.assertTrue(de19.deepEquals(de20,true));
    de19.myClassCircular2 = null;
    Assertions.assertFalse(de19.deepEquals(de20));
    Assertions.assertFalse(de19.deepEquals(de20,false));
    Assertions.assertFalse(de19.deepEquals(de20,true));
    //endregion
    //region Test multiple types and multiple dimensions at the same time
    AllTogether de21 = new AllTogether();
    AllTogether de22 = new AllTogether();
    de21.owns= new HashSet<>();
    de22.owns= new HashSet<>();
    de21.myBool = true;
    de22.myBool = true;
    de21.myInt = 1;
    de22.myInt = 1;
    de21.manyClassWith2DimList = new HashSet<>();
    de22.manyClassWith2DimList = new HashSet<>();
    ClassWith2DimList c112 = new ClassWith2DimList();
    ClassWith2DimList c122 = new ClassWith2DimList();
    c112.my2dimList = new ArrayList<>();
    c122.my2dimList = new ArrayList<>();
    c112.my2dimList.add(listAbsent1);
    c122.my2dimList.add(listAbsent2);
    c112.my2dimList.add(new ArrayList<>());
    c122.my2dimList.add(new ArrayList<>());
    de21.manyClassWith2DimList.add(c112);
    de22.manyClassWith2DimList.add(c122);
    de21.oneClassWith2DimList = c112;
    de22.oneClassWith2DimList = c122;
    de21.optClassWith2DimList = Optional.empty();
    de22.optClassWith2DimList = Optional.empty();
    Assertions.assertTrue(de21.deepEquals(de22));
    Assertions.assertTrue(de21.deepEquals(de22,false));
    Assertions.assertTrue(de21.deepEquals(de22,true));
    c112.my2dimList = new ArrayList<>();
    Assertions.assertFalse(de21.deepEquals(de22));
    Assertions.assertFalse(de21.deepEquals(de22,false));
    Assertions.assertFalse(de21.deepEquals(de22,true));
    c112.my2dimList = new ArrayList<>();
    c112.my2dimList.add(new ArrayList<>());
    c112.my2dimList.add(listAbsent1);
    Assertions.assertFalse(de21.deepEquals(de22));
    Assertions.assertTrue(de21.deepEquals(de22,false));
    Assertions.assertFalse(de21.deepEquals(de22,true));
    //endregion
    //endregion
    //region DeepClone
    //region deepClone for primitive types
    ClassWithPrimitiveType dc1 = new ClassWithPrimitiveType();
    dc1.myInt=0;
    ClassWithPrimitiveType dc2 = dc1.deepClone();
    Assertions.assertNotSame(dc1,dc2);
    Assertions.assertTrue(dc1.deepEquals(dc2));
    dc1.myInt = 1;
    Assertions.assertFalse(dc1.deepEquals(dc2));
    dc2 = dc1.deepClone();
    Assertions.assertNotSame(dc1,dc2);
    Assertions.assertTrue(dc1.deepEquals(dc2));
    //endregion
    //region deepClone for String types
    ClassWithString dcString1 = new ClassWithString();
    dcString1.myString = "test";
    ClassWithString dcString2 = dcString1.deepClone();
    Assertions.assertNotSame(dcString1,dcString2);
    Assertions.assertTrue(dcString1.deepEquals(dcString2));
    dcString1.myString = "test1";
    Assertions.assertFalse(dcString1.deepEquals(dcString2));
    //null check
    dcString1.myString = null;
    dcString2 = dcString1.deepClone();
    Assertions.assertNotSame(dcString1,dcString2);
    Assertions.assertTrue(dcString1.deepEquals(dcString2));
    Assertions.assertNull(dcString2.myString);
    //test Map correctness
    dcString1.myString = "test";
    dcString1.myString2 = dcString1.myString;
    dcString2 = dcString1.deepClone();
    Assertions.assertNotSame(dcString1,dcString2);
    Assertions.assertTrue(dcString1.deepEquals(dcString2));
    Assertions.assertSame(dcString2.myString,dcString2.myString2);
    dcString1.myString2 = null;
    dcString2 = dcString1.deepClone();
    Assertions.assertNotSame(dcString1,dcString2);
    Assertions.assertTrue(dcString1.deepEquals(dcString2));
    Assertions.assertNotSame(dcString2.myString,dcString2.myString2);
    //endregion
    //region deepClone for pojo types
    ClassWithPojoClassType dc3 = new ClassWithPojoClassType();
    dc3.pojoType = dc1;
    dc1.myInt=0;
    ClassWithPojoClassType dc4 = dc3.deepClone();
    Assertions.assertNotSame(dc3,dc4);
    Assertions.assertNotSame(dc3.pojoType,dc4.pojoType);
    Assertions.assertTrue(dc3.deepEquals(dc4));
    dc3.pojoType.myInt = 1;
    Assertions.assertFalse(dc3.deepEquals(dc4));
    dc4 = dc3.deepClone();
    Assertions.assertNotSame(dc3,dc4);
    Assertions.assertNotSame(dc3.pojoType,dc4.pojoType);
    Assertions.assertTrue(dc3.deepEquals(dc4));
    //null check
    dc3.pojoType = null;
    dc4 = dc3.deepClone();
    Assertions.assertNotSame(dc3,dc4);
    Assertions.assertTrue(dc3.deepEquals(dc4));
    Assertions.assertNull(dc4.pojoType);
    //test Map correctness
    dc3.pojoType = dc1;
    dc3.pojoType2 = dc3.pojoType;
    dc4 = dc3.deepClone();
    Assertions.assertNotSame(dc3,dc4);
    Assertions.assertNotSame(dc3.pojoType,dc4.pojoType);
    Assertions.assertNotSame(dc3.pojoType2,dc4.pojoType2);
    Assertions.assertTrue(dc3.deepEquals(dc4));
    Assertions.assertSame(dc4.pojoType,dc4.pojoType2);
    //endregion
    //region deepClone list types
    ClassWithList dc5 = new ClassWithList();
    dc5.myIntegerList = listAbsent1;
    ClassWithList dc6 = dc5.deepClone();
    Assertions.assertNotSame(dc5,dc6);
    Assertions.assertNotSame(dc5.myIntegerList,dc6.myIntegerList);
    Assertions.assertTrue(dc5.deepEquals(dc6));
    dc5.myIntegerList = listDescent1;
    dc6 = dc5.deepClone();
    Assertions.assertNotSame(dc5,dc6);
    Assertions.assertNotSame(dc5.myIntegerList,dc6.myIntegerList);
    Assertions.assertTrue(dc5.deepEquals(dc6));
    //null check
    dc5.myIntegerList = null;
    dc6 = dc5.deepClone();
    Assertions.assertTrue(dc5.deepEquals(dc6));
    Assertions.assertNull(dc6.myIntegerList);
    //test Map correctness
    dc5.myIntegerList = listAbsent1;
    dc5.myIntegerList2 = dc5.myIntegerList;
    dc6 = dc5.deepClone();
    Assertions.assertNotSame(dc5,dc6);
    Assertions.assertNotSame(dc5.myIntegerList,dc6.myIntegerList);
    Assertions.assertNotSame(dc5.myIntegerList2,dc6.myIntegerList2);
    Assertions.assertTrue(dc5.deepEquals(dc6));
    Assertions.assertSame(dc6.myIntegerList,dc6.myIntegerList2);

    //Test 2D list types
    ClassWith2DimList dc7 = new ClassWith2DimList();
    dc7.my2dimList= new ArrayList<>();
    dc7.my2dimList.add(listAbsent1);
    dc7.my2dimList.add(listAbsent1);
    dc7.my2dimList.add(new ArrayList<>());
    ClassWith2DimList dc8 = dc7.deepClone();
    Assertions.assertNotSame(dc7,dc8);
    Assertions.assertNotSame(dc7.my2dimList,dc8.my2dimList);
    Assertions.assertTrue(dc7.deepEquals(dc8));
    dc7.my2dimList = new ArrayList<>();
    Assertions.assertFalse(dc7.deepEquals(dc8));
    dc8 = dc7.deepClone();
    Assertions.assertNotSame(dc7,dc8);
    Assertions.assertNotSame(dc7.my2dimList,dc8.my2dimList);
    Assertions.assertTrue(dc7.deepEquals(dc8));
    //check for deepClone with zwo equal references inside the first list
    dc7.my2dimList = new ArrayList<>();
    dc7.my2dimList.add(listAbsent1);
    dc7.my2dimList.add(listAbsent1);
    dc8 = dc7.deepClone();
    Assertions.assertSame(dc8.my2dimList.get(0),dc8.my2dimList.get(1));
    //null check
    dc7.my2dimList = null;
    dc8 = dc7.deepClone();
    Assertions.assertTrue(dc7.deepEquals(dc8));
    Assertions.assertNull(dc8.my2dimList);
    //test map correctness
    dc7.my2dimList = new ArrayList<>();
    dc7.my2dimList2 = dc7.my2dimList;
    dc8 = dc7.deepClone();
    Assertions.assertNotSame(dc7,dc8);
    Assertions.assertNotSame(dc7.my2dimList,dc8.my2dimList);
    Assertions.assertNotSame(dc7.my2dimList2,dc8.my2dimList2);
    Assertions.assertTrue(dc7.deepEquals(dc8));
    Assertions.assertSame(dc8.my2dimList,dc8.my2dimList2);
    //endregion
    //region deepClone set types
    ClassWithSet dc9 = new ClassWithSet();
    dc9.mySet = set1;
    ClassWithSet dc10 = dc9.deepClone();
    Assertions.assertNotSame(dc9,dc10);
    Assertions.assertNotSame(dc9.mySet,dc10.mySet);
    Assertions.assertTrue(dc9.deepEquals(dc10));
    dc9.mySet = setUnequal;
    Assertions.assertFalse(dc9.deepEquals(dc10));
    dc10 = dc9.deepClone();
    Assertions.assertNotSame(dc9,dc10);
    Assertions.assertNotSame(dc9.mySet,dc10.mySet);
    Assertions.assertTrue(dc9.deepEquals(dc10));
    //null check
    dc9.mySet = null;
    dc10 = dc9.deepClone();
    Assertions.assertTrue(dc9.deepEquals(dc10));
    Assertions.assertNull(dc10.mySet);
    //test Map correctness
    dc9.mySet = set1;
    dc9.mySet2 = dc9.mySet;
    dc10 = dc9.deepClone();
    Assertions.assertNotSame(dc9,dc10);
    Assertions.assertNotSame(dc9.mySet,dc10.mySet);
    Assertions.assertNotSame(dc9.mySet2,dc10.mySet2);
    Assertions.assertTrue(dc9.deepEquals(dc10));
    Assertions.assertSame(dc10.mySet,dc10.mySet2);

    //Test 2D set types
    ClassWith2DimSet dc11 = new ClassWith2DimSet();
    dc11.my2dimSet = new HashSet<>();
    dc11.my2dimSet.add(set1);
    dc11.my2dimSet.add(set1);
    ClassWith2DimSet dc12 = dc11.deepClone();
    Assertions.assertNotSame(dc11,dc12);
    Assertions.assertNotSame(dc11.my2dimSet,dc12.my2dimSet);
    Assertions.assertTrue(dc11.deepEquals(dc12));
    dc11.my2dimSet = new HashSet<>();
    Assertions.assertFalse(dc11.deepEquals(dc12));
    dc12 = dc11.deepClone();
    Assertions.assertNotSame(dc11,dc12);
    Assertions.assertNotSame(dc11.my2dimSet,dc12.my2dimSet);
    Assertions.assertTrue(dc11.deepEquals(dc12));
    //check for deepClone with zwo equal references inside the first set
    //TODO this doesnt work as set will just compress the two elements
    // we need to have a 3 dim set. new hashSet().add(new HashSet()).add(new HastSet()) and the add the set on the third level
    dc11.my2dimSet = new HashSet<>();
    dc11.my2dimSet.add(set1);
    dc11.my2dimSet.add(set1);
    dc12 = dc11.deepClone();
    //Assertions.assertSame(dc12.my2dimSet.toArray()[0],dc12.my2dimSet.toArray()[1]);
    //null check
    dc11.my2dimSet = null;
    dc12 = dc11.deepClone();
    Assertions.assertTrue(dc11.deepEquals(dc12));
    Assertions.assertNull(dc12.my2dimSet);
    //test map correctness
    dc11.my2dimSet = new HashSet<>();
    dc11.my2dimSet2 = dc11.my2dimSet;
    dc12 = dc11.deepClone();
    Assertions.assertNotSame(dc11,dc12);
    Assertions.assertNotSame(dc11.my2dimSet,dc12.my2dimSet);
    Assertions.assertNotSame(dc11.my2dimSet2,dc12.my2dimSet2);
    Assertions.assertTrue(dc11.deepEquals(dc12));
    Assertions.assertSame(dc12.my2dimSet,dc12.my2dimSet2);
    //endregion
    //region deepClone optional types
    ClassWithOptional dc13 = new ClassWithOptional();
    dc13.myOptionalInteger = Optional.of(1);
    ClassWithOptional dc14 = dc13.deepClone();
    Assertions.assertNotSame(dc13,dc14);
    Assertions.assertNotSame(dc13.myOptionalInteger,dc14.myOptionalInteger);
    Assertions.assertTrue(dc13.deepEquals(dc14));
    dc13.myOptionalInteger = Optional.of(2);
    Assertions.assertFalse(dc13.deepEquals(dc14));
    dc14 = dc13.deepClone();
    Assertions.assertNotSame(dc13,dc14);
    Assertions.assertNotSame(dc13.myOptionalInteger,dc14.myOptionalInteger);
    Assertions.assertTrue(dc13.deepEquals(dc14));
    //null check
    dc13.myOptionalInteger = null;
    dc14 = dc13.deepClone();
    Assertions.assertTrue(dc13.deepEquals(dc14));
    Assertions.assertNull(dc14.myOptionalInteger);
    //test Map correctness
    Optional opt = Optional.of(1);
    dc13.myOptionalInteger = opt;
    dc13.myOptionalInteger2 = opt;
    dc14 = dc13.deepClone();
    Assertions.assertNotSame(dc13,dc14);
    //they are the same as Integer has no deepClone method therefore we just copy the reference
    //Assertions.assertNotSame(dc13.myOptionalInteger,dc14.myOptionalInteger);
    //Assertions.assertNotSame(dc13.myOptionalInteger2,dc14.myOptionalInteger2);
    Assertions.assertTrue(dc13.deepEquals(dc14));
    Assertions.assertSame(dc13.myOptionalInteger,dc13.myOptionalInteger2);
    Assertions.assertSame(dc14.myOptionalInteger,dc14.myOptionalInteger2);

    //Test 2D Optional
    ClassWith2DimOptional dcO1 = new ClassWith2DimOptional();
    ClassWith2DimOptional dcO2 = new ClassWith2DimOptional();
    dcO1.my2DimOptional = Optional.of(Optional.of(new B()));
    dcO2 = dcO1.deepClone();
    Assertions.assertNotSame(dcO1,dcO2);
    Assertions.assertNotSame(dcO1.my2DimOptional,dcO2.my2DimOptional);
    Assertions.assertTrue(dcO1.deepEquals(dcO2));
    dcO1.my2DimOptional = Optional.empty();
    Assertions.assertFalse(dcO1.deepEquals(dcO2));
    dcO2 = dcO1.deepClone();
    Assertions.assertNotSame(dcO1,dcO2);
    //Because Optional.empty() == Optional.empty() is true
    //Assertions.assertNotSame(dcO1.my2DimOptional,dcO3.my2DimOptional);
    Assertions.assertTrue(dcO1.deepEquals(dcO2));
    //null check
    dcO1.my2DimOptional = null;
    dcO2 = dcO1.deepClone();
    Assertions.assertTrue(dcO1.deepEquals(dcO2));
    Assertions.assertNull(dcO2.my2DimOptional);
    // further null checks are not possible because we can not set optional.of(null)
    //test Map correctness
    dcO1.my2DimOptional = Optional.of(Optional.of(new B()));
    dcO1.my2DimOptional2 = dcO1.my2DimOptional;
    dcO2 = dcO1.deepClone();
    Assertions.assertNotSame(dcO1,dcO2);
    Assertions.assertNotSame(dcO1.my2DimOptional,dcO2.my2DimOptional);
    Assertions.assertNotSame(dcO1.my2DimOptional2,dcO2.my2DimOptional2);
    Assertions.assertTrue(dcO1.deepEquals(dcO2));
    Assertions.assertSame(dcO2.my2DimOptional,dcO2.my2DimOptional2);
    //endregion
    //region deepClone association types
    ClassWithAssociation dc15 = new ClassWithAssociation();
    dc15.owns = new HashSet<>();
    ClassWithAssociation dc16 = dc15.deepClone();
    Assertions.assertNotSame(dc15,dc16);
    Assertions.assertTrue(dc15.deepEquals(dc16));
    dc15.owns.add(new B());
    Assertions.assertFalse(dc15.deepEquals(dc16));
    dc16 = dc15.deepClone();
    Assertions.assertNotSame(dc15,dc16);
    Assertions.assertNotSame(dc15.owns,dc16.owns);
    Assertions.assertTrue(dc15.deepEquals(dc16));
    //null check
    dc15.owns = null;
    dc16 = dc15.deepClone();
    Assertions.assertTrue(dc15.deepEquals(dc16));
    Assertions.assertNull(dc16.owns);
    //test Map correctness
    dc15.owns = new HashSet<>();
    dc15.owns2 = dc15.owns;
    dc16 = dc15.deepClone();
    Assertions.assertNotSame(dc15,dc16);
    Assertions.assertNotSame(dc15.owns,dc16.owns);
    Assertions.assertNotSame(dc15.owns2,dc16.owns2);
    Assertions.assertTrue(dc15.deepEquals(dc16));
    Assertions.assertSame(dc16.owns,dc16.owns2);
    //endregion
    //region deepClone composition types
    ClassWithComposition dc17 = new ClassWithComposition();
    dc17.many = new HashSet<>();
    ClassWithComposition dc18 = dc17.deepClone();
    Assertions.assertNotSame(dc17,dc18);
    Assertions.assertNotSame(dc17.many,dc18.many);
    Assertions.assertTrue(dc17.deepEquals(dc18));
    dc17.many.add(new B());
    Assertions.assertFalse(dc17.deepEquals(dc18));
    dc18 = dc17.deepClone();
    Assertions.assertNotSame(dc17,dc18);
    Assertions.assertNotSame(dc17.many,dc18.many);
    Assertions.assertTrue(dc17.deepEquals(dc18));
    dc17.one = new B();
    Assertions.assertFalse(dc17.deepEquals(dc18));
    dc18 = dc17.deepClone();
    Assertions.assertNotSame(dc17,dc18);
    Assertions.assertNotSame(dc17.one,dc18.one);
    Assertions.assertTrue(dc17.deepEquals(dc18));
    dc17.opt = Optional.of(new B());
    Assertions.assertFalse(dc17.deepEquals(dc18));
    dc18 = dc17.deepClone();
    Assertions.assertNotSame(dc17,dc18);
    Assertions.assertNotSame(dc17.opt,dc18.opt);
    Assertions.assertTrue(dc17.deepEquals(dc18));
    dc17.opt = Optional.empty();
    Assertions.assertFalse(dc17.deepEquals(dc18));
    dc18 = dc17.deepClone();
    Assertions.assertNotSame(dc17,dc18);
    //Assertions.assertNotSame(dc17.opt,dc18.opt);
    // as Optional.empty() == Optional.empty() is true
    Assertions.assertTrue(dc17.deepEquals(dc18));
    //null check
    dc17.many = null;
    dc18 = dc17.deepClone();
    Assertions.assertTrue(dc17.deepEquals(dc18));
    Assertions.assertNull(dc18.many);
    dc17.one = null;
    dc18 = dc17.deepClone();
    Assertions.assertTrue(dc17.deepEquals(dc18));
    Assertions.assertNull(dc18.one);
    dc17.opt = null;
    dc18 = dc17.deepClone();
    Assertions.assertTrue(dc17.deepEquals(dc18));
    Assertions.assertNull(dc18.opt);
    //test Map correctness
    dc17.many = new HashSet<>();
    dc17.many2 = dc17.many;
    dc17.one = new B();
    dc17.one2 = dc17.one;
    dc17.opt = Optional.of(new B());
    dc17.opt2 = dc17.opt;
    dc18 = dc17.deepClone();
    Assertions.assertNotSame(dc17,dc18);
    Assertions.assertNotSame(dc17.many,dc18.many);
    Assertions.assertNotSame(dc17.many2,dc18.many2);
    Assertions.assertNotSame(dc17.one,dc18.one);
    Assertions.assertNotSame(dc17.one2,dc18.one2);
    Assertions.assertNotSame(dc17.opt,dc18.opt);
    Assertions.assertNotSame(dc17.opt2,dc18.opt2);
    Assertions.assertTrue(dc17.deepEquals(dc18));
    Assertions.assertSame(dc18.many,dc18.many2);
    Assertions.assertSame(dc18.one,dc18.one2);
    Assertions.assertSame(dc18.opt,dc18.opt2);
    //endregion
    //region deepClone circular references
    ClassCircular1 dc19 = new ClassCircular1();
    ClassCircular2 dc20 = new ClassCircular2();
    dc19.myClassCircular2 = dc20;
    dc20.myClassCircular1 = dc19;
    ClassCircular1 dc21 = dc19.deepClone();
    Assertions.assertSame(dc21,dc21.myClassCircular2.myClassCircular1);
    Assertions.assertSame(dc21.myClassCircular2,dc21.myClassCircular2.myClassCircular1.myClassCircular2);
    Assertions.assertNotSame(dc19,dc21);
    Assertions.assertNotSame(dc19.myClassCircular2,dc21.myClassCircular2);
    //endregion
    //region check creation of same references
    ClassWith2DimList dc22 = new ClassWith2DimList();
    dc22.my2dimList = new ArrayList<>();
    dc22.my2dimList.add(listAbsent1);
    dc22.my2dimList.add(listAbsent1);
    ClassWith2DimList dc23 = dc22.deepClone();
    Assertions.assertSame(dc23.my2dimList.get(0),dc23.my2dimList.get(1));
    Assertions.assertNotSame(dc22,dc23);
    Assertions.assertNotSame(dc22.my2dimList,dc23.my2dimList);
    Assertions.assertTrue(dc22.my2dimList.get(0)==dc22.my2dimList.get(1) && dc23.my2dimList.get(0)==dc23.my2dimList.get(1));
    //endregion
    //region Test multiple types and multiple dimensions at the same time
    AllTogether dc24 = new AllTogether();
    dc24.owns = new HashSet<>();
    dc24.manyClassWith2DimList = new HashSet<>();
    dc24.myBool = true;
    dc24.myInt = -12121;
    dc24.optClassWith2DimList = Optional.empty();
    dc24.oneClassWith2DimList = null;
    AllTogether dc25 = dc24.deepClone();
    Assertions.assertNotSame(dc24,dc25);
    Assertions.assertTrue(dc24.deepEquals(dc25));
    Assertions.assertNotSame(dc24.manyClassWith2DimList,dc25.manyClassWith2DimList);
    Assertions.assertNotSame(dc24.owns,dc25.owns);
    //are the same as they are null values
    //Assertions.assertNotSame(dc24.oneClassWith2DimList,dc25.oneClassWith2DimList);
    //Assertions.assertNotSame(dc24.optClassWith2DimList,dc25.optClassWith2DimList);
    //Assertions.assertNotSame(dc24.manyClassWith2DimList,dc25.manyClassWith2DimList);
    ClassWith2DimList dc26 = new ClassWith2DimList();

    dc26.my2dimList = new ArrayList<>();
    dc26.my2dimList.add(listAbsent1);
    dc24.manyClassWith2DimList.add(dc26);
    dc24.optClassWith2DimList= Optional.of(dc26);
    dc24.oneClassWith2DimList = dc26;
    dc24.owns = null;
    dc25 = dc24.deepClone();
    Assertions.assertNotSame(dc24.oneClassWith2DimList,dc25.oneClassWith2DimList);
    Assertions.assertNotSame(dc24.optClassWith2DimList,dc25.optClassWith2DimList);
    Assertions.assertNotSame(dc24.manyClassWith2DimList,dc25.manyClassWith2DimList);
    Assertions.assertSame(dc25.manyClassWith2DimList.toArray()[0], dc25.oneClassWith2DimList);
    Assertions.assertSame(dc25.manyClassWith2DimList.toArray()[0],dc25.optClassWith2DimList.get());
    Assertions.assertSame(dc25.optClassWith2DimList.get(),dc25.oneClassWith2DimList);
    //endregion
    //endregion
    //check construction of default constructor if not present
    ClassWithNoDefaultConstructor classWithNoDefaultConstructor = new ClassWithNoDefaultConstructor(1);
    ClassWithNoDefaultConstructor classWithNoDefaultConstructor2 = classWithNoDefaultConstructor.deepClone();
    Assertions.assertTrue(classWithNoDefaultConstructor.deepEquals(classWithNoDefaultConstructor2));
  }
}
