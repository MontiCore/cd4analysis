/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation._visitor;

import de.monticore.cdassociation._ast.ASTCDAssocTypeAssoc;
import de.monticore.cdassociation._ast.ASTCDAssocTypeComp;
import de.monticore.cdassociation._symboltable.SymAssociationBuilder;

public class CDAssocTypeForSymAssociationVisitor
    implements CDAssociationVisitor2 {
  protected SymAssociationBuilder symAssociation;


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
