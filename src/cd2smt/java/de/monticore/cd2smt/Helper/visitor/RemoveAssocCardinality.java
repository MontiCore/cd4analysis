/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.Helper.visitor;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._visitor.CDAssociationVisitor2;

public class RemoveAssocCardinality implements CDAssociationVisitor2 {
  @Override
  public void visit(ASTCDAssociation node) {
    node.getLeft().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
    node.getRight().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
  }
}
