/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd.cdbasis._visitor;

import de.monticore.cd.cdbasis.CDBasisMill;
import de.monticore.cd.cdbasis._ast.*;
import de.monticore.cd.cdbasis._symboltable.SymModifier;
import de.monticore.cd.cdbasis._symboltable.SymModifierBuilder;

import java.util.List;

public class SymModifierVisitor implements CDBasisVisitor {
  protected SymModifierBuilder builder;

  public SymModifierVisitor() {
    init();
  }

  public SymModifierVisitor(SymModifierBuilder builder) {
    this.builder = builder;
  }

  private void init() {
    this.builder = CDBasisMill.symModifierBuilder();
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
  public void visit(ASTCDStereotype node) {
    builder.addStereotype(node);
  }

  @Override
  public void visit(ASTCDAbstractModifier node) {
    builder.setAbstract(true);
  }

  @Override
  public void visit(ASTCDFinalModifier node) {
    builder.setFinal(true);
  }

  @Override
  public void visit(ASTCDStaticModifier node) {
    builder.setStatic(true);
  }

  public SymModifierVisitor visitAll(List<ASTCDModifier> modifier) {
    modifier.forEach(this::visit);
    return this;
  }
}
