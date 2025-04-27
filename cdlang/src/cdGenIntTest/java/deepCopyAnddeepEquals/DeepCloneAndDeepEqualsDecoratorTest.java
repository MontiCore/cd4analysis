package deepCopyAnddeepEquals;

import TestDeepCloneAndDeepEquals.OtherC;
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

    Assertions.assertTrue();



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
