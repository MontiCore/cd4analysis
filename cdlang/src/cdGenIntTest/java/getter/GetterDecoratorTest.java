/* (c) https://github.com/MontiCore/monticore */
package getter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;

/**
 * Test the result of the Getter Decorator.
 * When we arrive in this test, the output compiles correctly
 */
public class GetterDecoratorTest {

  @Test
  public void test() throws Exception {
    var obj = new TestGetterImpl();

    Assertions.assertEquals(0, obj.getMyInt());
    obj.__setMyInt(42);
    Assertions.assertEquals(42, obj.getMyInt());

    // Check if the boolean is prefixed with is & has the package default visibility
    Method isMyBool = TestGetter.TestGetter.class.getDeclaredMethod("isMyBool");
    var modifier = BigInteger.valueOf(isMyBool.getModifiers());
    Assertions.assertFalse(modifier.testBit(Modifier.PUBLIC));
    Assertions.assertFalse(modifier.testBit(Modifier.PRIVATE));
    Assertions.assertFalse(modifier.testBit(Modifier.PROTECTED));
    Assertions.assertEquals(0, modifier.intValue());

    // Test NoGetter / public
    Assertions.assertEquals(0, obj.pubX);

    // Ensure no getPubX() method exists
    //noinspection JavaReflectionMemberAccess
    Assertions.assertThrows(java.lang.NoSuchMethodException.class,
                            () -> TestGetter.TestGetter.class.getDeclaredMethod("getPubX"));
  }

  // Add a setter for tests
  static class TestGetterImpl extends TestGetter.TestGetter {

    protected void __setMyInt(int i) {
      this.myInt = i;
    }

  }
}
