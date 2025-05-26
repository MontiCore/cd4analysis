package TestObserver;

import java.util.Optional;
import java.util.Set;

public class Observer implements TestObserver.IOtherCObserver {

  int countUpdateObserver = 0;
  int countUpdateObserverMyInt = 0;
  int countUpdateObserverMyBool = 0;
  int countUpdateObserverManyB = 0;
  int countUpdateObserverOptB = 0;
  int countUpdateObserverOneB = 0;
  int countUpdateObserverOv = 0;

  @Override
  public void update(OtherC clazz) {
    countUpdateObserver++;
  }

  @Override
  public void updateObserverMyInt(OtherC clazz, int ov) {
    countUpdateObserverMyInt++;
  }

  @Override
  public void updateObserverMyBool(OtherC clazz, boolean ov) {
    countUpdateObserverMyBool++;
  }

  @Override
  public void updateObserverManyB(OtherC clazz, Set<B> ov) {
    countUpdateObserverManyB++;
  }

  @Override
  public void updateObserverOptB(OtherC clazz, Optional<B> ov) {
    countUpdateObserverOptB++;
  }

  @Override
  public void updateObserverOneB(OtherC clazz, B ov) {
    countUpdateObserverOneB++;
  }

  @Override
  public void updateObserverOv(OtherC clazz, int ov) {
    countUpdateObserverOv++;
  }

  public int getCountUpdateObserver() {
    return countUpdateObserver;
  }

  public int getCountUpdateObserverMyInt() {
    return countUpdateObserverMyInt;
  }

  public int getCountUpdateObserverMyBool() {
    return countUpdateObserverMyBool;
  }

  public int getCountUpdateObserverManyB() {
    return countUpdateObserverManyB;
  }

  public int getCountUpdateObserverOptB() {
    return countUpdateObserverOptB;
  }

  public int getCountUpdateObserverOneB() {
    return countUpdateObserverOneB;
  }
}
