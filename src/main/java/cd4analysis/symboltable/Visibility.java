package cd4analysis.symboltable;

// TODO PN replace by access modifier
@Deprecated
public enum Visibility {

  PRIVATE("private"), DEFAULT(""), PROTECTED("protected"), PUBLIC("public"), LOCAL("local");

  private final String kind;

  private Visibility(String kind) {
    this.kind = kind;
  }

  /**
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return kind;
  }

}
