package de.monticore.cddiff.syndiff;

public class DataStructure {

  public static class DiffPair<T> {
    private T first;
    private T second;
    private DiffTypes myEnum;

    public DiffPair(T first, T second, DiffTypes myEnum) {
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
