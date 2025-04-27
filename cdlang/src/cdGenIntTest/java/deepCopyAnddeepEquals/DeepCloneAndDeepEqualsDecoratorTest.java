package deepCopyAnddeepEquals;

import TestDeepCloneAndDeepEquals.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class DeepCloneAndDeepEqualsDecoratorTest {

  @Test
  public void test() throws Exception {
    testMethodExistence();
    Assertions.assertTrue(true);
    //TODO
    //create equal set and lists
    List<Integer> listAbsent1 = new ArrayList<>();
    List<Integer> listAbsent2 = new ArrayList<>();
    List<Integer> listDescent1 = new ArrayList<>();
    List<Integer> listUnequal = new ArrayList<>();
    Set<Integer> set1 = new HashSet<>();
    Set<Integer> set2 = new HashSet<>();
    Set<Integer> setUnequal = new HashSet<>();
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

    //Test primitive types
    ClassWithPrimitiveType c1 = new ClassWithPrimitiveType();
    ClassWithPrimitiveType c2 = new ClassWithPrimitiveType();
    c1.myInt = 1;
    c2.myInt = 1;
    Assertions.assertTrue(c1.deepEquals(c2));
    c1.myInt = 2;
    c2.myInt = 1;
    Assertions.assertFalse(c1.deepEquals(c2));

    //Test pojo types
    ClassWithPojoClassType c3 = new ClassWithPojoClassType();
    ClassWithPojoClassType c4 = new ClassWithPojoClassType();
    c3.pojoType = c1;
    c4.pojoType = c2;
    c1.myInt=1;
    c2.myInt=1;
    Assertions.assertTrue(c3.deepEquals(c4));
    c1.myInt=2;
    c2.myInt=1;
    Assertions.assertFalse(c3.deepEquals(c4));

    //Test list types
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

    //Test set types
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

    //Test optional types
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
    
  }


  public void testMethodExistence() throws Exception {
    Class<?> OtherC = Class.forName("TestDeepCloneAndDeepEquals.OtherC");
    Assertions.assertTrue(Modifier.isPublic(OtherC.getModifiers()));

    Class<?> B = Class.forName("TestDeepCloneAndDeepEquals.B");
    Assertions.assertTrue(Modifier.isPublic(B.getModifiers()));

    //deepEquals methods
    Method deepEquals1OtherC = OtherC.class.getDeclaredMethod("deepEquals", Object.class);
    Assertions.assertEquals(Modifier.PUBLIC, deepEquals1OtherC.getModifiers());

    Method deepEquals2OtherC = OtherC.getDeclaredMethod("deepEquals", Object.class, boolean.class);
    Assertions.assertEquals(Modifier.PUBLIC, deepEquals2OtherC.getModifiers());

    Method deepEquals3OtherC = OtherC.getDeclaredMethod("deepEquals", Object.class, boolean.class, java.util.Set.class);
    Assertions.assertEquals(Modifier.PUBLIC, deepEquals3OtherC.getModifiers());

    Method deepEquals1B = B.getDeclaredMethod("deepEquals", Object.class);
    Assertions.assertEquals(Modifier.PUBLIC, deepEquals1B.getModifiers());

    Method deepEquals2B = B.getDeclaredMethod("deepEquals", Object.class, boolean.class);
    Assertions.assertEquals(Modifier.PUBLIC, deepEquals2B.getModifiers());

    Method deepEquals3B = B.getDeclaredMethod("deepEquals", Object.class, boolean.class, java.util.Set.class);
    Assertions.assertEquals(Modifier.PUBLIC, deepEquals3B.getModifiers());

    //deepClone methods
    Method deepCloneOtherC = OtherC.class.getDeclaredMethod("deepClone");
    Assertions.assertEquals(Modifier.PUBLIC, deepCloneOtherC.getModifiers());

    Method deepCloneB = B.getDeclaredMethod("deepClone");
    Assertions.assertEquals(Modifier.PUBLIC, deepCloneB.getModifiers());
  }
}
