package de.monticore.cddiff.syntax2semdiff.SemDiff;
public class DataStructure {

  public static class Pair<T> {
    private T first;
    private T second;
    private DiffTypes myEnum;

    public Pair(T first, T second, DiffTypes myEnum) {
      this.first = first;
      this.second = second;
      this.myEnum = myEnum;
    }

    public T getFirst() {
      return first;
    }

    public T getSecond() {
      return second;
    }

    public DiffTypes getEnum() {
      return myEnum;
    }
  }
}
