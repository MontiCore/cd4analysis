package deepCopyAnddeepEquals;

import TestDeepCloneAndDeepEquals.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
    init();

    //deepEquals
    testDeepEqualsPrimitiveTypes();
    testDeepEqualsPojoTypes();
    testDeepEqualsListType();
    testDeepEqualsSetType();
    testDeepEqualsOptionalType();
    testDeepEqualsCircularRelations();
    testDeepEqualsAssociation();
    testDeepEqualsComposition();
    testDeepEqualsTogether();
    
    //deepCopy
    //TODO
  }

  public static void init() {
    //create equal sets and lists
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
  }

  public void testDeepEqualsPrimitiveTypes() {
    ClassWithPrimitiveType c1 = new ClassWithPrimitiveType();
    ClassWithPrimitiveType c2 = new ClassWithPrimitiveType();
    c1.myInt = 1;
    c2.myInt = 1;
    Assertions.assertTrue(c1.deepEquals(c2));
    c1.myInt = 2;
    c2.myInt = 1;
    Assertions.assertFalse(c1.deepEquals(c2));
  }

  public void testDeepEqualsPojoTypes() throws Exception {
    ClassWithPrimitiveType c1 = new ClassWithPrimitiveType();
    ClassWithPrimitiveType c2 = new ClassWithPrimitiveType();
    c1.myInt = 1;
    c2.myInt = 1;
    ClassWithPojoClassType c3 = new ClassWithPojoClassType();
    ClassWithPojoClassType c4 = new ClassWithPojoClassType();
    c3.pojoType = c1;
    c4.pojoType = c2;
    Assertions.assertTrue(c3.deepEquals(c4));
    c1.myInt=2;
    c2.myInt=1;
    Assertions.assertFalse(c3.deepEquals(c4));
    //null check
    c1.myInt=2;
    c2.myInt=1;
    c3.pojoType = null;
    c4.pojoType = null;
    Assertions.assertTrue(c3.deepEquals(c4));
  }

  public void testDeepEqualsListType(){
    ClassWithList c5 = new ClassWithList();
    ClassWithList c6 = new ClassWithList();
    c5.myIntegerList = listAbsent1;
    c6.myIntegerList = listAbsent2;
    Assertions.assertTrue(c5.deepEquals(c6));
    c5.myIntegerList = listDescent1;
    c6.myIntegerList = listAbsent2;
    Assertions.assertFalse(c5.deepEquals(c6));
    Assertions.assertFalse(c5.deepEquals(c6,true));
    Assertions.assertTrue(c5.deepEquals(c6,false));
    c5.myIntegerList = listAbsent1;
    c6.myIntegerList = listUnequal;
    Assertions.assertFalse(c5.deepEquals(c6));
    Assertions.assertFalse(c5.deepEquals(c6,true));
    Assertions.assertFalse(c5.deepEquals(c6,false));
    c5.myIntegerList=new ArrayList<>();
    c6.myIntegerList=new ArrayList<>();
    Assertions.assertTrue(c5.deepEquals(c6));
    Assertions.assertTrue(c5.deepEquals(c6,true));
    Assertions.assertTrue(c5.deepEquals(c6,false));
    //null check
    c5.myIntegerList=null;
    c6.myIntegerList=null;
    Assertions.assertTrue(c5.deepEquals(c6));

    //Test 2D list types
    ClassWith2DimList c11 = new ClassWith2DimList();
    ClassWith2DimList c12 = new ClassWith2DimList();
    c11.my2dimList = new ArrayList<>();
    c12.my2dimList = new ArrayList<>();
    c11.my2dimList.add(listAbsent1);
    c12.my2dimList.add(listAbsent2);
    c11.my2dimList.add(new ArrayList<>());
    c12.my2dimList.add(new ArrayList<>());
    Assertions.assertTrue(c11.deepEquals(c12));
    Assertions.assertTrue(c11.deepEquals(c12,false));
    Assertions.assertTrue(c11.deepEquals(c12,true));
    List<Integer> hSwap = c11.my2dimList.get(0);
    c11.my2dimList.set(0, c11.my2dimList.get(1));
    c11.my2dimList.set(1, hSwap);
    Assertions.assertFalse(c11.deepEquals(c12));
    Assertions.assertTrue(c11.deepEquals(c12,false));
    Assertions.assertFalse(c11.deepEquals(c12,true));
    c11.my2dimList.set(0, listDescent1);
    Assertions.assertFalse(c11.deepEquals(c12));
    Assertions.assertTrue(c11.deepEquals(c12,false));
    Assertions.assertFalse(c11.deepEquals(c12,true));
  }

  public void testDeepEqualsSetType(){
    ClassWithSet c7 = new ClassWithSet();
    ClassWithSet c8 = new ClassWithSet();
    c7.mySet = set1;
    c8.mySet = set2;
    Assertions.assertTrue(c7.deepEquals(c8));
    Assertions.assertTrue(c7.deepEquals(c8,false));
    Assertions.assertTrue(c7.deepEquals(c8,true));
    c7.mySet = setUnequal;
    c8.mySet = set2;
    Assertions.assertFalse(c7.deepEquals(c8));
    Assertions.assertFalse(c7.deepEquals(c8,false));
    Assertions.assertFalse(c7.deepEquals(c8,true));
    c7.mySet = new HashSet<>();
    c8.mySet = new HashSet<>();
    Assertions.assertTrue(c7.deepEquals(c8));
    Assertions.assertTrue(c7.deepEquals(c8,false));
    Assertions.assertTrue(c7.deepEquals(c8,true));
    //null check
    c7.mySet = null;
    c8.mySet = null;
    Assertions.assertTrue(c7.deepEquals(c8));

    //Test 2D set types
    ClassWith2DimSet c13 = new ClassWith2DimSet();
    ClassWith2DimSet c14 = new ClassWith2DimSet();
    c13.my2dimSet = new HashSet<>();
    c14.my2dimSet = new HashSet<>();
    c13.my2dimSet.add(set1);
    c14.my2dimSet.add(set2);
    c13.my2dimSet.add(new HashSet<>());
    c14.my2dimSet.add(new HashSet<>());
    Assertions.assertTrue(c13.deepEquals(c14));
    Assertions.assertTrue(c13.deepEquals(c14,false));
    Assertions.assertTrue(c13.deepEquals(c14,true));
    c14.my2dimSet = new HashSet<>();
    c14.my2dimSet.add(set2);
    c14.my2dimSet.add(new HashSet<>());
    Assertions.assertTrue(c13.deepEquals(c14));
    Assertions.assertTrue(c13.deepEquals(c14,false));
    Assertions.assertTrue(c13.deepEquals(c14,true));
  }

  public void testDeepEqualsOptionalType(){
    ClassWithOptional c9 = new ClassWithOptional();
    ClassWithOptional c10 = new ClassWithOptional();
    c9.myOptionalInteger = Optional.of(1);
    c10.myOptionalInteger = Optional.of(1);
    Assertions.assertTrue(c9.deepEquals(c10));
    Assertions.assertTrue(c9.deepEquals(c10,false));
    Assertions.assertTrue(c9.deepEquals(c10,true));
    c9.myOptionalInteger = Optional.of(2);
    c10.myOptionalInteger = Optional.of(1);
    Assertions.assertFalse(c9.deepEquals(c10));
    Assertions.assertFalse(c9.deepEquals(c10,false));
    Assertions.assertFalse(c9.deepEquals(c10,true));
    c9.myOptionalInteger = Optional.empty();
    c10.myOptionalInteger = Optional.empty();
    Assertions.assertTrue(c9.deepEquals(c10));
    c9.myOptionalInteger= Optional.of(1);
    Assertions.assertFalse(c9.deepEquals(c10));
    Assertions.assertFalse(c9.deepEquals(c10,false));
    Assertions.assertFalse(c9.deepEquals(c10,true));
    //null check
    c9.myOptionalInteger = null;
    c10.myOptionalInteger = null;
    Assertions.assertTrue(c9.deepEquals(c10));
    Assertions.assertTrue(c9.deepEquals(c10,false));
    Assertions.assertTrue(c9.deepEquals(c10,true));
  }

  public void testDeepEqualsCircularRelations(){
    //termination condition needs to be checked
    ClassCircular1 c11 = new ClassCircular1();
    ClassCircular1 c12 = new ClassCircular1();
    ClassCircular2 c13 = new ClassCircular2();
    ClassCircular2 c14 = new ClassCircular2();
    c11.myClassCircular2 = c13;
    c12.myClassCircular2 = c14;
    c13.myClassCircular1 = c11;
    c14.myClassCircular1 = c12;
    Assertions.assertTrue(c11.deepEquals(c12));
    Assertions.assertTrue(c11.deepEquals(c12,false));
    Assertions.assertTrue(c11.deepEquals(c12,true));
    c11.myClassCircular2 = null;
    Assertions.assertFalse(c11.deepEquals(c12));
    Assertions.assertFalse(c11.deepEquals(c12,false));
    Assertions.assertFalse(c11.deepEquals(c12,true));
  }

  public void testDeepEqualsAssociation(){
    //TODO
  }

  public void testDeepEqualsComposition(){
    //TODO
  }

  public void testDeepEqualsTogether(){
    //Test multiple types and multiple dimensions at the same time
    AllTogether c15 = new AllTogether();
    AllTogether c16 = new AllTogether();
    c15.owns= new HashSet<>();
    c16.owns= new HashSet<>();
    c15.myBool = true;
    c16.myBool = true;
    c15.myInt = 1;
    c16.myInt = 1;
    c15.manyClassWith2DimList = new HashSet<>();
    c16.manyClassWith2DimList = new HashSet<>();
    ClassWith2DimList c11 = new ClassWith2DimList();
    ClassWith2DimList c12 = new ClassWith2DimList();
    c11.my2dimList = new ArrayList<>();
    c12.my2dimList = new ArrayList<>();
    c11.my2dimList.add(listAbsent1);
    c12.my2dimList.add(listAbsent2);
    c11.my2dimList.add(new ArrayList<>());
    c12.my2dimList.add(new ArrayList<>());
    c15.manyClassWith2DimList.add(c11);
    c16.manyClassWith2DimList.add(c12);
    c15.oneClassWith2DimList = c11;
    c16.oneClassWith2DimList = c12;
    c15.optClassWith2DimList = Optional.empty();
    c16.optClassWith2DimList = Optional.empty();
    Assertions.assertTrue(c15.deepEquals(c16));
    Assertions.assertTrue(c15.deepEquals(c16,false));
    Assertions.assertTrue(c15.deepEquals(c16,true));
    c11.my2dimList = new ArrayList<>();
    Assertions.assertFalse(c15.deepEquals(c16));
    Assertions.assertFalse(c15.deepEquals(c16,false));
    Assertions.assertFalse(c15.deepEquals(c16,true));
    c11.my2dimList = new ArrayList<>();
    c11.my2dimList.add(new ArrayList<>());
    c11.my2dimList.add(listAbsent1);
    Assertions.assertFalse(c15.deepEquals(c16));
    Assertions.assertTrue(c15.deepEquals(c16,false));
    Assertions.assertFalse(c15.deepEquals(c16,true));
  }
}
