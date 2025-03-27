/* (c) https://github.com/MontiCore/monticore */
package getter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import TestGetter.Other;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test the result of the Getter Decorator. When we arrive in this test, the output compiles
 * correctly
 */
public class GetterDecoratorTest {

  @Test
  public void test() throws Exception {
    var obj = new TestGetterCImpl();

    Assertions.assertEquals(0, obj.getMyInt());
    obj.__setMyInt(42);
    Assertions.assertEquals(42, obj.getMyInt());

    // Check if the boolean is prefixed with is & has the package default visibility
    Method isMyBool = TestGetter.TestGetterC.class.getDeclaredMethod("isMyBool");
    var modifier = BigInteger.valueOf(isMyBool.getModifiers());
    Assertions.assertFalse(modifier.testBit(Modifier.PUBLIC));
    Assertions.assertFalse(modifier.testBit(Modifier.PRIVATE));
    Assertions.assertFalse(modifier.testBit(Modifier.PROTECTED));
    Assertions.assertEquals(0, modifier.intValue());

    // Test NoGetter / public
    Assertions.assertEquals(0, obj.pubX);

    // Ensure no getPubX() method exists
    //noinspection JavaReflectionMemberAccess
    Assertions.assertThrows(
        java.lang.NoSuchMethodException.class,
        () -> TestGetter.TestGetterC.class.getDeclaredMethod("getPubX"));

    // Test the initial size of lists/sets
    Assertions.assertEquals(0, obj.__getRoleB().size());
    Assertions.assertEquals(0, obj.__getOrderedRole().size());
  }

  // Add a setter for tests
  static class TestGetterCImpl extends TestGetter.TestGetterC {
    protected void __setMyInt(int i) {
      this.myInt = i;
    }

    protected Set<Other> __getRoleB() {
      return this.roleB;
    }

    protected List<Other> __getOrderedRole() {
      return this.orderedRole;
    }
  }
}
