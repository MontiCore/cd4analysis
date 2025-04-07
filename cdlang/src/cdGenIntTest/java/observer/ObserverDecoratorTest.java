/* (c) https://github.com/MontiCore/monticore */
package observer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.*;

/**
 * Test the result of the Getter Decorator. When we arrive in this test, the output compiles
 * correctly
 */
public class ObserverDecoratorTest {

  @Test
  public void test() throws Exception {
    //TODO if variables exists with name a and A we have a conflict
    checkMethodExistence();

    TestObserver.Observer observer = new TestObserver.Observer();
    TestObserver.Observer observer2 = new TestObserver.Observer();

    OtherC otherC = new OtherC();
    otherC.addObserver(observer);
    otherC.addObserver(observer2);


    B b = new B();
    Set<B> set = new HashSet<>(Set.of(b));
    otherC.notifyObserverManyB(otherC,set);

    Assertions.assertEquals(1, observer.getCountUpdateObserverManyB());
    Assertions.assertEquals(1, observer2.getCountUpdateObserverManyB());





  }

  private void checkMethodExistence() throws Exception {
    //check for the existence of the interfaces
    Class<?> interfaceObservable = Class.forName("TestObserver.IOtherCObservable");
    Assertions.assertTrue(Modifier.isPublic(interfaceObservable.getModifiers()));
    Assertions.assertTrue(interfaceObservable.isInterface());

    Class<?> interfaceObserver = Class.forName("TestObserver.IOtherCObserver");
    Assertions.assertTrue(Modifier.isPublic(interfaceObserver.getModifiers()));
    Assertions.assertTrue(interfaceObserver.isInterface());

    Class<?> clazz = Class.forName("TestObserver.OtherC");
    Assertions.assertTrue(Modifier.isPublic(clazz.getModifiers()));
    Assertions.assertFalse(clazz.isInterface());

    //check for the methods in the interface Observe
    Method[] methods = interfaceObservable.getDeclaredMethods();
    Assertions.assertEquals(9, methods.length);

    //check methods of the pojo
    Method addObserver = IOtherCObservable.class.getDeclaredMethod("addObserver", TestObserver.IOtherCObserver.class);
    Assertions.assertTrue(Modifier.isPublic(addObserver.getModifiers()));

    Method removeObserver = IOtherCObservable.class.getDeclaredMethod("removeObserver", TestObserver.IOtherCObserver.class);
    Assertions.assertTrue(Modifier.isPublic(removeObserver.getModifiers()));

    Method notifyObservers = IOtherCObservable.class.getDeclaredMethod("notifyObservers", OtherC.class);
    Assertions.assertTrue(Modifier.isPublic(notifyObservers.getModifiers()));

    Method notifyObserverInt = IOtherCObservable.class.getDeclaredMethod("notifyObserverMyInt", OtherC.class, int.class);
    Assertions.assertTrue(Modifier.isPublic(notifyObserverInt.getModifiers()));

    Method notifyObserverBoolean = IOtherCObservable.class.getDeclaredMethod("notifyObserverMyBool", OtherC.class, boolean.class);
    Assertions.assertTrue(Modifier.isPublic(notifyObserverBoolean.getModifiers()));

    Method notifyObserverSet = IOtherCObservable.class.getDeclaredMethod("notifyObserverManyB", OtherC.class, Set.class);
    Assertions.assertTrue(Modifier.isPublic(notifyObserverSet.getModifiers()));

    Method notifyObserverOptional = IOtherCObservable.class.getDeclaredMethod("notifyObserverOptB", OtherC.class, Optional.class);
    Assertions.assertTrue(Modifier.isPublic(notifyObserverOptional.getModifiers()));

    Method notifyObserverB = IOtherCObservable.class.getDeclaredMethod("notifyObserverOneB", OtherC.class, B.class);
    Assertions.assertTrue(Modifier.isPublic(notifyObserverB.getModifiers()));


    //check for the methods in the interface Observe
    Method notifyPojoAddObserver = OtherC.class.getDeclaredMethod("addObserver", TestObserver.IOtherCObserver.class);
    Assertions.assertTrue(Modifier.isPublic(notifyPojoAddObserver.getModifiers()));

    Method notifyPojoRemoveObserver = OtherC.class.getDeclaredMethod("removeObserver", TestObserver.IOtherCObserver.class);
    Assertions.assertTrue(Modifier.isPublic(notifyPojoRemoveObserver.getModifiers()));

    Method notifyPojoNotifyObservers = OtherC.class.getDeclaredMethod("notifyObservers", OtherC.class);
    Assertions.assertTrue(Modifier.isPublic(notifyPojoNotifyObservers.getModifiers()));

    Method notifyPojoNotifyObserverInt = OtherC.class.getDeclaredMethod("notifyObserverMyInt", OtherC.class, int.class);
    Assertions.assertTrue(Modifier.isPublic(notifyPojoNotifyObserverInt.getModifiers()));

    Method notifyPojoNotifyObserverBoolean = OtherC.class.getDeclaredMethod("notifyObserverMyBool", OtherC.class, boolean.class);
    Assertions.assertTrue(Modifier.isPublic(notifyPojoNotifyObserverBoolean.getModifiers()));

    Method notifyPojoNotifyObserverSet = OtherC.class.getDeclaredMethod("notifyObserverManyB", OtherC.class, Set.class);
    Assertions.assertTrue(Modifier.isPublic(notifyPojoNotifyObserverSet.getModifiers()));

    Method notifyPojoNotifyObserverOptional = OtherC.class.getDeclaredMethod("notifyObserverOptB", OtherC.class, Optional.class);
    Assertions.assertTrue(Modifier.isPublic(notifyPojoNotifyObserverOptional.getModifiers()));

    Method notifyPojoNotifyObserverB = OtherC.class.getDeclaredMethod("notifyObserverOneB", OtherC.class, B.class);
    Assertions.assertTrue(Modifier.isPublic(notifyPojoNotifyObserverB.getModifiers()));

    //check for the methods in the interface Observer
    Method update = IOtherCObserver.class.getDeclaredMethod("update", OtherC.class);
    Assertions.assertTrue(Modifier.isPublic(update.getModifiers()));

    Method updateObserverMyInt = IOtherCObserver.class.getDeclaredMethod("updateObserverMyInt", OtherC.class, int.class);
    Assertions.assertTrue(Modifier.isPublic(updateObserverMyInt.getModifiers()));

    Method updateObserverMyBool = IOtherCObserver.class.getDeclaredMethod("updateObserverMyBool", OtherC.class, boolean.class);
    Assertions.assertTrue(Modifier.isPublic(updateObserverMyBool.getModifiers()));

    Method updateObserverManyB = IOtherCObserver.class.getDeclaredMethod("updateObserverManyB", OtherC.class, Set.class);
    Assertions.assertTrue(Modifier.isPublic(updateObserverManyB.getModifiers()));

    Method updateObserverOptB = IOtherCObserver.class.getDeclaredMethod("updateObserverOptB", OtherC.class, Optional.class);
    Assertions.assertTrue(Modifier.isPublic(updateObserverOptB.getModifiers()));

    Method updateObserverOneB = IOtherCObserver.class.getDeclaredMethod("updateObserverOneB", OtherC.class, B.class);
    Assertions.assertTrue(Modifier.isPublic(updateObserverOneB.getModifiers()));
  }
}
