package cd4analysis.symboltable;

import de.cd4analysis._ast.ASTCardinality;

/**
 * Cardinality of an association end
 */
public class Cardinality {
  
  protected int min;
  
  protected int max;
  
  /**
   * Star-cardinality
   */
  public static final int STAR = -1;
  
  public Cardinality() {
    this.min = 1;
    this.max = 1;
  }
  
  /**
   * @param min cardinality minimum
   * @param max cardinality maximum
   */
  public Cardinality(int min, int max) {
    this.min = min;
    this.max = max;
  }
  
  /**
   * @return maximum (may be STAR)
   */
  public int getMax() {
    return this.max;
  }
  
  /**
   * @return minimum
   */
  public int getMin() {
    return this.min;
  }
  
  public static Cardinality convertCardinality(ASTCardinality aSTCard) {
    if (aSTCard == null) {
      return new Cardinality();
    }
    if (aSTCard.isMany()) {
      return new Cardinality(0, Cardinality.STAR);
    }
    if (aSTCard.isOne()) {
      return new Cardinality(1, 1);
    }
    if (aSTCard.isOptional()) {
      return new Cardinality(0, 1);
    }
    if (aSTCard.isOneToMany()) {
      return new Cardinality(1, Cardinality.STAR);
    }
    return new Cardinality();
  }
  
  public boolean isDefault() {
    return (min == 1 && max == 1);
  }
  
  public boolean isMultiple() {
    return (max > 1 || max == Cardinality.STAR);
  }
  
  public String printMax() {
    if (max == Cardinality.STAR) {
      return "*";
    }
    return String.valueOf(max);
  }
  
  public String printMin() {
    if (min == Cardinality.STAR) {
      return "*";
    }
    return String.valueOf(min);
  }
}
