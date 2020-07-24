/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._visitor;

import de.monticore.cdassociation._ast.ASTCDAssocTypeAssoc;
import de.monticore.cdassociation._ast.ASTCDAssocTypeComp;
import de.monticore.cdassociation._symboltable.SymAssociationBuilder;

public class CDAssocTypeForSymAssociationVisitor
    implements CDAssociationVisitor {
  protected SymAssociationBuilder symAssociation;

  private CDAssocTypeForSymAssociationVisitor realThis;

  public CDAssocTypeForSymAssociationVisitor() {
    setRealThis(this);
  }

  @Override
  public CDAssocTypeForSymAssociationVisitor getRealThis() {
    return realThis;
  }

  public void setRealThis(CDAssocTypeForSymAssociationVisitor realThis) {
    this.realThis = realThis;
  }

  public SymAssociationBuilder getSymAssociation() {
    return symAssociation;
  }

  public void setSymAssociation(SymAssociationBuilder symAssociation) {
    this.symAssociation = symAssociation;
  }

  @Override
  public void visit(ASTCDAssocTypeAssoc node) {
    this.symAssociation.setIsAssociation(true);
  }

  @Override
  public void visit(ASTCDAssocTypeComp node) {
    this.symAssociation.setIsComposition(true);
  }
}
