package de.monticore.cd.cd4codebasis._symboltable;

import de.monticore.cd.cd4codebasis._ast.*;
import de.monticore.cd.cdbasis._ast.ASTCDDirectModifier;

import java.util.Arrays;
import java.util.List;

public class SymModifier
    extends de.monticore.cd.cdbasis._symboltable.SymModifier {

  private boolean isPrivate;
  private boolean isProtected;
  private boolean isPublic;
  private boolean isDerived;
  private boolean isReadOnly;

  protected void resetVisibility(boolean reset) {
    if (reset) {
      this.isPrivate = false;
      this.isProtected = false;
      this.isPublic = false;
    }
  }

  public boolean isPrivate() {
    return isPrivate;
  }

  public void setPrivate(boolean isPrivate) {
    resetVisibility(isPrivate);
    this.isPrivate = isPrivate;
  }

  public boolean isProtected() {
    return isProtected;
  }

  public void setProtected(boolean isProtected) {
    resetVisibility(isProtected);
    this.isProtected = isProtected;
  }

  public boolean isPublic() {
    return isPublic;
  }

  public void setPublic(boolean isPublic) {
    resetVisibility(isPublic);
    this.isPublic = isPublic;
  }

  public boolean isDerived() {
    return isDerived;
  }

  public void setDerived(boolean isDerived) {
    this.isDerived = isDerived;
  }

  public boolean isReadOnly() {
    return isReadOnly;
  }

  public void setReadOnly(boolean readOnly) {
    isReadOnly = readOnly;
  }

  @Override
  public de.monticore.cd.cd4codebasis._symboltable.SymModifier addModifier(ASTCDDirectModifier... modifier) {
    final List<ASTCDDirectModifier> directModifier = Arrays.asList(modifier);
    directModifier.forEach(this::addModifier);
    return this;
  }

  public SymModifier addModifier(ASTCD4CodePrivateModifier modifier) {
    this.setPrivate(true);
    return this;
  }

  public SymModifier addModifier(ASTCD4CodeProtectedModifier modifier) {
    this.setProtected(true);
    return this;
  }

  public SymModifier addModifier(ASTCD4CodePublicModifier modifier) {
    this.setPublic(true);
    return this;
  }

  public SymModifier addModifier(ASTCD4CodeDerivedModifier modifier) {
    this.setDerived(true);
    return this;
  }

  public SymModifier addModifier(ASTCD4CodeReadOnlyModifier modifier) {
    this.setReadOnly(true);
    return this;
  }
}
