/* (c) https://github.com/MontiCore/monticore */
package observer;

import TestObserver.*;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Test the result of the Getter & NavigableSetter Decorator.
 */
public class NavigableObserverDecoratorResultTest {
  
  @BeforeEach
  public void setup() {
    LogStub.init();
  }
  
  @Test
  public void testBi() throws Exception {
    List<String> caList = new ArrayList<>();
    
    CA ca = new CA();
    ca.addObserver(new ICAObserver() {
      
      @Override
      public void notifyUpdateSetCB(CA clazz, CB ov) {
        caList.add("notifyUpdateSetCB");
      }
      
      @Override
      public void notifyUpdateAddCC(CA clazz, CC newElem) {
        caList.add("notifyUpdateAddCC");
      }
      
      @Override
      public void notifyUpdateRemoveCC(CA clazz, CC elem) {
        caList.add("notifyUpdateRemoveCC");
      }
      
      @Override
      public void notifyUpdateSetCD(CA clazz, Optional<CD> ov) {
        caList.add("notifyUpdateSetCD");
      }
      
    });
    
    ca.setCB(new CB());
    assertAction(caList, "notifyUpdateSetCB");
    new CB().setCA(ca);
    assertAction(caList, "notifyUpdateSetCB");
    
    var cc1 = new CC();
    var cc2 = new CC();
    ca.addCC(cc1);
    assertAction(caList, "notifyUpdateAddCC");
    ca.addCC(cc1); // already added
    Assertions.assertEquals(0, caList.size());
    cc2.setCA(ca); // add via the other direction
    assertAction(caList, "notifyUpdateAddCC");
    ca.removeCC(cc1);
    assertAction(caList, "notifyUpdateRemoveCC");
    ca.removeCC(cc2);
    assertAction(caList, "notifyUpdateRemoveCC");
    ca.removeCC(cc2); // already removed
    Assertions.assertEquals(0, caList.size());
    
    ca.setCD(new CD());
    assertAction(caList, "notifyUpdateSetCD");
    ca.setCDAbsent();
    assertAction(caList, "notifyUpdateSetCD");
    new CD().setCA(ca);
    assertAction(caList, "notifyUpdateSetCD");
    
    // end
    Assertions.assertEquals(0, caList.size());
  }
  
  protected void assertAction(List<String> list, String expected) {
    String head = list.get(0);
    Assertions.assertEquals(expected, head);
    list.remove(0);
  }
  
}
