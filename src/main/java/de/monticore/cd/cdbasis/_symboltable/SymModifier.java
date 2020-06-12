/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdbasis._symboltable;

import de.monticore.cd.cd4codebasis._symboltable.CD4CodeSymModifier;
import de.monticore.cd.cdbasis._ast.*;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;

import java.util.Arrays;
import java.util.List;

public class SymModifier {
  protected boolean isAbstract;
  protected boolean isFinal;
  protected boolean isStatic;
  protected ASTStereotype stereotype;

  /**
   * The SymModifier can't be constructed directly,
   * use {@link SymModifierBuilder}
   */
  protected SymModifier() {
  }

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

  public void setStereotype(ASTStereotype stereotype) {
    this.stereotype = stereotype;
  }

  public SymModifier addModifier(ASTCDModifier... modifier) {
    final List<ASTCDModifier> directModifier = Arrays.asList(modifier);
    directModifier.forEach(this::addModifier);
    return this;
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

  public SymModifier addModifier(ASTCDStereotype stereotype) {
    return addModifier(stereotype.getStereotype());
  }

  public SymModifier addModifier(ASTStereotype stereotype) {
    this.stereotype.addAllValues(stereotype.getValueList());
    return this;
  }

  public SymModifier addModifier(ASTStereoValue value) {
    this.stereotype.addValue(value);
    return this;
  }
}
