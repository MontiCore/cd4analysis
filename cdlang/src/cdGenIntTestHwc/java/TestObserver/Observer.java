/* (c) https://github.com/MontiCore/monticore */
package TestObserver;

import java.util.Optional;

public class Observer implements TestObserver.IOtherCObserver {
  
  int countUpdateObserver = 0;
  int countUpdateObserverMyInt = 0;
  int countUpdateObserverMyBool = 0;
  int countUpdateObserverManyB = 0;
  int countUpdateObserverOptB = 0;
  int countUpdateObserverOneB = 0;
  int countUpdateObserverOv = 0;
  
  @Override
  public void notifyUpdate(OtherC clazz) {
    countUpdateObserver++;
  }
  
  @Override
  public void notifyUpdateSetMyInt(OtherC clazz, int ov) {
    countUpdateObserverMyInt++;
  }
  
  @Override
  public void notifyUpdateSetMyBool(OtherC clazz, boolean ov) {
    countUpdateObserverMyBool++;
  }
  
  @Override
  public void notifyUpdateSetOptB(OtherC clazz, Optional<B> ov) {
    countUpdateObserverOptB++;
  }
  
  @Override
  public void notifyUpdateSetOneB(OtherC clazz, B ov) {
    countUpdateObserverOneB++;
  }
  
  @Override
  public void notifyUpdateAddManyB(OtherC clazz, B newElem) {
    countUpdateObserverManyB++;
  }
  
  @Override
  public void notifyUpdateRemoveManyB(OtherC clazz, B elem) {
    countUpdateObserverManyB++;
  }
  
  @Override
  public void notifyUpdateSetOv(OtherC clazz, int ov) {
    countUpdateObserverOv++;
  }
  
  public int getCountUpdateObserver() { return countUpdateObserver; }
  
  public int getCountUpdateObserverMyInt() { return countUpdateObserverMyInt; }
  
  public int getCountUpdateObserverMyBool() { return countUpdateObserverMyBool; }
  
  public int getCountUpdateObserverManyB() { return countUpdateObserverManyB; }
  
  public int getCountUpdateObserverOptB() { return countUpdateObserverOptB; }
  
  public int getCountUpdateObserverOneB() { return countUpdateObserverOneB; }
  
}
