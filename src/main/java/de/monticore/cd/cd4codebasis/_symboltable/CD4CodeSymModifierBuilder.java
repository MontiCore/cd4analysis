/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cd4codebasis._symboltable;

import de.monticore.cd.cdbasis._symboltable.SymModifierBuilder;

public class CD4CodeSymModifierBuilder extends SymModifierBuilder {
  protected boolean isPrivate;
  protected boolean isProtected;
  protected boolean isPublic;
  protected boolean isDerived;
  protected boolean isReadOnly;

  public CD4CodeSymModifierBuilder setPrivate(boolean _private) {
    this.isPrivate = _private;
    return this;
  }

  public CD4CodeSymModifierBuilder setProtected(boolean _protected) {
    this.isProtected = _protected;
    return this;
  }

  public CD4CodeSymModifierBuilder setPublic(boolean _public) {
    this.isPublic = _public;
    return this;
  }

  public CD4CodeSymModifierBuilder setDerived(boolean _derived) {
    this.isDerived = _derived;
    return this;
  }

  public CD4CodeSymModifierBuilder setReadOnly(boolean _readOnly) {
    this.isReadOnly = _readOnly;
    return this;
  }

  public CD4CodeSymModifier build() {
    final CD4CodeSymModifier cd4CodeSymModifier = new CD4CodeSymModifier();
    build(cd4CodeSymModifier);
    return cd4CodeSymModifier;
  }

  public void build(CD4CodeSymModifier modifier) {
    super.build(modifier);
    modifier.setPrivate(this.isPrivate);
    modifier.setProtected(this.isProtected);
    modifier.setPublic(this.isPublic);
    modifier.setDerived(this.isDerived);
    modifier.setReadOnly(this.isReadOnly);
  }
}
