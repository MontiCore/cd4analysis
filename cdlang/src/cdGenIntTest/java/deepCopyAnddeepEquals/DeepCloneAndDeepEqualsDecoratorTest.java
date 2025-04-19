package deepCopyAnddeepEquals;

import TestDeepCloneAndDeepEquals.OtherC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;

public class DeepCloneAndDeepEqualsDecoratorTest {

  @Test
  public void test() throws Exception {
    Assertions.assertTrue(true);
    //TODO


  }

  public void testMethodExistence() throws NoSuchMethodException {
    //constructor methods
    Method methods = OtherC.class.getDeclaredMethod("deepClone");
    BigInteger constructorModifier = BigInteger.valueOf(methods.getModifiers());
    Assertions.assertEquals(Modifier.PUBLIC, constructorModifier.intValue());

  }
}
