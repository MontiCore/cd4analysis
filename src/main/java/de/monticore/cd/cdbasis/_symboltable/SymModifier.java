package de.monticore.cd.cdbasis._symboltable;

import de.monticore.cd.cdbasis._ast.ASTCDAbstractModifier;
import de.monticore.cd.cdbasis._ast.ASTCDDirectModifier;
import de.monticore.cd.cdbasis._ast.ASTCDFinalModifier;
import de.monticore.cd.cdbasis._ast.ASTCDStaticModifier;

import java.util.Arrays;
import java.util.List;

public class SymModifier {
  private boolean isAbstract;
  private boolean isFinal;
  private boolean isStatic;

  protected void resetExtensibility(boolean reset) {
    if (reset) {
      this.isAbstract = false;
      this.isFinal = false;
    }
  }

  public boolean isAbstract() {
    return isAbstract;
  }

  public void setAbstract(boolean isAbstract) {
    resetExtensibility(isAbstract);
    this.isAbstract = isAbstract;
  }

  public boolean isFinal() {
    return isFinal;
  }

  public void setFinal(boolean isFinal) {
    resetExtensibility(isFinal);
    this.isFinal = isFinal;
  }

  public boolean isStatic() {
    return isStatic;
  }

  public void setStatic(boolean isStatic) {
    this.isStatic = isStatic;
  }

  public SymModifier addModifier(ASTCDDirectModifier... modifier) {
    final List<ASTCDDirectModifier> directModifier = Arrays.asList(modifier);
    directModifier.forEach(this::addModifier);
    return this;
  }

  public SymModifier addModifier(ASTCDAbstractModifier modifier) {
    this.setAbstract(true);
    return this;
  }

  public SymModifier addModifier(ASTCDFinalModifier modifier) {
    this.setFinal(true);
    return this;
  }

  public SymModifier addModifier(ASTCDStaticModifier modifier) {
    this.setStatic(true);
    return this;
  }
}
