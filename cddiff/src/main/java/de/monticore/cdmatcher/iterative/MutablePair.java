/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative;

import java.util.Objects;

public class MutablePair<T, V> {
  
  private T a;
  private V b;
  
  public MutablePair(T a, V b) {
    this.a = a;
    this.b = b;
  }
  
  public T getA() { return a; }
  
  public void setA(T a) { this.a = a; }
  
  public V getB() { return b; }
  
  public void setB(V b) { this.b = b; }
  
  public String toString() {
    return "(" + a + ", " + b + ")";
  }
  
  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass())
      return false;
    MutablePair<?, ?> that = (MutablePair<?, ?>) o;
    return Objects.equals(a, that.a) && Objects.equals(b, that.b);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(a, b);
  }
  
}
