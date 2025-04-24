package deepCopyAnddeepEquals;

import TestDeepCloneAndDeepEquals.OtherC;
import TestObserver.IOtherCObservable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;

public class DeepCloneAndDeepEqualsDecoratorTest {

  @Test
  public void test() throws Exception {
    testMethodExistence();
    Assertions.assertTrue(true);
    //TODO


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
