/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cd4codebasis._visitor;

import de.monticore.cd.cd4code._visitor.CD4CodeVisitor;
import de.monticore.cd.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd.cd4codebasis._ast.*;
import de.monticore.cd.cd4codebasis._symboltable.CD4CodeSymModifierBuilder;
import de.monticore.cd.cdbasis._symboltable.SymModifier;
import de.monticore.cd.cdbasis._symboltable.SymModifierBuilder;
import de.monticore.cd.cdbasis._visitor.SymModifierVisitor;

public class CD4CodeSymModifierVisitor extends SymModifierVisitor
    implements CD4CodeVisitor {
  public CD4CodeSymModifierVisitor() {
    super();
  }

  public CD4CodeSymModifierVisitor(CD4CodeSymModifierBuilder builder) {
    super(builder);
  }

  private void init() {
    this.builder = CD4CodeBasisMill.symModifierBuilder();
  }

  public void reset() {
    init();
  }

  public void reset(SymModifierBuilder builder) {
    this.builder = builder;
  }

  public SymModifierBuilder getBuilder() {
    return this.builder;
  }

  public SymModifier build() {
    return this.builder.build();
  }

  @Override
  public void visit(ASTCD4CodePrivateModifier node) {
    if (this.builder instanceof CD4CodeSymModifierBuilder) {
      ((CD4CodeSymModifierBuilder) this.builder).setPrivate(true);
    }
  }

  @Override
  public void visit(ASTCD4CodeProtectedModifier node) {
    if (this.builder instanceof CD4CodeSymModifierBuilder) {
      ((CD4CodeSymModifierBuilder) this.builder).setProtected(true);
    }
  }

  @Override
  public void visit(ASTCD4CodePublicModifier node) {
    if (this.builder instanceof CD4CodeSymModifierBuilder) {
      ((CD4CodeSymModifierBuilder) this.builder).setPublic(true);
    }
  }

  @Override
  public void visit(ASTCD4CodeDerivedModifier node) {
    if (this.builder instanceof CD4CodeSymModifierBuilder) {
      ((CD4CodeSymModifierBuilder) this.builder).setDerived(true);
    }
  }

  @Override
  public void visit(ASTCD4CodeReadOnlyModifier node) {
    if (this.builder instanceof CD4CodeSymModifierBuilder) {
      ((CD4CodeSymModifierBuilder) this.builder).setReadOnly(true);
    }
  }
}
