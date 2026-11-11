/* (c) https://github.com/MontiCore/monticore */
package observer;

import TestObserver.B;
import TestObserver.OtherC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.*;

/**
 * Test the result of the Getter Decorator.
 */
public class ObserverDecoratorResultTest {
  
  @Test
  public void test() throws Exception {
    
    TestObserver.Observer observer = new TestObserver.Observer();
    TestObserver.Observer observer2 = new TestObserver.Observer();
    
    OtherC pojo = new OtherC();
    pojo.addObserver(observer);
    pojo.addObserver(observer2);
    
    B b = new B();
    
    Assertions.assertEquals(0, observer.getCountUpdateObserver());
    
    //check if setters are implemented correctly and call the observer
    pojo.setMyInt(42);
    Assertions.assertEquals(1, observer.getCountUpdateObserverMyInt());
    Assertions.assertEquals(1, observer2.getCountUpdateObserverMyInt());
    Assertions.assertEquals(1, observer.getCountUpdateObserver());
    
    pojo.setMyBool(true);
    Assertions.assertEquals(1, observer.getCountUpdateObserverMyBool());
    Assertions.assertEquals(1, observer2.getCountUpdateObserverMyBool());
    Assertions.assertEquals(2, observer.getCountUpdateObserver());
    
    pojo.removeManyB(b);
    Assertions.assertEquals(0, observer.getCountUpdateObserverManyB());
    Assertions.assertEquals(0, observer2.getCountUpdateObserverManyB());
    Assertions.assertEquals(2, observer.getCountUpdateObserver());
    
    pojo.addManyB(b);
    Assertions.assertEquals(1, observer.getCountUpdateObserverManyB());
    Assertions.assertEquals(1, observer2.getCountUpdateObserverManyB());
    Assertions.assertEquals(3, observer.getCountUpdateObserver());
    
    pojo.addManyB(b);
    Assertions.assertEquals(1, observer.getCountUpdateObserverManyB());
    Assertions.assertEquals(1, observer2.getCountUpdateObserverManyB());
    Assertions.assertEquals(3, observer.getCountUpdateObserver());
    
    pojo.removeManyB(b);
    Assertions.assertEquals(2, observer.getCountUpdateObserverManyB());
    Assertions.assertEquals(2, observer2.getCountUpdateObserverManyB());
    Assertions.assertEquals(4, observer.getCountUpdateObserver());
    
    pojo.setOptBAbsent();
    Assertions.assertEquals(1, observer.getCountUpdateObserverOptB());
    Assertions.assertEquals(1, observer2.getCountUpdateObserverOptB());
    Assertions.assertEquals(5, observer.getCountUpdateObserver());
    
    pojo.setOneB(null);
    Assertions.assertEquals(1, observer.getCountUpdateObserverOneB());
    Assertions.assertEquals(1, observer2.getCountUpdateObserverOneB());
    Assertions.assertEquals(6, observer.getCountUpdateObserver());
    
    //check if removeObserver works
    pojo.removeObserver(observer);
    
    pojo.setMyBool(false);
    Assertions.assertEquals(1, observer.getCountUpdateObserverMyBool());
    Assertions.assertEquals(2, observer2.getCountUpdateObserverMyBool());
    Assertions.assertEquals(6, observer.getCountUpdateObserver());
    Assertions.assertEquals(7, observer2.getCountUpdateObserver());
    
    pojo.removeObserver(observer2);
    
    pojo.setMyBool(false);
    Assertions.assertEquals(1, observer.getCountUpdateObserverMyBool());
    Assertions.assertEquals(2, observer2.getCountUpdateObserverMyBool());
    Assertions.assertEquals(6, observer.getCountUpdateObserver());
    Assertions.assertEquals(7, observer2.getCountUpdateObserver());
    
  }
  
}
