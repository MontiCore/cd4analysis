/* (c) https://github.com/MontiCore/monticore */
package getter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test the result of the Getter Decorator. When we arrive in this test, the output compiles
 * correctly
 */
public class GetterDecoratorResultTest {
  
  @Test
  public void test() throws Exception {
    var obj = new TestGetterCImpl();
    
    Assertions.assertEquals(0, obj.getMyInt());
    obj.__setMyInt(42);
    Assertions.assertEquals(42, obj.getMyInt());
    
    // Check if the boolean is prefixed with is & has the "public" default visibility (via default)
    Method isMyBool = TestGetter.TestGetterC.class.getDeclaredMethod("isMyBool");
    Assertions.assertTrue(Modifier.isPublic(isMyBool.getModifiers()));
    Assertions.assertFalse(Modifier.isPrivate(isMyBool.getModifiers()));
    Assertions.assertFalse(Modifier.isProtected(isMyBool.getModifiers()));
    
    // Test NoGetter / public
    Assertions.assertEquals(0, obj.pubX);
    
    // Ensure no getPubX() method exists
    //noinspection JavaReflectionMemberAccess
    Assertions.assertThrows(java.lang.NoSuchMethodException.class,
        () -> TestGetter.TestGetterC.class.getDeclaredMethod("getPubX"));
    
    // Test that the CardinalityDefaultDecorator run correctly
    Assertions.assertNotNull(obj.getRoleB());
    Assertions.assertNotNull(obj.getOrderedRole());
    
    // Test the initial size of lists/sets
    Assertions.assertEquals(0, obj.getRoleB().size());
    Assertions.assertEquals(0, obj.getOrderedRole().size());
  }
  
  // Add a setter for tests
  static class TestGetterCImpl extends TestGetter.TestGetterC {
    
    protected void __setMyInt(int i) {
      this.myInt = i;
    }
    
  }
  
}
