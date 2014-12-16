package cd4analysis.symboltable;

public enum Relationship {
  
  ASSOCIATION("association"), AGGREGATE("aggregate"), COMPOSITE("composite"), PART("part");
  
  private final String kind;
  
  private Relationship(String kind) {
    this.kind = kind;
  }
  
  @Override
  public String toString() {
    return kind;
  }
  
}
