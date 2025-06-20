package de.monticore.cdmatcher;

public class MutablePair<T, V> {
  private T a;
  private V b;

  public MutablePair(T a, V b) {
    this.a = a;
    this.b = b;
  }

  public T getA() {
    return a;
  }

  public void setA(T a) {
    this.a = a;
  }

  public V getB() {
    return b;
  }

  public void setB(V b) {
    this.b = b;
  }

  public String toString() {
    return "(" + a + ", " + b + ")";
  }

  public int hashCode() {
    return a.hashCode() ^ b.hashCode();
  }

  public boolean equals(Object obj) {
    if(obj instanceof MutablePair) {
      MutablePair<?, ?> pair = (MutablePair<?, ?>) obj;
      return a.equals(pair.a) && b.equals(pair.b);
    }
    return false;
  }
}
