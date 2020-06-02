/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cdbasis._symboltable;

public class SymModifierBuilder {
  protected boolean isAbstract;
  protected boolean isFinal;
  protected boolean isStatic;

  public SymModifierBuilder setAbstract(boolean _abstract) {
    this.isAbstract = _abstract;
    return this;
  }

  public SymModifierBuilder setFinal(boolean _final) {
    this.isFinal = _final;
    return this;
  }

  public SymModifierBuilder setStatic(boolean _static) {
    this.isStatic = _static;
    return this;
  }

  public SymModifier build() {
    final SymModifier symModifier = new SymModifier();
    build(symModifier);
    return symModifier;
  }

  protected void build(SymModifier modifier) {
    modifier.setAbstract(this.isAbstract);
    modifier.setFinal(this.isFinal);
    modifier.setStatic(this.isStatic);
  }
}
